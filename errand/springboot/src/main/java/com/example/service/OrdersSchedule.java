package com.example.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.entity.Orders;
import com.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class OrdersSchedule {
    private static Logger log = LoggerFactory.getLogger(OrdersSchedule.class);

    @Resource
    OrdersService ordersService;

    @Resource
    UserService userService;

    @Scheduled(fixedRate = 60000)
    public void task() {
        log.info("========================订单扫描任务开始========================");
        Orders params = new Orders();
        params.setStatus(OrderStatusEnum.NO_ACCEPT.getValue());
        List<Orders> ordersList = ordersService.selectAll(params);
        for (Orders orders : ordersList) {
            String time = orders.getTime();
            DateTime dateTime = DateUtil.parseDateTime(time);
            long seconds = DateUtil.between(dateTime, new Date(), DateUnit.SECOND);// 计算下单的时间跟当前时间的间隔秒数
            if (seconds >= 300) {  // 超过了5分钟   自动取消
                orders.setStatus(OrderStatusEnum.CANCEL.getValue());
                ordersService.updateById(orders);

                // 取消订单后需要归还用户金额
                User user = userService.selectById(orders.getUserId());
                user.setAccount(user.getAccount().add(BigDecimal.valueOf(orders.getPrice())));
                userService.updateById(user);
            }
        }

        log.info("========================订单扫描任务结束========================");
    }

}
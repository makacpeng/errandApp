package com.example.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Orders;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.font.FontRenderContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单信息业务处理
 **/

@Service
public class OrdersService {

    @Resource
    UserService userService;

    @Resource
    private OrdersMapper ordersMapper;


    /**
     * 新增
     */
    public void add(Orders orders) {
        ordersMapper.insert(orders);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        List<Orders> ordersList = ordersMapper.selectAll(orders);
        for (Orders o : ordersList) {
            String time = o.getTime();
            Date date = new Date();
            int range = (int) DateUtil.between(DateUtil.parseDateTime(time), date, DateUnit.MINUTE);
            o.setRange(range);
        }
        return ordersList;
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    public void addOrder(Orders orders) {
        Account currentUser = TokenUtils.getCurrentUser();
        BigDecimal account = currentUser.getAccount();
        Double price = orders.getPrice();
        if (price > account.doubleValue()) {
            throw new CustomException(ResultCodeEnum.ACCOUNT_LIMIT_ERROR);
        }
        // 更新账户余额
        currentUser.setAccount(account.subtract(BigDecimal.valueOf(price)));
        userService.updateById((User) currentUser);

        orders.setUserId(currentUser.getId());
        orders.setOrderNo(IdUtil.getSnowflakeNextIdStr());  // 设置唯一的订单编号
        orders.setStatus(OrderStatusEnum.NO_ACCEPT.getValue());
        orders.setTime(DateUtil.now());
        ordersMapper.insert(orders);

    }


}
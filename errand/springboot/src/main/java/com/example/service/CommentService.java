package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.Constants;
import com.example.common.enums.OrderStatusEnum;
import com.example.entity.Comment;
import com.example.entity.Orders;
import com.example.mapper.CommentMapper;
import com.example.utils.RedisUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评价表业务处理
 **/
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private OrdersService ordersService;

    /**
     * 新增
     */
    @Transactional
    public void add(Comment comment) {
        comment.setTime(DateUtil.now());  // 设置当前的最新的时间
        commentMapper.insert(comment);
        // 更新订单状态
        Integer orderId = comment.getOrderId();
        Orders orders = ordersService.selectById(orderId);
        orders.setStatus(OrderStatusEnum.DONE.getValue());  // 已完成
        ordersService.updateById(orders);
        this.setCache(comment);
    }

    // 更新用户的缓存评论
    public void setCache(Comment comment) {
        // 查询出当前用户的所有评论信息
        List<Comment> commentList = commentMapper.selectUserComment(comment.getUserId());
        List<Comment> acceptCommentList = commentMapper.selectAcceptComment(comment.getAcceptId());
        RedisUtils.setCacheObject(Constants.REDIS_COMMENT_KEY + comment.getUserId(), commentList);  //设置用户缓存
        RedisUtils.setCacheObject(Constants.REDIS_COMMENT_KEY + comment.getAcceptId(), acceptCommentList);  //设置骑手缓存
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        Comment comment = this.selectById(id);
        commentMapper.deleteById(id);
        // 删除之后设置评价信息
        this.setCache(comment);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }
    /**
     * 查询用户或骑手的评价信息
     */
    public List<Comment> selectComment(Integer userId) {
        // 先查询缓存
        List<Comment> commentList = RedisUtils.getCacheObject(Constants.REDIS_COMMENT_KEY + userId);
        if (CollUtil.isEmpty(commentList)) {  // 以防万一  万一缓存没查询到  从数据库查询下保底
            //  从数据库查询所有的评论信息
            commentList = commentMapper.selectComment(userId);
            for (Comment comment : commentList) {
                this.setCache(comment);  // 设置缓存
            }
        }
        return commentList;
    }
}

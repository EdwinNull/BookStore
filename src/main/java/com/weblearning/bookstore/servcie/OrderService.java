package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.OrderDetails;
import com.weblearning.bookstore.pojo.OrderRequest;
import com.weblearning.bookstore.pojo.PageBean;

import java.util.List;

public interface OrderService {
    void addOrder(OrderRequest order);

    Order getOrder(Integer orderId);

    OrderDetailResponse getOrderDetail(Integer orderId);

    // 获取用户的订单列表（分页）
    PageBean<Order> getUserOrderList(Integer userId, Integer pageNum, Integer pageSize);

    // 获取所有订单列表（管理员用，分页）
    PageBean<Order> getAllOrderList(Integer pageNum, Integer pageSize, String status);

    // ==================== 阶段三：订单确认收货相关 ====================

    /**
     * 用户确认收货
     * 确认收货后，将订单状态更新为delivered，并累加用户消费金额
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     */
    void confirmReceive(Integer orderId, Integer userId);
}

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
}

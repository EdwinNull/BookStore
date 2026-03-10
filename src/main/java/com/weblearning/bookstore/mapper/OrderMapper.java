package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.DTO.OrderItemResponse;
import com.weblearning.bookstore.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    void addOrder(Integer userId, Double finalPrice, java.time.LocalDateTime orderDate, String status);

    Integer getLastInsertId();

    void addOrderDetails(Integer orderId, Integer bookId, Integer quantity, Double price);

    OrderDetailResponse getById(Integer orderId);

    List<OrderItemResponse> getItemsByOrderId(Integer orderId);

    Order findById(Integer orderId);

    // 根据用户ID查询订单列表
    List<Order> findByUserId(Integer userId);

    // 获取所有订单列表（带状态筛选）
    List<Order> findAllOrders(String status);

    void updateStatus(Integer orderId, String status);
}

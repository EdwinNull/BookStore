package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.DTO.OrderItemResponse;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.OrderDetails;
import com.weblearning.bookstore.pojo.OrderRequest;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    void addOrder(Integer userId, Double finalPrice, LocalDateTime orderDate, String status);

    Order findById(Integer orderId);

    void updateStatus(Integer orderId, String status);

    void addOrderDetails(Integer orderId, Integer bookId, Integer quantity, Double price);

    Integer getLastInsertId();


    OrderDetailResponse getById(Integer orderId);

    List<OrderItemResponse> getItemsByOrderId(Integer orderId);
}

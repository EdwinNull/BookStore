package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.OrderDetails;
import com.weblearning.bookstore.pojo.OrderRequest;

import java.util.List;

public interface OrderService {
    void addOrder(OrderRequest order);

    Order getOrder(Integer orderId);


    OrderDetailResponse getOrderDetail(Integer orderId);
}

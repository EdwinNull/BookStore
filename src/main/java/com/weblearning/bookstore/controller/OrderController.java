package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.OrderDetails;
import com.weblearning.bookstore.pojo.OrderRequest;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.servcie.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/add")
    public Result addOrder(@RequestBody OrderRequest order) {
        orderService.addOrder(order);
        return Result.success();
    }

    @GetMapping("/get/{orderId}")
    public Result getOrder(@PathVariable("orderId") Integer orderId) {
        Order order = orderService.getOrder(orderId);
        return Result.success(order);
    }

    @GetMapping("/detail/{orderId}")
    public Result<OrderDetailResponse> getOrderDetail(@PathVariable("orderId") Integer orderId) {
        OrderDetailResponse orderDetails = orderService.getOrderDetail(orderId);
        return Result.success(orderDetails);
    }
}

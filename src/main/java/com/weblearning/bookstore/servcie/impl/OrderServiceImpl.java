package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.DTO.OrderItemResponse;
import com.weblearning.bookstore.mapper.BookMapper;
import com.weblearning.bookstore.mapper.OrderMapper;
import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.*;
import com.weblearning.bookstore.servcie.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;

    @Override
    public void addOrder(OrderRequest order) {
        Integer userId = order.getUserId();
        User user = userMapper.findById(userId);
        //用户相关信息
        Double discount = user.getDiscount();
        Double overBalance = user.getOverBalance();
        Double accountBalance = user.getAccountBalance();
        //订单相关信息
        Double totalPrice = 0.00;
        for(OrderDetails item: order.getItems()){
            Integer bookId = item.getBookId();
            Books book = bookMapper.findById(bookId);
            Integer stockQuantity = book.getStockQuantity();
            if(stockQuantity < item.getQuantity()){
                throw new RuntimeException("库存不足");
            }
            book.setStockQuantity(stockQuantity - item.getQuantity());
            bookMapper.updateQuantity(book.getStockQuantity(),bookId);
            totalPrice += item.getPrice() * item.getQuantity();
        }
        Double finalPrice = totalPrice * (1 - discount);
        if(finalPrice > accountBalance + overBalance){
            throw new RuntimeException("余额不足");
        }
        Double n = accountBalance - finalPrice;
        if(n< 0 && overBalance != 0){
            user.setOverBalance(finalPrice - accountBalance);
            user.setAccountBalance(0.00);
            userMapper.updateCreditLevel(userId,1,0.10);
        }
        else{
            userMapper.addBalance(userId,-finalPrice);
            if(n < 500){
                userMapper.updateCreditLevel(userId,1,0.10);
            }
            if(n >= 500 && n < 1000){
                userMapper.updateCreditLevel(userId,2,0.15);
            }
            if(n >= 1000 && n < 2000){
                userMapper.updateCreditLevel(userId,3,0.15);
                userMapper.updateOverBalance(userId,100.00);
            }
            if(n >= 2000 && n < 5000){
                userMapper.updateCreditLevel(userId,4,0.20);
                userMapper.updateOverBalance(userId,200.00);
            }
            if(n >= 5000){
                userMapper.updateCreditLevel(userId,5,0.25);
                userMapper.updateOverBalance(userId,500.00);
            }
        }
        String status = "待发货";
        orderMapper.addOrder(userId,finalPrice,LocalDateTime.now(),status);
        Integer orderId = orderMapper.getLastInsertId();
        for(OrderDetails item: order.getItems()){
            orderMapper.addOrderDetails(orderId,item.getBookId(),item.getQuantity(),item.getPrice());
        }
    }

    @Override
    public Order getOrder(Integer orderId) {
        Order order = orderMapper.findById(orderId);
        return order;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Integer orderId) {
        OrderDetailResponse orderDetails = orderMapper.getById(orderId);
        List<OrderItemResponse> items = orderMapper.getItemsByOrderId(orderId);
        orderDetails.setItems(items);
        return orderDetails;
    }
}

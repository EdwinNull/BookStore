package com.weblearning.bookstore.pojo;

import java.time.LocalDateTime;

/**
 * 订单实体类
 * 包含订单基本信息及关联的用户名（用于列表显示）
 */
public class Order {
    private Integer orderId;
    private Integer userId;
    private String username;       // 用户名，用于前端列表显示
    private Double totalPrice;
    private LocalDateTime orderDate;
    private String status;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

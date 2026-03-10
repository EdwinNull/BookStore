package com.weblearning.bookstore.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情响应DTO
 * 用于返回订单的完整信息，包含用户信息和订单项列表
 */
public class OrderDetailResponse {
    // 用户相关字段
    private int userId;
    private String username;      // 用户名，用于前端显示
    private String realName;      // 用户真实姓名

    // 订单基本信息
    private BigDecimal finalPrice;
    private String status;
    private LocalDateTime orderDate;  // 修复：使用LocalDateTime替代Timestamp

    // 订单项列表
    private List<OrderItemResponse> items;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}

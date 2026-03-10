package com.weblearning.bookstore.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Integer userId;
    private String username;
    private String password;
    private String name;
    private String address;
    private Double accountBalance;
    private Integer CreditLevel;
    private String role;
    private Double discount;
    private Double overBalance;

    /**
     * 累计消费金额（用于计算会员等级折扣）
     */
    private BigDecimal totalSpent;

    /**
     * 折扣更新时间
     */
    private LocalDateTime discountUpdatedAt;

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getOverBalance() {
        return overBalance;
    }

    public void setOverBalance(Double overBalance) {
        this.overBalance = overBalance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Integer getCreditLevel() {
        return CreditLevel;
    }

    public void setCreditLevel(Integer creditLevel) {
        CreditLevel = creditLevel;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // ==================== 累计消费金额相关 ====================

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public LocalDateTime getDiscountUpdatedAt() {
        return discountUpdatedAt;
    }

    public void setDiscountUpdatedAt(LocalDateTime discountUpdatedAt) {
        this.discountUpdatedAt = discountUpdatedAt;
    }
}


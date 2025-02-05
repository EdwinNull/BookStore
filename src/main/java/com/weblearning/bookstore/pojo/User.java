package com.weblearning.bookstore.pojo;

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
}


package com.weblearning.bookstore.pojo;

import java.util.List;

public class OrderRequest {
    private Integer userId;
    List<OrderDetails> items;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<OrderDetails> getItems() {
        return items;
    }

    public void setItems(List<OrderDetails> items) {
        this.items = items;
    }
}


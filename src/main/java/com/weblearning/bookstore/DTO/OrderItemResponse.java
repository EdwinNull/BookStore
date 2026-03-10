package com.weblearning.bookstore.DTO;

import java.math.BigDecimal;

/**
 * 订单项响应DTO
 * 用于返回订单中单个商品的详细信息
 */
public class OrderItemResponse {
    // 图书基本信息
    private int bookId;
    private String bookTitle;     // 图书名称，用于前端显示
    private String coverImage;    // 封面图片路径

    // 订单项信息
    private int quantity;
    private BigDecimal price;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

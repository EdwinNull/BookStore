package com.weblearning.bookstore.pojo;

import java.time.LocalDateTime;

public class MissingBooks {
    private Integer missingBooksId;
    private Integer bookId;
    private String bookName;
    private Integer supplierId;
    private Integer quantity;
    private LocalDateTime registrationDate;
    private Integer userId;
    private String publisher;
    private Integer requestedQuantity;

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getMissingBooksId() {
        return missingBooksId;
    }

    public void setMissingBooksId(Integer missingBooksId) {
        this.missingBooksId = missingBooksId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getRegistrationDate(LocalDateTime now) {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

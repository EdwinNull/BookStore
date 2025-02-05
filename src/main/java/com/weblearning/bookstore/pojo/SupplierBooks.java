package com.weblearning.bookstore.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SupplierBooks {
    private Integer supplierBookId;
    private Integer supplierName;
    private Integer bookName;
    private Integer quantity;
    private Double price;


    public Integer getSupplierBookId() {
        return supplierBookId;
    }

    public void setSupplierBookId(Integer supplierBookId) {
        this.supplierBookId = supplierBookId;
    }

    public Integer getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(Integer supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getBookName() {
        return bookName;
    }

    public void setBookName(Integer bookName) {
        this.bookName = bookName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

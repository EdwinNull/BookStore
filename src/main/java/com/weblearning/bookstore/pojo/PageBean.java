package com.weblearning.bookstore.pojo;

import java.util.List;

//分页返回结果对象
public class PageBean<T>{
    private Long total;//总条数
    private List<T> items;//当前页数据集合

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public PageBean(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageBean() {
    }
}



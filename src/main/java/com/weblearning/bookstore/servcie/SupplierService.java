package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Suppliers;

public interface SupplierService {
    void addSupplier(Suppliers supplier);

    void updateSupplier(Suppliers supplier);

    void deleteSupplier(Integer id);


    Suppliers findByName(String name);
}

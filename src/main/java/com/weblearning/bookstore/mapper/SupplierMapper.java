package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Suppliers;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SupplierMapper {
    void addSupplier(Suppliers supplier);

    void updateSupplier(Suppliers supplier);

    void deleteSupplier(Integer id);


    Suppliers findByName(String name);
}

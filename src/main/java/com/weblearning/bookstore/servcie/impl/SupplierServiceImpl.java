package com.weblearning.bookstore.servcie.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.weblearning.bookstore.mapper.SupplierMapper;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierService;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public void addSupplier(Suppliers supplier) {
        supplierMapper.addSupplier(supplier);
    }

    @Override
    public void updateSupplier(Suppliers supplier) {
        supplierMapper.updateSupplier(supplier);
    }

    @Override
    public void deleteSupplier(Integer id) {
        supplierMapper.deleteSupplier(id);
    }

    @Override
    public Suppliers findByName(String name) {
        Suppliers supplier = supplierMapper.findByName(name);
        return supplier;
    }


}

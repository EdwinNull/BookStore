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

/**
 * 供应商服务实现类
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public void addSupplier(Suppliers supplier) {
        // 如果没有设置状态，默认为 active（管理员直接添加）
        if (supplier.getStatus() == null || supplier.getStatus().isEmpty()) {
            supplier.setStatus("active");
        }
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
        return supplierMapper.findByName(name);
    }

    @Override
    public List<Suppliers> findAll() {
        return supplierMapper.findAll();
    }

    @Override
    public List<Suppliers> findByStatus(String status) {
        return supplierMapper.findByStatus(status);
    }

    @Override
    public void updateStatus(Integer id, String status) {
        supplierMapper.updateStatus(id, status);
    }

    @Override
    public Suppliers findById(Integer id) {
        return supplierMapper.findById(id);
    }
}

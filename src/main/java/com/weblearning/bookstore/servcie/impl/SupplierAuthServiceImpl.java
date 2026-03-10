package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.dto.SupplierProfileResponse;
import com.weblearning.bookstore.dto.SupplierRegisterRequest;
import com.weblearning.bookstore.mapper.SupplierMapper;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierAuthService;
import com.weblearning.bookstore.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供应商认证服务实现类
 * 提供供应商的登录、注册、信息管理等功能
 */
@Service
public class SupplierAuthServiceImpl implements SupplierAuthService {

    @Autowired
    private SupplierMapper supplierMapper;

    /**
     * 供应商注册申请
     * 注册后状态默认为 pending，需要管理员审核
     */
    @Override
    public void register(SupplierRegisterRequest request) {
        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建供应商实体
        Suppliers supplier = new Suppliers();
        supplier.setUsername(request.getUsername());
        // 密码MD5加密
        supplier.setPassword(Md5Util.getMD5String(request.getPassword()));
        supplier.setName(request.getName());
        supplier.setAddress(request.getAddress());
        supplier.setContactInfo(request.getContactInfo());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        // 默认状态为待审核
        supplier.setStatus("pending");

        supplierMapper.addSupplier(supplier);
    }

    /**
     * 供应商登录验证
     */
    @Override
    public Suppliers login(String username, String password) {
        Suppliers supplier = supplierMapper.findByUsername(username);
        if (supplier == null) {
            return null;
        }

        // 验证密码
        if (!Md5Util.checkPassword(password, supplier.getPassword())) {
            return null;
        }

        // 检查状态
        if (!"active".equals(supplier.getStatus())) {
            throw new RuntimeException("账号未激活或已被停用，请联系管理员");
        }

        return supplier;
    }

    /**
     * 根据用户名查询供应商
     */
    @Override
    public Suppliers findByUsername(String username) {
        return supplierMapper.findByUsername(username);
    }

    /**
     * 根据ID查询供应商
     */
    @Override
    public Suppliers findById(Integer supplierId) {
        return supplierMapper.findById(supplierId);
    }

    /**
     * 获取供应商信息（不含密码）
     */
    @Override
    public SupplierProfileResponse getProfile(Integer supplierId) {
        Suppliers supplier = supplierMapper.findById(supplierId);
        if (supplier == null) {
            return null;
        }

        // 转换为响应DTO（不包含密码）
        SupplierProfileResponse response = new SupplierProfileResponse();
        response.setSupplierId(supplier.getSupplierId());
        response.setUsername(supplier.getUsername());
        response.setName(supplier.getName());
        response.setAddress(supplier.getAddress());
        response.setContactInfo(supplier.getContactInfo());
        response.setContactPerson(supplier.getContactPerson());
        response.setPhone(supplier.getPhone());
        response.setEmail(supplier.getEmail());
        response.setStatus(supplier.getStatus());

        return response;
    }

    /**
     * 更新供应商信息
     */
    @Override
    public void updateProfile(Suppliers supplier) {
        supplierMapper.updateSupplier(supplier);
    }

    /**
     * 修改密码
     */
    @Override
    public boolean updatePassword(Integer supplierId, String oldPwd, String newPwd) {
        Suppliers supplier = supplierMapper.findById(supplierId);
        if (supplier == null) {
            return false;
        }

        // 验证旧密码
        if (!Md5Util.checkPassword(oldPwd, supplier.getPassword())) {
            return false;
        }

        // 更新新密码
        supplierMapper.updatePassword(supplierId, Md5Util.getMD5String(newPwd));
        return true;
    }

    /**
     * 审核供应商（管理员操作）
     */
    @Override
    public void auditSupplier(Integer supplierId, String status) {
        if (!"active".equals(status) && !"inactive".equals(status)) {
            throw new RuntimeException("无效的状态");
        }
        supplierMapper.updateStatus(supplierId, status);
    }

    /**
     * 获取待审核供应商列表
     */
    @Override
    public List<Suppliers> getPendingSuppliers() {
        return supplierMapper.findByStatus("pending");
    }

    /**
     * 获取所有供应商列表
     */
    @Override
    public List<Suppliers> getAllSuppliers() {
        return supplierMapper.findAll();
    }

    /**
     * 检查用户名是否已存在
     */
    @Override
    public boolean isUsernameExists(String username) {
        return supplierMapper.checkUsernameExists(username) > 0;
    }
}

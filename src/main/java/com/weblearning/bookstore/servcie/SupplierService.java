package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Suppliers;

import java.util.List;

/**
 * 供应商服务接口
 */
public interface SupplierService {

    /**
     * 添加供应商
     * @param supplier 供应商信息
     */
    void addSupplier(Suppliers supplier);

    /**
     * 更新供应商信息
     * @param supplier 供应商信息
     */
    void updateSupplier(Suppliers supplier);

    /**
     * 删除供应商
     * @param id 供应商ID
     */
    void deleteSupplier(Integer id);

    /**
     * 根据名称查询供应商
     * @param name 供应商名称
     * @return 供应商信息
     */
    Suppliers findByName(String name);

    /**
     * 获取所有供应商列表
     * @return 供应商列表
     */
    List<Suppliers> findAll();

    /**
     * 根据状态获取供应商列表
     * @param status 状态
     * @return 供应商列表
     */
    List<Suppliers> findByStatus(String status);

    /**
     * 更新供应商状态
     * @param id 供应商ID
     * @param status 状态
     */
    void updateStatus(Integer id, String status);

    Suppliers findById(Integer id);
}

package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Suppliers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商数据访问接口
 * 提供供应商的CRUD操作和认证相关查询
 */
@Mapper
public interface SupplierMapper {

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
     * 根据用户名查询供应商（用于登录认证）
     * @param username 用户名
     * @return 供应商信息
     */
    Suppliers findByUsername(String username);

    /**
     * 根据ID查询供应商
     * @param supplierId 供应商ID
     * @return 供应商信息
     */
    Suppliers findById(Integer supplierId);

    /**
     * 更新供应商状态（审核通过/拒绝/停用）
     * @param supplierId 供应商ID
     * @param status 状态：active/inactive/pending
     */
    void updateStatus(@Param("supplierId") Integer supplierId, @Param("status") String status);

    /**
     * 更新供应商密码
     * @param supplierId 供应商ID
     * @param password 新密码（MD5加密后）
     */
    void updatePassword(@Param("supplierId") Integer supplierId, @Param("password") String password);

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
     * 检查用户名是否存在
     * @param username 用户名
     * @return 存在返回1，否则返回0
     */
    int checkUsernameExists(String username);
}

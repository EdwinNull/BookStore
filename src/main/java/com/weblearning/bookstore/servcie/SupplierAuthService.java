package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.dto.SupplierProfileResponse;
import com.weblearning.bookstore.dto.SupplierRegisterRequest;
import com.weblearning.bookstore.pojo.Suppliers;

import java.util.List;

/**
 * 供应商认证服务接口
 * 提供供应商的登录、注册、信息管理等功能
 */
public interface SupplierAuthService {

    /**
     * 供应商注册申请
     * 注册后状态默认为 pending，需要管理员审核
     * @param request 注册请求
     */
    void register(SupplierRegisterRequest request);

    /**
     * 供应商登录验证
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功返回供应商信息，失败返回null
     */
    Suppliers login(String username, String password);

    /**
     * 根据用户名查询供应商
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
     * 获取供应商信息（不含密码）
     * @param supplierId 供应商ID
     * @return 供应商信息响应
     */
    SupplierProfileResponse getProfile(Integer supplierId);

    /**
     * 更新供应商信息
     * @param supplier 供应商信息
     */
    void updateProfile(Suppliers supplier);

    /**
     * 修改密码
     * @param supplierId 供应商ID
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return 成功返回true，失败返回false
     */
    boolean updatePassword(Integer supplierId, String oldPwd, String newPwd);

    /**
     * 审核供应商（管理员操作）
     * @param supplierId 供应商ID
     * @param status 状态：active/inactive
     */
    void auditSupplier(Integer supplierId, String status);

    /**
     * 获取待审核供应商列表
     * @return 待审核供应商列表
     */
    List<Suppliers> getPendingSuppliers();

    /**
     * 获取所有供应商列表
     * @return 供应商列表
     */
    List<Suppliers> getAllSuppliers();

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 存在返回true，否则返回false
     */
    boolean isUsernameExists(String username);
}

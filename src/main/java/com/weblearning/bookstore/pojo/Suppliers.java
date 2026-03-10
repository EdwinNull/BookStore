package com.weblearning.bookstore.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 供应商实体类
 * 包含供应商基本信息和登录认证信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suppliers {

    /**
     * 供应商ID（主键）
     */
    private Integer supplierId;

    /**
     * 供应商登录账号（唯一）
     */
    private String username;

    /**
     * 密码（MD5加密存储）
     */
    private String password;

    /**
     * 供应商名称
     */
    private String name;

    /**
     * 供应商地址
     */
    private String address;

    /**
     * 联系方式（备用电话等）
     */
    private String contactInfo;

    /**
     * 联系人姓名
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 状态：active-激活，inactive-停用，pending-待审核
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

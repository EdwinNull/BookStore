package com.weblearning.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 供应商信息响应 DTO
 * 用于返回供应商的基本信息（不包含敏感信息如密码）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProfileResponse {

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 供应商登录账号
     */
    private String username;

    /**
     * 供应商名称
     */
    private String name;

    /**
     * 供应商地址
     */
    private String address;

    /**
     * 联系方式（备用）
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
}

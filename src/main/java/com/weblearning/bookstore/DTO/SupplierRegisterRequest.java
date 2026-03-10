package com.weblearning.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 供应商注册请求 DTO
 * 用于接收供应商注册申请的 JSON 格式数据
 * 注册后状态默认为 pending，需要管理员审核
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRegisterRequest {

    /**
     * 供应商登录账号（5-20位）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名长度应为5-20位")
    private String username;

    /**
     * 密码（5-16位非空字符）
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^\\S{5,16}$", message = "密码长度应为5-16位非空字符")
    private String password;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 100, message = "供应商名称不能超过100个字符")
    private String name;

    /**
     * 供应商地址
     */
    @Size(max = 255, message = "地址不能超过255个字符")
    private String address;

    /**
     * 联系人姓名
     */
    @Size(max = 50, message = "联系人姓名不能超过50个字符")
    private String contactPerson;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^(1[3-9]\\d{9})?$", message = "请输入有效的手机号码")
    private String phone;

    /**
     * 邮箱地址
     */
    @Email(message = "请输入有效的邮箱地址")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    /**
     * 联系方式（备用电话、QQ等）
     */
    @Size(max = 100, message = "联系方式不能超过100个字符")
    private String contactInfo;
}

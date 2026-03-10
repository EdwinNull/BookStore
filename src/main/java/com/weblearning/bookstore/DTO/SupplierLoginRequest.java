package com.weblearning.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 供应商登录请求 DTO
 * 用于接收供应商登录的 JSON 格式数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierLoginRequest {

    /**
     * 供应商登录账号
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

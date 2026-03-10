package com.weblearning.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户注册请求 DTO
 * 用于接收前端发送的 JSON 格式注册数据
 */
public class RegisterRequest {

    /**
     * 用户名（5-16位非空字符）
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^\\S{5,16}$", message = "用户名长度应为5-16位非空字符")
    private String username;

    /**
     * 密码（5-16位非空字符）
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^\\S{5,16}$", message = "密码长度应为5-16位非空字符")
    private String password;

    // 默认构造函数
    public RegisterRequest() {}

    // 带参构造函数
    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 和 Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.weblearning.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 密码更新请求 DTO
 * 用于接收前端发送的 JSON 格式密码更新数据
 */
public class PasswordUpdateRequest {

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String old_pwd;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String new_pwd;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String re_pwd;

    // 默认构造函数
    public PasswordUpdateRequest() {}

    // 带参构造函数
    public PasswordUpdateRequest(String old_pwd, String new_pwd, String re_pwd) {
        this.old_pwd = old_pwd;
        this.new_pwd = new_pwd;
        this.re_pwd = re_pwd;
    }

    // Getter 和 Setter
    public String getOld_pwd() {
        return old_pwd;
    }

    public void setOld_pwd(String old_pwd) {
        this.old_pwd = old_pwd;
    }

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public String getRe_pwd() {
        return re_pwd;
    }

    public void setRe_pwd(String re_pwd) {
        this.re_pwd = re_pwd;
    }
}

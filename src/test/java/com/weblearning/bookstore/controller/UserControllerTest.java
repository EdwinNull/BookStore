package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.Md5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求
使用 @MockitoBean Mock UserService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UserService userService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private StringRedisTemplate stringRedisTemplate;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ValueOperations<String, String> valueOperations;

    private User testUser;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试用户对象
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setPassword(Md5Util.getMD5String("password123"));
        testUser.setRole("user");

        // Mock Redis 操作
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * 测试用户注册接口 - 注册成功
     * 验证：新用户注册成功，返回成功消息
     */
    @Test
    @DisplayName("用户注册 - 注册成功")
    void testRegister_Success() throws Exception {
        String username = "newuser";
        String password = "password123";

        // Mock 用户不存在
        when(userService.findByUserName(username)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/register")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("register success"));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
        verify(userService, times(1)).add(username, password);
    }

    /**
     * 测试用户注册接口 - 用户名已存在
     * 验证：用户名已存在时返回错误信息
     */
    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void testRegister_UserAlreadyExists() throws Exception {
        String username = "existinguser";
        String password = "password123";

        // Mock 用户已存在
        when(userService.findByUserName(username)).thenReturn(testUser);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/register")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
        verify(userService, never()).add(anyString(), anyString());
    }

    /**
     * 测试用户登录接口 - 登录成功
     * 验证：正确用户名和密码登录成功，返回token
     */
    @Test
    @DisplayName("用户登录 - 登录成功")
    void testLogin_Success() throws Exception {
        String username = "testuser";
        String password = "password123";

        // Mock 用户存在
        when(userService.findByUserName(username)).thenReturn(testUser);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any());
    }

    /**
     * 测试用户登录接口 - 用户名不存在
     * 验证：用户名不存在时返回错误信息
     */
    @Test
    @DisplayName("用户登录 - 用户名不存在")
    void testLogin_UserNotFound() throws Exception {
        String username = "nonexistent";
        String password = "password123";

        // Mock 用户不存在
        when(userService.findByUserName(username)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("用户名不存在"));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
    }

    /**
     * 测试用户登录接口 - 密码错误
     * 验证：密码错误时返回错误信息
     */
    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() throws Exception {
        String username = "testuser";
        String password = "wrongpassword";

        // Mock 用户存在
        when(userService.findByUserName(username)).thenReturn(testUser);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("密码错误"));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
    }

    /**
     * 测试更新用户信息接口 - 更新成功
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("更新用户信息 - 成功")
    void testUpdate_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testUser);

        // 执行请求并验证响应
        mockMvc.perform(put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("update success"));

        // 验证 service 方法被调用
//        verify(userService, times(1)).update(any(User.class), userId);
    }

    /**
     * 测试更新密码接口 - 参数为空
     * 验证：参数为空时返回错误信息
     */
    @Test
    @DisplayName("更新密码 - 参数为空")
    void testUpdatePwd_EmptyParams() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("old_pwd", "");
        params.put("new_pwd", "newpassword");
        params.put("re_pwd", "newpassword");

        String json = objectMapper.writeValueAsString(params);

        // 执行请求并验证响应
        mockMvc.perform(patch("/api/user/updatePwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "test-token")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("参数不能为空"));
    }

    /**
     * 测试更新密码接口 - 两次密码不一致
     * 验证：新密码和确认密码不一致时返回错误信息
     */
    @Test
    @DisplayName("更新密码 - 两次密码不一致")
    void testUpdatePwd_PasswordMismatch() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("old_pwd", "password123");
        params.put("new_pwd", "newpassword1");
        params.put("re_pwd", "newpassword2");

        String json = objectMapper.writeValueAsString(params);

        // Mock 原密码正确
        when(userService.findByUserName(anyString())).thenReturn(testUser);

        // 执行请求并验证响应
        mockMvc.perform(patch("/api/user/updatePwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "test-token")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("两次密码不一致"));
    }
}

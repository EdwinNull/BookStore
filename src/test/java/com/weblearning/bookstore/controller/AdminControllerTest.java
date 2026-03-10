package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.pojo.PageBean;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static io.lettuce.core.KillArgs.Builder.user;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdminController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock UserService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = AdminController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

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

    private User testAdmin;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试管理员对象
     */
    @BeforeEach
    void setUp() {
        testAdmin = new User();
        testAdmin.setUserId(1);
        testAdmin.setUsername("admin");
        testAdmin.setPassword(Md5Util.getMD5String("admin123"));
        testAdmin.setRole("admin");

        // Mock Redis 操作
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * 测试管理员登录接口 - 登录成功
     * 验证：正确用户名和密码登录成功，返回token
     */
    @Test
    @DisplayName("管理员登录 - 登录成功")
    void testAdminLogin_Success() throws Exception {
        String username = "admin";
        String password = "admin123";

        // Mock 用户存在
        when(userService.findByUserName(username)).thenReturn(testAdmin);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/admin/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any());
    }

    /**
     * 测试管理员登录接口 - 密码错误
     * 验证：密码错误时返回错误信息
     */
    @Test
    @DisplayName("管理员登录 - 密码错误")
    void testAdminLogin_WrongPassword() throws Exception {
        String username = "admin";
        String password = "wrongpassword";

        // Mock 用户存在
        when(userService.findByUserName(username)).thenReturn(testAdmin);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/admin/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("密码错误"));

        // 验证 service 方法被调用
        verify(userService, times(1)).findByUserName(username);
    }

    /**
     * 测试获取用户列表接口 - GET /api/admin/users
     * 验证：成功返回分页用户列表
     */
    @Test
    @DisplayName("获取用户列表 - 分页查询成功")
    void testGetUserList_Success() throws Exception {
        // 准备 Mock 数据
        PageBean<User> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testAdmin));

        when(userService.getUserList(1, 10, null, null)).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/admin/users")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items").isArray());

        // 验证 service 方法被调用
        verify(userService, times(1)).getUserList(1, 10, null, null);
    }

    /**
     * 测试获取用户列表接口 - 带用户名筛选
     * 验证：带用户名参数的查询成功
     */
    @Test
    @DisplayName("获取用户列表 - 带用户名筛选")
    void testGetUserList_WithUsernameFilter() throws Exception {
        // 准备 Mock 数据
        PageBean<User> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testAdmin));

        when(userService.getUserList(1, 10, "admin", null)).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/admin/users")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(userService, times(1)).getUserList(1, 10, "admin", null);
    }

    /**
     * 测试获取用户列表接口 - 带角色筛选
     * 验证：带角色参数的查询成功
     */
    @Test
    @DisplayName("获取用户列表 - 带角色筛选")
    void testGetUserList_WithRoleFilter() throws Exception {
        // 准备 Mock 数据
        PageBean<User> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testAdmin));

        when(userService.getUserList(1, 10, null, "admin")).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/admin/users")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(userService, times(1)).getUserList(1, 10, null, "admin");
    }

    /**
     * 测试获取所有用户列表接口 - GET /api/admin/users/all
     * 验证：成功返回所有用户列表（不分页）
     */
    @Test
    @DisplayName("获取所有用户 - 不分页查询成功")
    void testGetAllUsers_Success() throws Exception {
        // 准备 Mock 数据
        List<User> users = Arrays.asList(testAdmin);

        when(userService.getAllUsers()).thenReturn(users);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/admin/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value("admin"));

        // 验证 service 方法被调用
        verify(userService, times(1)).getAllUsers();
    }

    /**
     * 测试添加余额接口 - PUT /api/admin/addBalance
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("添加用户余额 - 成功")
    void testAddBalance_Success() throws Exception {
        Integer userId = 1;
        Double balance = 100.0;

        // 执行请求并验证响应
        mockMvc.perform(put("/api/admin/addBalance")
                        .param("userId", userId.toString())
                        .param("balance", balance.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("添加成功"));

        // 验证 service 方法被调用
        verify(userService, times(1)).addBalance(userId, balance);
    }

    /**
     * 测试添加余额接口 - 添加负数余额（扣款）
     * 验证：负数金额参数正确传递
     */
    @Test
    @DisplayName("添加用户余额 - 负数金额")
    void testAddBalance_NegativeAmount() throws Exception {
        Integer userId = 1;
        Double balance = -50.0;

        // 执行请求并验证响应
        mockMvc.perform(put("/api/admin/addBalance")
                        .param("userId", userId.toString())
                        .param("balance", balance.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(userService, times(1)).addBalance(userId, balance);
    }

    /**
     * 测试查看用户详情接口 - GET /api/admin/show/{userId}
     * 验证：成功返回用户详情
     */
    @Test
    @DisplayName("查看用户详情 - 成功")
    void testShowUser_Success() throws Exception {
        Integer userId = 1;
        // Mock 服务层返回
        when(userService.findById(userId)).thenReturn(testAdmin);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/admin/show/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"));

        // 验证 service 方法被调用
        verify(userService, times(1)).findById(userId);
    }

    /**
     * 测试查看用户详情接口 - 用户不存在
     * 验证：用户不存在时返回空数据
     */
    @Test
    @DisplayName("查看用户详情 - 用户不存在")
    void testShowUser_NotFound() throws Exception {
        Integer userId = 999;
        // Mock 服务层返回 null
        when(userService.findById(userId)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/admin/show/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(userService, times(1)).findById(userId);
    }
}

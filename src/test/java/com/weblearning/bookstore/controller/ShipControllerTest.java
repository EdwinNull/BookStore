package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Ship;
import com.weblearning.bookstore.servcie.ShipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ShipController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock ShipService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = ShipController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ShipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ShipService shipService;

    private Ship testShip;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试发货对象
     */
    @BeforeEach
    void setUp() {
        testShip = new Ship();
        testShip.setShipmentId(1);
        testShip.setOrderId(100);
        testShip.setShippingDate(LocalDateTime.now());
        testShip.setStatus("shipped");
    }

    /**
     * 测试添加发货记录接口 - POST /api/ship/add
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("添加发货记录 - 成功")
    void testAddShip_Success() throws Exception {
        Integer orderId = 100;

        // 执行请求并验证响应
        mockMvc.perform(post("/api/ship/add")
                        .param("orderId", orderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("添加成功"));

        // 验证 service 方法被调用一次
        verify(shipService, times(1)).addShip(orderId);
    }

    /**
     * 测试添加发货记录接口 - 缺少必要参数
     * 验证：缺少参数时返回400错误
     */
    @Test
    @DisplayName("添加发货记录 - 缺少订单ID参数")
    void testAddShip_MissingOrderId() throws Exception {
        // 执行请求 - 不传 orderId 参数
        mockMvc.perform(post("/api/ship/add"))
                .andExpect(status().isBadRequest());

        // 验证 service 方法未被调用
        verify(shipService, never()).addShip(anyInt());
    }

    /**
     * 测试根据订单ID获取发货记录接口 - GET /api/ship/{orderId}
     * 验证：成功返回发货记录详情
     */
    @Test
    @DisplayName("获取发货记录 - 成功")
    void testGetShip_Success() throws Exception {
        Integer orderId = 100;
        // Mock 服务层返回
        when(shipService.getShip(orderId)).thenReturn(testShip);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ship/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderId").value(100))
                .andExpect(jsonPath("$.data.status").value("shipped"));

        // 验证 service 方法被调用
        verify(shipService, times(1)).getShip(orderId);
    }

    /**
     * 测试根据订单ID获取发货记录接口 - 发货记录不存在
     * 验证：返回空数据
     */
    @Test
    @DisplayName("获取发货记录 - 发货记录不存在")
    void testGetShip_NotFound() throws Exception {
        Integer orderId = 999;
        // Mock 服务层返回 null
        when(shipService.getShip(orderId)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ship/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(shipService, times(1)).getShip(orderId);
    }

    /**
     * 测试添加发货记录接口 - 不同的订单ID
     * 验证：不同订单ID参数正确传递
     */
    @Test
    @DisplayName("添加发货记录 - 不同订单ID")
    void testAddShip_DifferentOrderId() throws Exception {
        Integer orderId = 200;

        // 执行请求并验证响应
        mockMvc.perform(post("/api/ship/add")
                        .param("orderId", orderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("添加成功"));

        // 验证 service 方法被调用，且参数正确
        verify(shipService, times(1)).addShip(200);
    }
}

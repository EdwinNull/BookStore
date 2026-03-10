package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.OrderRequest;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.servcie.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OrderController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock OrderService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = OrderController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private OrderService orderService;

    private Order testOrder;
    private OrderRequest testOrderRequest;
    private OrderDetailResponse testOrderDetail;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试订单对象
     */
    @BeforeEach
    void setUp() {
        // 初始化测试订单对象
        testOrder = new Order();
        testOrder.setOrderId(1);
        testOrder.setUserId(1);
        testOrder.setTotalPrice(199.99);
        testOrder.setStatus("pending");
        testOrder.setOrderDate(LocalDateTime.now());

        // 初始化测试订单请求对象
        testOrderRequest = new OrderRequest();
        testOrderRequest.setUserId(1);

        // 初始化测试订单详情响应对象
        testOrderDetail = new OrderDetailResponse();
        testOrderDetail.setUserId(1);
        testOrderDetail.setStatus("pending");
    }

    /**
     * 测试添加订单接口 - POST /api/order/add
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("添加订单 - 成功")
    void testAddOrder_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testOrderRequest);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/order/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用一次
        verify(orderService, times(1)).addOrder(any(OrderRequest.class));
    }

    /**
     * 测试获取订单接口 - GET /api/order/get/{orderId}
     * 验证：成功返回订单信息
     */
    @Test
    @DisplayName("获取订单 - 成功")
    void testGetOrder_Success() throws Exception {
        Integer orderId = 1;
        // Mock 服务层返回
        when(orderService.getOrder(orderId)).thenReturn(testOrder);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/get/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.status").value("pending"));

        // 验证 service 方法被调用
        verify(orderService, times(1)).getOrder(orderId);
    }

    /**
     * 测试获取订单接口 - 订单不存在
     * 验证：订单不存在时返回空数据
     */
    @Test
    @DisplayName("获取订单 - 订单不存在")
    void testGetOrder_NotFound() throws Exception {
        Integer orderId = 999;
        // Mock 服务层返回 null
        when(orderService.getOrder(orderId)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/get/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(orderService, times(1)).getOrder(orderId);
    }

    /**
     * 测试获取订单详情接口 - GET /api/order/detail/{orderId}
     * 验证：成功返回订单详情
     */
    @Test
    @DisplayName("获取订单详情 - 成功")
    void testGetOrderDetail_Success() throws Exception {
        Integer orderId = 1;
        // Mock 服务层返回
        when(orderService.getOrderDetail(orderId)).thenReturn(testOrderDetail);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/detail/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value(1));

        // 验证 service 方法被调用
        verify(orderService, times(1)).getOrderDetail(orderId);
    }

    /**
     * 测试获取用户订单列表接口 - GET /api/order/list
     * 验证：成功返回用户订单分页列表
     */
    @Test
    @DisplayName("获取用户订单列表 - 分页查询成功")
    void testGetUserOrderList_Success() throws Exception {
        // 准备 Mock 数据
        PageBean<Order> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testOrder));

        when(orderService.getUserOrderList(anyInt(), eq(1), eq(10))).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items").isArray());

        // 验证 service 方法被调用
        verify(orderService, times(1)).getUserOrderList(anyInt(), eq(1), eq(10));
    }

    /**
     * 测试获取用户订单列表接口 - 自定义分页参数
     * 验证：不同分页参数正确传递
     */
    @Test
    @DisplayName("获取用户订单列表 - 自定义分页参数")
    void testGetUserOrderList_CustomPaging() throws Exception {
        // 准备 Mock 数据
        PageBean<Order> pageBean = new PageBean<>();
        pageBean.setTotal(10L);
        pageBean.setItems(Arrays.asList(testOrder));

        when(orderService.getUserOrderList(anyInt(), eq(2), eq(20))).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/list")
                        .param("pageNum", "2")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(orderService, times(1)).getUserOrderList(anyInt(), eq(2), eq(20));
    }

    /**
     * 测试获取所有订单列表接口（管理员用） - GET /api/order/all
     * 验证：成功返回所有订单分页列表
     */
    @Test
    @DisplayName("获取所有订单列表 - 分页查询成功")
    void testGetAllOrderList_Success() throws Exception {
        // 准备 Mock 数据
        PageBean<Order> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testOrder));

        when(orderService.getAllOrderList(1, 10, null)).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/all")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items").isArray());

        // 验证 service 方法被调用
        verify(orderService, times(1)).getAllOrderList(1, 10, null);
    }

    /**
     * 测试获取所有订单列表接口 - 带状态筛选
     * 验证：带状态参数的查询成功
     */
    @Test
    @DisplayName("获取所有订单列表 - 带状态筛选")
    void testGetAllOrderList_WithStatusFilter() throws Exception {
        // 准备 Mock 数据
        PageBean<Order> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testOrder));

        when(orderService.getAllOrderList(1, 10, "pending")).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/all")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("status", "pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(orderService, times(1)).getAllOrderList(1, 10, "pending");
    }

    /**
     * 测试获取所有订单列表接口 - 按已完成状态筛选
     * 验证：不同状态参数正确传递
     */
    @Test
    @DisplayName("获取所有订单列表 - 按已完成状态筛选")
    void testGetAllOrderList_CompletedStatus() throws Exception {
        // 准备 Mock 数据
        Order completedOrder = new Order();
        completedOrder.setOrderId(2);
        completedOrder.setStatus("completed");

        PageBean<Order> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(completedOrder));

        when(orderService.getAllOrderList(1, 10, "completed")).thenReturn(pageBean);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/order/all")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("status", "completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(orderService, times(1)).getAllOrderList(1, 10, "completed");
    }
}

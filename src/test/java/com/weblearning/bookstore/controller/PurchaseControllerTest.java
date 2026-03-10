package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import com.weblearning.bookstore.servcie.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PurchaseController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock PurchaseService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = PurchaseController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private PurchaseService purchaseService;

    private Purchase testPurchase;
    private MissingBooks testMissingBooks;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试对象
     */
    @BeforeEach
    void setUp() {
        // 初始化测试采购单对象
        testPurchase = new Purchase();
        testPurchase.setPurchaseOrderId(1);
        testPurchase.setSupplierId(1);
        testPurchase.setStatus("pending");

        // 初始化测试缺书记录对象
        testMissingBooks = new MissingBooks();
        testMissingBooks.setMissingBooksId(1);
        testMissingBooks.setBookId(1);
        testMissingBooks.setQuantity(10);
    }

    /**
     * 测试直接添加缺书记录接口 - POST /purchases/add1
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("直接添加缺书记录 - 成功")
    void testAddMissStraight_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testMissingBooks);

        // 执行请求并验证响应
        mockMvc.perform(post("/purchases/add1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用一次
        verify(purchaseService, times(1)).addMissStraight(any(MissingBooks.class));
    }

    /**
     * 测试添加采购单接口 - POST /purchases/add2
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("添加采购单 - 成功")
    void testAddPurchase_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testPurchase);

        // 执行请求并验证响应
        mockMvc.perform(post("/purchases/add2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用一次
        verify(purchaseService, times(1)).addPurchase(any(Purchase.class));
    }

    /**
     * 测试更新采购单状态接口 - PUT /purchases/update
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("更新采购单状态 - 成功")
    void testUpdatePurchase_Success() throws Exception {
        Integer purchaseOrderId = 1;
        String status = "completed";

        // 执行请求并验证响应
        mockMvc.perform(put("/purchases/update")
                        .param("purchaseOrderId", purchaseOrderId.toString())
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用一次
        verify(purchaseService, times(1)).updatePurchase(purchaseOrderId, status);
    }

    /**
     * 测试根据ID获取采购单接口 - GET /purchases/{purchaseOrderId}
     * 验证：成功返回采购单详情
     */
    @Test
    @DisplayName("获取采购单详情 - 成功")
    void testGetPurchaseById_Success() throws Exception {
        Integer purchaseOrderId = 1;
        // Mock 服务层返回
        when(purchaseService.getPurchaseById(purchaseOrderId)).thenReturn(testPurchase);

        // 执行请求并验证响应
        mockMvc.perform(get("/purchases/{purchaseOrderId}", purchaseOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.purchaseOrderId").value(1));

        // 验证 service 方法被调用
        verify(purchaseService, times(1)).getPurchaseById(purchaseOrderId);
    }

    /**
     * 测试根据ID获取采购单接口 - 采购单不存在
     * 验证：返回空数据
     */
    @Test
    @DisplayName("获取采购单详情 - 采购单不存在")
    void testGetPurchaseById_NotFound() throws Exception {
        Integer purchaseOrderId = 999;
        // Mock 服务层返回 null
        when(purchaseService.getPurchaseById(purchaseOrderId)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/purchases/{purchaseOrderId}", purchaseOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(purchaseService, times(1)).getPurchaseById(purchaseOrderId);
    }

    /**
     * 测试根据ID获取缺书记录接口 - GET /purchases/missingBooks/{missingBookId}
     * 验证：成功返回缺书记录详情
     */
    @Test
    @DisplayName("获取缺书记录详情 - 成功")
    void testGetMissingBooksById_Success() throws Exception {
        Integer missingBookId = 1;
        // Mock 服务层返回
        when(purchaseService.getMissingBooksById(missingBookId)).thenReturn(testMissingBooks);

        // 执行请求并验证响应
        mockMvc.perform(get("/purchases/missingBooks/{missingBookId}", missingBookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.missingBooksId").value(1));

        // 验证 service 方法被调用
        verify(purchaseService, times(1)).getMissingBooksById(missingBookId);
    }

    /**
     * 测试根据ID获取缺书记录接口 - 缺书记录不存在
     * 验证：返回空数据
     */
    @Test
    @DisplayName("获取缺书记录详情 - 缺书记录不存在")
    void testGetMissingBooksById_NotFound() throws Exception {
        Integer missingBookId = 999;
        // Mock 服务层返回 null
        when(purchaseService.getMissingBooksById(missingBookId)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/purchases/missingBooks/{missingBookId}", missingBookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(purchaseService, times(1)).getMissingBooksById(missingBookId);
    }
}

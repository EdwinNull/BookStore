package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierService;
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
 * SupplierController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock SupplierService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = SupplierController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private SupplierService supplierService;

    private Suppliers testSupplier;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试供应商对象
     */
    @BeforeEach
    void setUp() {
        testSupplier = new Suppliers();
        testSupplier.setSupplierId(1);
        testSupplier.setName("测试供应商");
        testSupplier.setAddress("北京市朝阳区");
        testSupplier.setContactInfo("13800138000");
    }

    /**
     * 测试添加供应商接口 - POST /api/supplier/add
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("添加供应商 - 成功")
    void testAddSupplier_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testSupplier);

        // 执行请求并验证响应
        mockMvc.perform(post("/api/supplier/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("添加成功"));

        // 验证 service 方法被调用一次
        verify(supplierService, times(1)).addSupplier(any(Suppliers.class));
    }

    /**
     * 测试更新供应商接口 - PUT /api/supplier/update
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("更新供应商 - 成功")
    void testUpdateSupplier_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testSupplier);

        // 执行请求并验证响应
        mockMvc.perform(put("/api/supplier/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("更新成功"));

        // 验证 service 方法被调用一次
        verify(supplierService, times(1)).updateSupplier(any(Suppliers.class));
    }

    /**
     * 测试删除供应商接口 - DELETE /api/supplier/delete/{id}
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("删除供应商 - 成功")
    void testDeleteSupplier_Success() throws Exception {
        Integer supplierId = 1;

        // 执行请求并验证响应
        mockMvc.perform(delete("/api/supplier/delete/{id}", supplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("删除成功"));

        // 验证 service 方法被调用一次
        verify(supplierService, times(1)).deleteSupplier(supplierId);
    }

    /**
     * 测试删除供应商接口 - 不同的供应商ID
     * 验证：不同ID参数正确传递到服务层
     */
    @Test
    @DisplayName("删除供应商 - 不同ID")
    void testDeleteSupplier_DifferentId() throws Exception {
        Integer supplierId = 999;

        // 执行请求并验证响应
        mockMvc.perform(delete("/api/supplier/delete/{id}", supplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用，且参数正确
        verify(supplierService, times(1)).deleteSupplier(999);
    }

    /**
     * 测试根据名称查询供应商接口 - GET /api/supplier/findByName/{name}
     * 验证：成功返回供应商信息
     */
    @Test
    @DisplayName("根据名称查询供应商 - 成功")
    void testFindByName_Success() throws Exception {
        String name = "测试供应商";
        // Mock 服务层返回
        when(supplierService.findByName(name)).thenReturn(testSupplier);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/supplier/findByName/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.supplierId").value(1))
                .andExpect(jsonPath("$.data.name").value("测试供应商"));

        // 验证 service 方法被调用
        verify(supplierService, times(1)).findByName(name);
    }

    /**
     * 测试根据名称查询供应商接口 - 供应商不存在
     * 验证：返回空数据
     */
    @Test
    @DisplayName("根据名称查询供应商 - 供应商不存在")
    void testFindByName_NotFound() throws Exception {
        String name = "不存在的供应商";
        // Mock 服务层返回 null
        when(supplierService.findByName(name)).thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/supplier/findByName/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证 service 方法被调用
        verify(supplierService, times(1)).findByName(name);
    }

    /**
     * 测试根据名称查询供应商接口 - 中文名称
     * 验证：中文路径参数正确传递
     */
    @Test
    @DisplayName("根据名称查询供应商 - 中文名称")
    void testFindByName_ChineseName() throws Exception {
        String chineseName = "北京图书供应商";
        Suppliers chineseSupplier = new Suppliers();
        chineseSupplier.setSupplierId(2);
        chineseSupplier.setName(chineseName);
        chineseSupplier.setAddress("北京市海淀区");

        // Mock 服务层返回
        when(supplierService.findByName(chineseName)).thenReturn(chineseSupplier);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/supplier/findByName/{name}", chineseName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value(chineseName));

        // 验证 service 方法被调用
        verify(supplierService, times(1)).findByName(chineseName);
    }
}

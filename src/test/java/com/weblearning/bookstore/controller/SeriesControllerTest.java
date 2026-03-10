package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.pojo.Series;
import com.weblearning.bookstore.servcie.SeriesService;
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
 * SeriesController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock SeriesService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 */
@WebMvcTest(value = SeriesController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class SeriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private SeriesService seriesService;

    private Series testSeries;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行，准备通用的测试系列对象
     */
    @BeforeEach
    void setUp() {
        testSeries = new Series();
        testSeries.setSeriesId(1);
        testSeries.setSeriesName("测试系列");
    }

    /**
     * 测试添加系列接口 - POST /series/add
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("添加系列 - 成功")
    void testAddSeries_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testSeries);

        // 执行请求并验证响应
        mockMvc.perform(post("/series/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Series added"));

        // 验证 service 方法被调用一次
        verify(seriesService, times(1)).addSeries(any(Series.class));
    }

    /**
     * 测试根据名称查询系列接口 - GET /series/find/{name}
     * 验证：成功返回系列信息
     */
    @Test
    @DisplayName("根据名称查询系列 - 成功")
    void testFindSeriesByName_Success() throws Exception {
        String name = "测试系列";
        // Mock 服务层返回
        when(seriesService.findSeriesByName(name)).thenReturn(testSeries);

        // 执行请求并验证响应
        mockMvc.perform(get("/series/find/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seriesId").value(1))
                .andExpect(jsonPath("$.seriesName").value("测试系列"));

        // 验证 service 方法被调用
        verify(seriesService, times(1)).findSeriesByName(name);
    }

    /**
     * 测试根据名称查询系列接口 - 系列不存在
     * 验证：返回空数据
     */
    @Test
    @DisplayName("根据名称查询系列 - 系列不存在")
    void testFindSeriesByName_NotFound() throws Exception {
        String name = "不存在的系列";
        // Mock 服务层返回 null
        when(seriesService.findSeriesByName(name)).thenReturn(null);

        // 执行请求并验证响应 - 由于直接返回对象，null 时响应体为空
        mockMvc.perform(get("/series/find/{name}", name))
                .andExpect(status().isOk());

        // 验证 service 方法被调用
        verify(seriesService, times(1)).findSeriesByName(name);
    }

    /**
     * 测试更新系列接口 - PUT /series/update
     * 验证：请求成功返回200，服务层方法被正确调用
     */
    @Test
    @DisplayName("更新系列 - 成功")
    void testUpdateSeries_Success() throws Exception {
        // 准备请求体
        String json = objectMapper.writeValueAsString(testSeries);

        // 执行请求并验证响应
        mockMvc.perform(put("/series/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Series updated"));

        // 验证 service 方法被调用一次
        verify(seriesService, times(1)).updateSeries(any(Series.class));
    }

    /**
     * 测试更新系列接口 - 带中文路径参数
     * 验证：中文路径参数正确传递
     */
    @Test
    @DisplayName("根据名称查询系列 - 中文路径参数")
    void testFindSeriesByName_ChineseName() throws Exception {
        String chineseName = "计算机科学丛书";
        Series chineseSeries = new Series();
        chineseSeries.setSeriesId(2);
        chineseSeries.setSeriesName(chineseName);

        // Mock 服务层返回
        when(seriesService.findSeriesByName(chineseName)).thenReturn(chineseSeries);

        // 执行请求并验证响应
        mockMvc.perform(get("/series/find/{name}", chineseName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seriesName").value(chineseName));

        // 验证 service 方法被调用
        verify(seriesService, times(1)).findSeriesByName(chineseName);
    }
}

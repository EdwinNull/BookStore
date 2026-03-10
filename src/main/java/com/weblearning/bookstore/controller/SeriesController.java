package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Series;
import com.weblearning.bookstore.servcie.SeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系列管理接口
 * 提供图书系列的增删改查功能
 */
@RestController
@RequestMapping("/api/series")
@Tag(name = "系列管理", description = "图书系列的添加、查询、更新等接口")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    /**
     * 添加系列
     */
    @Operation(summary = "添加系列", description = "添加新的图书系列，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add")
    public Result addSeries(
            @Parameter(description = "系列信息", required = true)
            @RequestBody Series series) {
        seriesService.addSeries(series);
        return Result.success("Series added");
    }

    /**
     * 根据名称查询系列
     */
    @Operation(summary = "查询系列", description = "根据系列名称查询系列信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/find")
    public Result findSeriesByName(
            @Parameter(description = "系列名称", required = true, example = "计算机科学丛书")
            @RequestParam("name") String name) {
        Series series = seriesService.findSeriesByName(name);
        return Result.success(series);
    }

    /**
     * 更新系列
     */
    @Operation(summary = "更新系列", description = "更新系列信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/update")
    public Result updateSeries(
            @Parameter(description = "系列信息", required = true)
            @RequestBody Series series) {
        seriesService.updateSeries(series);
        return Result.success("Series updated");
    }
}

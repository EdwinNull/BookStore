package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商管理接口
 * 提供供应商的增删改查功能
 */
@RestController
@RequestMapping("/api/supplier")
@Tag(name = "供应商管理", description = "供应商的添加、更新、删除、查询等接口")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 添加供应商
     */
    @Operation(summary = "添加供应商", description = "添加新的供应商，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add")
    public Result addSupplier(
            @Parameter(description = "供应商信息", required = true)
            @RequestBody Suppliers supplier) {
        supplierService.addSupplier(supplier);
        return Result.success("添加成功");
    }

    /**
     * 更新供应商
     */
    @Operation(summary = "更新供应商", description = "更新供应商信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/update")
    public Result updateSupplier(
            @Parameter(description = "供应商信息", required = true)
            @RequestBody Suppliers supplier) {
        supplierService.updateSupplier(supplier);
        return Result.success("更新成功");
    }

    /**
     * 删除供应商
     */
    @Operation(summary = "删除供应商", description = "根据ID删除供应商，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "供应商不存在")
    })
    @DeleteMapping("/delete/{id}")
    public Result deleteSupplier(
            @Parameter(description = "供应商ID", required = true, example = "1")
            @PathVariable("id") Integer id) {
        supplierService.deleteSupplier(id);
        return Result.success("删除成功");
    }

    /**
     * 根据名称查询供应商
     */
    @Operation(summary = "查询供应商", description = "根据供应商名称查询供应商信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/findByName/{name}")
    public Result findByName(
            @Parameter(description = "供应商名称", required = true, example = "清华大学出版社")
            @PathVariable("name") String name) {
        Suppliers supplier = supplierService.findByName(name);
        return Result.success(supplier);
    }
}

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

    /**
     * 获取所有供应商列表
     */
    @Operation(summary = "获取所有供应商", description = "获取所有供应商列表，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/all")
    public Result getAllSuppliers() {
        java.util.List<Suppliers> suppliers = supplierService.findAll();
        return Result.success(suppliers);
    }

    /**
     * 根据状态获取供应商列表
     */
    @Operation(summary = "根据状态获取供应商", description = "获取指定状态的供应商列表，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/status/{status}")
    public Result getSuppliersByStatus(
            @Parameter(description = "供应商状态", required = true, example = "pending")
            @PathVariable("status") String status) {
        java.util.List<Suppliers> suppliers = supplierService.findByStatus(status);
        return Result.success(suppliers);
    }

    /**
     * 根据ID获取供应商详情
     */
    @Operation(summary = "获取供应商详情", description = "根据ID获取供应商详细信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "供应商不存在")
    })
    @GetMapping("/{id}")
    public Result getById(
            @Parameter(description = "供应商ID", required = true, example = "1")
            @PathVariable("id") Integer id) {
        Suppliers supplier = supplierService.findById(id);
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        return Result.success(supplier);
    }

    /**
     * 更新供应商状态（管理员审核）
     */
    @Operation(summary = "更新供应商状态", description = "审核供应商，更新其状态，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/status/{id}")
    public Result updateStatus(
            @Parameter(description = "供应商ID", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "状态：active-激活，inactive-停用，pending-待审核", required = true, example = "active")
            @RequestParam String status) {
        supplierService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}

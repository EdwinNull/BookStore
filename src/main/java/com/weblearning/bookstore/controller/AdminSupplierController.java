package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员供应商管理接口
 * 提供供应商的增删改查和审核功能
 * 注意：此接口路径为 /api/admin/suppliers，需要管理员权限，由LoginInterceptor处理
 */
@RestController
@RequestMapping("/api/admin/suppliers")
@Tag(name = "管理员-供应商管理", description = "管理员对供应商的增删改查和审核接口")
public class AdminSupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 获取所有供应商列表
     */
    @Operation(summary = "获取所有供应商", description = "获取所有供应商列表，需要管理员权限")
    @GetMapping("/list")
    public Result<List<Suppliers>> getAllSuppliers() {
        List<Suppliers> suppliers = supplierService.findAll();
        return Result.success(suppliers);
    }

    /**
     * 获取待审核供应商列表
     */
    @Operation(summary = "获取待审核供应商", description = "获取状态为pending的供应商列表")
    @GetMapping("/pending")
    public Result<List<Suppliers>> getPendingSuppliers() {
        List<Suppliers> suppliers = supplierService.findByStatus("pending");
        return Result.success(suppliers);
    }

    /**
     * 根据ID获取供应商详情
     */
    @Operation(summary = "获取供应商详情", description = "根据ID获取供应商详细信息")
    @GetMapping("/{id}")
    public Result<Suppliers> getById(@PathVariable("id") Integer id) {
        Suppliers supplier = supplierService.findById(id);
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        return Result.success(supplier);
    }

    /**
     * 添加供应商（管理员直接添加，状态默认为active）
     */
    @Operation(summary = "添加供应商", description = "管理员添加供应商，默认状态为active")
    @PostMapping("/add")
    public Result addSupplier(@RequestBody Suppliers supplier) {
        supplierService.addSupplier(supplier);
        return Result.success("添加成功");
    }

    /**
     * 更新供应商信息
     */
    @Operation(summary = "更新供应商", description = "更新供应商基本信息")
    @PutMapping("/update")
    public Result updateSupplier(@RequestBody Suppliers supplier) {
        supplierService.updateSupplier(supplier);
        return Result.success("更新成功");
    }

    /**
     * 删除供应商
     */
    @Operation(summary = "删除供应商", description = "根据ID删除供应商")
    @DeleteMapping("/delete/{id}")
    public Result deleteSupplier(@PathVariable("id") Integer id) {
        supplierService.deleteSupplier(id);
        return Result.success("删除成功");
    }

    /**
     * 更新供应商状态（审核通过/拒绝/停用）
     */
    @Operation(summary = "更新供应商状态", description = "审核供应商或更改其状态")
    @PutMapping("/status/{id}")
    public Result updateStatus(
            @PathVariable("id") Integer id,
            @Parameter(description = "状态：active-激活，inactive-停用，pending-待审核")
            @RequestParam String status) {
        supplierService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 根据名称查询供应商
     */
    @Operation(summary = "查询供应商", description = "根据供应商名称查询")
    @GetMapping("/findByName/{name}")
    public Result<Suppliers> findByName(@PathVariable("name") String name) {
        Suppliers supplier = supplierService.findByName(name);
        return Result.success(supplier);
    }
}

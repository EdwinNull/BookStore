package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.Purchase;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.servcie.BookService;
import com.weblearning.bookstore.servcie.PurchaseService;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供应商采购订单接口
 * 提供供应商查看和管理自己的采购订单功能
 */
@RestController
@RequestMapping("/api/supplier/orders")
@Tag(name = "供应商采购订单", description = "供应商查看和管理采购订单的接口")
public class SupplierOrderController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private BookService bookService;

    /**
     * 获取供应商的所有采购订单
     */
    @Operation(summary = "获取采购订单列表", description = "获取当前供应商的所有采购订单")
    @GetMapping
    public Result getOrders() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");

        List<Purchase> orders = purchaseService.getPurchasesBySupplierId(supplierId);

        Map<String, Object> result = new HashMap<>();
        result.put("orders", orders);
        result.put("total", orders.size());

        return Result.success(result);
    }

    /**
     * 获取采购订单详情
     */
    @Operation(summary = "获取订单详情", description = "获取指定采购订单的详细信息")
    @GetMapping("/{orderId}")
    public Result getOrderDetail(@PathVariable Integer orderId) {
        Purchase purchase = purchaseService.getPurchaseById(orderId);
        if (purchase == null) {
            return Result.error("订单不存在");
        }

        // 验证订单是否属于当前供应商
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");
        if (!purchase.getSupplierId().equals(supplierId)) {
            return Result.error("无权访问此订单");
        }

        // 获取图书信息
        Books book = bookService.findById(purchase.getBookId());

        Map<String, Object> result = new HashMap<>();
        result.put("order", purchase);
        result.put("book", book);

        return Result.success(result);
    }

    /**
     * 确认接单
     */
    @Operation(summary = "确认接单", description = "供应商确认接受采购订单")
    @PostMapping("/{orderId}/confirm")
    public Result confirmOrder(@PathVariable Integer orderId) {
        Purchase purchase = purchaseService.getPurchaseById(orderId);
        if (purchase == null) {
            return Result.error("订单不存在");
        }

        // 验证订单是否属于当前供应商
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");
        if (!purchase.getSupplierId().equals(supplierId)) {
            return Result.error("无权操作此订单");
        }

        // 检查订单状态
        if (!"pending".equals(purchase.getStatus())) {
            return Result.error("订单状态不允许此操作");
        }

        // 更新状态为已确认/进行中
        purchaseService.updatePurchase(orderId, "confirmed");
        return Result.success("接单成功");
    }

    /**
     * 发货
     */
    @Operation(summary = "发货", description = "供应商发货，填写物流信息")
    @PostMapping("/{orderId}/ship")
    public Result shipOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) String carrier) {

        Purchase purchase = purchaseService.getPurchaseById(orderId);
        if (purchase == null) {
            return Result.error("订单不存在");
        }

        // 验证订单是否属于当前供应商
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");
        if (!purchase.getSupplierId().equals(supplierId)) {
            return Result.error("无权操作此订单");
        }

        // 检查订单状态
        if (!"confirmed".equals(purchase.getStatus())) {
            return Result.error("订单状态不允许发货，请先确认接单");
        }

        // 更新状态为已发货
        purchaseService.updatePurchase(orderId, "shipped");
        // TODO: 保存物流信息（trackingNumber, carrier）
        return Result.success("发货成功");
    }

    /**
     * 拒绝订单
     */
    @Operation(summary = "拒绝订单", description = "供应商拒绝采购订单")
    @PostMapping("/{orderId}/reject")
    public Result rejectOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = false) String reason) {

        Purchase purchase = purchaseService.getPurchaseById(orderId);
        if (purchase == null) {
            return Result.error("订单不存在");
        }

        // 验证订单是否属于当前供应商
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");
        if (!purchase.getSupplierId().equals(supplierId)) {
            return Result.error("无权操作此订单");
        }

        // 检查订单状态
        if (!"pending".equals(purchase.getStatus())) {
            return Result.error("订单状态不允许此操作");
        }

        // 更新状态为已拒绝
        purchaseService.updatePurchase(orderId, "rejected");
        // TODO: 保存拒绝原因
        return Result.success("已拒绝订单");
    }

    /**
     * 获取订单统计
     */
    @Operation(summary = "获取订单统计", description = "获取当前供应商的订单统计数据")
    @GetMapping("/stats")
    public Result getOrderStats() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");

        Map<String, Object> stats = purchaseService.getSupplierOrderStats(supplierId);
        return Result.success(stats);
    }
}

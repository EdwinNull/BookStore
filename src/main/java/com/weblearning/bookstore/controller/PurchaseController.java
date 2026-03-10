package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.servcie.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 采购管理接口
 * 提供采购订单和缺书登记的管理功能
 */
@RestController
@RequestMapping("/purchases")
@Tag(name = "采购管理", description = "采购订单和缺书登记的添加、更新、查询等接口")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    /**
     * 添加缺书登记（直接添加）
     */
    @Operation(summary = "添加缺书登记", description = "直接添加缺书登记记录，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add1")
    public Result addMissStraight(
            @Parameter(description = "缺书记录信息", required = true)
            @RequestBody MissingBooks missingBooks) {
        purchaseService.addMissStraight(missingBooks);
        return Result.success();
    }

    /**
     * 添加采购订单
     */
    @Operation(summary = "添加采购订单", description = "添加新的采购订单，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add2")
    public Result addPurchase(
            @Parameter(description = "采购订单信息", required = true)
            @RequestBody Purchase purchase) {
        purchaseService.addPurchase(purchase);
        return Result.success();
    }

    /**
     * 更新采购订单状态
     */
    @Operation(summary = "更新采购订单状态", description = "更新采购订单的状态，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/update")
    public Result updatePurchase(
            @Parameter(description = "采购订单ID", required = true, example = "1")
            @RequestParam Integer purchaseOrderId,
            @Parameter(description = "订单状态（如：pending、completed）", required = true, example = "completed")
            @RequestParam String status) {
        purchaseService.updatePurchase(purchaseOrderId, status);
        return Result.success();
    }

    /**
     * 根据ID获取采购订单
     */
    @Operation(summary = "获取采购订单", description = "根据采购订单ID获取订单详细信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "采购订单不存在")
    })
    @GetMapping("/{purchaseOrderId}")
    public Result getPurchaseById(
            @Parameter(description = "采购订单ID", required = true, example = "1")
            @PathVariable("purchaseOrderId") Integer purchaseOrderId) {
        Purchase purchase = purchaseService.getPurchaseById(purchaseOrderId);
        return Result.success(purchase);
    }

    /**
     * 根据ID获取缺书登记
     */
    @Operation(summary = "获取缺书登记", description = "根据缺书登记ID获取详细信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "缺书记录不存在")
    })
    @GetMapping("/missingBooks/{missingBookId}")
    public Result getMissingBooksById(
            @Parameter(description = "缺书登记ID", required = true, example = "1")
            @PathVariable("missingBookId") Integer missingBookId) {
        MissingBooks missingBooks = purchaseService.getMissingBooksById(missingBookId);
        return Result.success(missingBooks);
    }
}

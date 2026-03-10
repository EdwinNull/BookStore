package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.OrderDetails;
import com.weblearning.bookstore.pojo.OrderRequest;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.servcie.OrderService;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单接口
 * 提供订单的创建、查询等功能
 */
@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理", description = "订单的创建、查询、管理等接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "创建新的订单，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add")
    public Result addOrder(
            @Parameter(description = "订单信息", required = true)
            @RequestBody OrderRequest order) {
        orderService.addOrder(order);
        return Result.success();
    }

    /**
     * 获取订单基本信息
     */
    @Operation(summary = "获取订单信息", description = "根据订单ID获取订单基本信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/get/{orderId}")
    public Result getOrder(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable("orderId") Integer orderId) {
        Order order = orderService.getOrder(orderId);
        return Result.success(order);
    }

    /**
     * 获取订单详细信息（包含订单项）
     */
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息（包含订单项），需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/detail/{orderId}")
    public Result<OrderDetailResponse> getOrderDetail(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable("orderId") Integer orderId) {
        OrderDetailResponse orderDetails = orderService.getOrderDetail(orderId);
        return Result.success(orderDetails);
    }

    /**
     * 获取当前用户的订单列表（分页）
     */
    @Operation(summary = "获取用户订单列表", description = "获取当前登录用户的订单列表（分页），需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/list")
    public Result<PageBean<Order>> getUserOrderList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从ThreadLocal获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        PageBean<Order> pageBean = orderService.getUserOrderList(userId, pageNum, pageSize);
        return Result.success(pageBean);
    }

    /**
     * 获取所有订单列表（管理员用，分页）
     */
    @Operation(summary = "获取所有订单列表", description = "分页获取所有订单列表，支持按状态筛选，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要管理员登录")
    })
    @GetMapping("/all")
    public Result<PageBean<Order>> getAllOrderList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "订单状态（可选，如：pending、shipped、completed）")
            @RequestParam(required = false) String status) {
        PageBean<Order> pageBean = orderService.getAllOrderList(pageNum, pageSize, status);
        return Result.success(pageBean);
    }

    /**
     * 用户确认收货
     * 确认收货后，订单状态变为delivered，并累加用户消费金额用于折扣计算
     */
    @Operation(summary = "确认收货", description = "用户确认收货，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "确认成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "400", description = "订单状态不允许确认收货")
    })
    @PostMapping("/confirm-receive/{orderId}")
    public Result confirmReceive(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable("orderId") Integer orderId) {
        // 从ThreadLocal获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        try {
            orderService.confirmReceive(orderId, userId);
            return Result.success("确认收货成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Ship;
import com.weblearning.bookstore.servcie.BookService;
import com.weblearning.bookstore.servcie.OrderService;
import com.weblearning.bookstore.servcie.ShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流管理接口
 * 提供物流记录的添加和查询功能
 */
@RestController
@RequestMapping("/api/ship")
@Tag(name = "物流管理", description = "物流记录的添加、查询等接口")
public class ShipController {

    @Autowired
    private ShipService shipService;

    /**
     * 添加物流记录
     * 支持一个订单多次发货（如分批次发货）
     */
    @Operation(summary = "添加物流记录", description = "为订单添加物流记录，支持一个订单多次发货（分批次发货），需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add")
    public Result addShip(
            @Parameter(description = "订单ID", required = true, example = "1")
            @RequestParam("orderId") Integer orderId) {
        shipService.addShip(orderId);
        return Result.success("添加成功");
    }

    /**
     * 获取订单的最新一条物流信息
     */
    @Operation(summary = "获取最新物流", description = "获取订单的最新一条物流信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
        @ApiResponse(responseCode = "404", description = "物流记录不存在")
    })
    @GetMapping("/{orderId}")
    public Result getShip(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable("orderId") Integer orderId) {
        Ship ship = shipService.getShip(orderId);
        return Result.success(ship);
    }

    /**
     * 获取订单的所有物流记录
     * 支持一个订单对应多个物流的场景
     */
    @Operation(summary = "获取订单所有物流", description = "获取订单的所有物流记录（支持一个订单多个物流的场景），需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/list/{orderId}")
    public Result getShipsByOrderId(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable("orderId") Integer orderId) {
        List<Ship> ships = shipService.getShipsByOrderId(orderId);
        return Result.success(ships);
    }
}

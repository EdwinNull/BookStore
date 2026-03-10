package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.dto.SupplierLoginRequest;
import com.weblearning.bookstore.dto.SupplierProfileResponse;
import com.weblearning.bookstore.dto.SupplierRegisterRequest;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierAuthService;
import com.weblearning.bookstore.utils.JwtUtil;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 供应商认证接口
 * 提供供应商的注册、登录、信息管理等功能
 */
@RestController
@RequestMapping("/api/supplier/auth")
@Tag(name = "供应商认证", description = "供应商注册、登录、信息管理等相关接口")
public class SupplierAuthController {

    @Autowired
    private SupplierAuthService supplierAuthService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 供应商注册申请
     * 注册后状态为 pending，需要管理员审核后才能登录
     */
    @Operation(summary = "供应商注册", description = "供应商注册申请，注册后需要管理员审核")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册申请成功，等待审核"),
        @ApiResponse(responseCode = "400", description = "用户名已存在或参数格式错误")
    })
    @PostMapping("/register")
    public Result register(@Valid @RequestBody SupplierRegisterRequest request) {
        // 检查用户名是否已存在
        if (supplierAuthService.isUsernameExists(request.getUsername())) {
            return Result.error("用户名已存在");
        }

        supplierAuthService.register(request);
        return Result.success("注册申请成功，请等待管理员审核");
    }

    /**
     * 供应商登录
     * 只有状态为 active 的供应商才能登录
     */
    @Operation(summary = "供应商登录", description = "供应商登录，返回JWT令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功，返回JWT令牌"),
        @ApiResponse(responseCode = "400", description = "用户名不存在或密码错误"),
        @ApiResponse(responseCode = "403", description = "账号未激活或已被停用")
    })
    @PostMapping("/login")
    public Result login(@Valid @RequestBody SupplierLoginRequest request) {
        Suppliers supplier;
        try {
            supplier = supplierAuthService.login(request.getUsername(), request.getPassword());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }

        if (supplier == null) {
            return Result.error("用户名或密码错误");
        }

        // 生成JWT令牌，包含供应商信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("supplierId", supplier.getSupplierId());
        claims.put("username", supplier.getUsername());
        claims.put("role", "supplier");  // 标识为供应商角色
        String token = JwtUtil.genToken(claims);

        // 将token存入Redis，有效期12小时
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("supplier_token:" + token, token, 12, TimeUnit.HOURS);

        return Result.success(token);
    }

    /**
     * 获取当前登录供应商信息
     */
    @Operation(summary = "获取供应商信息", description = "获取当前登录供应商的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/profile")
    public Result<SupplierProfileResponse> getProfile() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");

        SupplierProfileResponse profile = supplierAuthService.getProfile(supplierId);
        if (profile == null) {
            return Result.error("供应商不存在");
        }
        return Result.success(profile);
    }

    /**
     * 更新供应商信息
     */
    @Operation(summary = "更新供应商信息", description = "更新当前登录供应商的信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/profile")
    public Result updateProfile(@RequestBody Suppliers supplier) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");

        // 确保只能更新自己的信息
        supplier.setSupplierId(supplierId);
        // 不允许通过此接口修改密码和状态
        supplier.setPassword(null);
        supplier.setStatus(null);

        supplierAuthService.updateProfile(supplier);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改当前登录供应商的密码")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "修改成功"),
        @ApiResponse(responseCode = "400", description = "原密码错误或两次密码不一致"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PatchMapping("/password")
    public Result updatePassword(
            @Parameter(description = "旧密码", required = true) @RequestParam String oldPwd,
            @Parameter(description = "新密码", required = true) @RequestParam String newPwd,
            @Parameter(description = "确认新密码", required = true) @RequestParam String confirmPwd,
            @RequestHeader("Authorization") String token) {

        if (!newPwd.equals(confirmPwd)) {
            return Result.error("两次密码不一致");
        }

        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer supplierId = (Integer) claims.get("supplierId");

        boolean success = supplierAuthService.updatePassword(supplierId, oldPwd, newPwd);
        if (!success) {
            return Result.error("原密码错误");
        }

        // 删除Redis中的token，强制重新登录
        stringRedisTemplate.delete("supplier_token:" + token);

        return Result.success("密码修改成功，请重新登录");
    }

    /**
     * 供应商登出
     */
    @Operation(summary = "供应商登出", description = "退出登录，清除token")
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        // 删除Redis中的token
        stringRedisTemplate.delete("supplier_token:" + token);
        return Result.success("登出成功");
    }

    // ==================== 管理员操作接口 ====================

    /**
     * 获取待审核供应商列表（管理员）
     */
    @Operation(summary = "获取待审核供应商", description = "管理员获取待审核的供应商列表")
    @GetMapping("/admin/pending")
    public Result<List<Suppliers>> getPendingSuppliers() {
        List<Suppliers> suppliers = supplierAuthService.getPendingSuppliers();
        return Result.success(suppliers);
    }

    /**
     * 获取所有供应商列表（管理员）
     */
    @Operation(summary = "获取所有供应商", description = "管理员获取所有供应商列表")
    @GetMapping("/admin/all")
    public Result<List<Suppliers>> getAllSuppliers() {
        List<Suppliers> suppliers = supplierAuthService.getAllSuppliers();
        return Result.success(suppliers);
    }

    /**
     * 审核供应商（管理员）
     */
    @Operation(summary = "审核供应商", description = "管理员审核供应商，通过或拒绝")
    @PostMapping("/admin/audit/{supplierId}")
    public Result auditSupplier(
            @PathVariable Integer supplierId,
            @Parameter(description = "状态：active-通过，inactive-拒绝", required = true)
            @RequestParam String status) {

        try {
            supplierAuthService.auditSupplier(supplierId, status);
            String message = "active".equals(status) ? "审核通过" : "已拒绝";
            return Result.success(message);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.dto.LoginRequest;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.JwtUtil;
import com.weblearning.bookstore.utils.Md5Util;
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
 * 管理员接口
 * 提供管理员登录、用户管理等功能
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口", description = "管理员登录、用户管理等后台管理接口")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 管理员登录 - 支持 JSON 格式
     */
    @Operation(summary = "管理员登录", description = "管理员登录接口，返回JWT令牌，支持匿名访问")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功，返回JWT令牌"),
        @ApiResponse(responseCode = "400", description = "密码错误")
    })
    @PostMapping("/login")
    public Result adminLogin(@Valid @RequestBody LoginRequest request) {
        User loginUser = userService.findByUserName(request.getUsername());
        if (loginUser == null) {
            return Result.error("用户名不存在");
        }
        if (loginUser.getPassword().equals(Md5Util.getMD5String(request.getPassword()))) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", loginUser.getUserId());
            map.put("username", loginUser.getUsername());
            map.put("role", loginUser.getRole());
            String token = JwtUtil.genToken(map);
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            ops.set(token, token, 1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    /**
     * 获取用户列表（分页）
     */
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持按用户名和角色筛选，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要管理员登录")
    })
    @GetMapping("/users")
    public Result<PageBean<User>> getUserList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名（可选，模糊搜索）")
            @RequestParam(required = false) String username,
            @Parameter(description = "角色（可选，如：admin、user）")
            @RequestParam(required = false) String role) {
        PageBean<User> pageBean = userService.getUserList(pageNum, pageSize, username, role);
        return Result.success(pageBean);
    }

    /**
     * 获取所有用户列表（不分页）
     */
    @Operation(summary = "获取所有用户", description = "获取所有用户列表（不分页），用于下拉选择等场景，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要管理员登录")
    })
    @GetMapping("/users/all")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @Operation(summary = "添加用户余额", description = "为指定用户添加余额，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要管理员登录")
    })
    @PutMapping("/addBalance")
    public Result addBalance(
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam("userId") Integer userId,
            @Parameter(description = "添加的余额金额", required = true, example = "100.00")
            @RequestParam("balance") Double balance) {
        userService.addBalance(userId, balance);
        return Result.success("添加成功");
    }

    /**
     * 根据用户ID查询用户信息
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要管理员登录"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/show/{userId}")
    public Result showUser(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable("userId") Integer userId) {
        User user = userService.findById(userId);
        return Result.success(user);
    }
}

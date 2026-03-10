package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.dto.LoginRequest;
import com.weblearning.bookstore.dto.RegisterRequest;
import com.weblearning.bookstore.dto.PasswordUpdateRequest;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.JwtUtil;
import com.weblearning.bookstore.utils.Md5Util;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户接口
 * 提供用户注册、登录、信息查询和更新等功能
 */
@RestController
@RequestMapping("/api")
@Tag(name = "用户接口", description = "用户注册、登录、信息查询和更新等相关接口")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户注册 - 支持 JSON 格式
     */
    @Operation(summary = "用户注册", description = "用户注册接口，用户名和密码必须为5-16位的非空字符，支持匿名访问")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "用户名已存在或参数格式错误")
    })
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.findByUserName(request.getUsername());
        if (user == null) {
            userService.add(request.getUsername(), request.getPassword());
            return Result.success("register success");
        } else {
            return Result.error("用户名已存在");
        }
    }

    /**
     * 用户登录 - 支持 JSON 格式
     */
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌，支持匿名访问")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功，返回JWT令牌"),
        @ApiResponse(responseCode = "400", description = "用户名不存在或密码错误")
    })
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginRequest request) {
        User loginUser = userService.findByUserName(request.getUsername());
        if (loginUser == null) {
            return Result.error("用户名不存在");
        }
        if (Md5Util.getMD5String(request.getPassword()).equals(loginUser.getPassword())) {
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
     * 获取当前用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/user/userInfo")
    public Result<User> userInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的信息，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/user/update")
    public Result update(@Valid @RequestBody User user) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        userService.update(user, userId);
        return Result.success("update success");
    }

    /**
     * 修改密码 - 支持 JSON 格式
     */
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码，需要登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "修改成功"),
        @ApiResponse(responseCode = "400", description = "原密码错误或两次密码不一致"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PatchMapping("/user/updatePwd")
    public Result updatePwd(
            @Valid @RequestBody PasswordUpdateRequest request,
            @Parameter(description = "JWT令牌（请求头）", required = true)
            @RequestHeader("Authorization") String token) {
        String oldPwd = request.getOld_pwd();
        String newPwd = request.getNew_pwd();
        String rePwd = request.getRe_pwd();

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("参数不能为空");
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        User loginUser = userService.findByUserName(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))) {
            return Result.error("原密码错误");
        }
        if (!rePwd.equals(newPwd)) {
            return Result.error("两次密码不一致");
        }
        userService.updatePwd(username, newPwd);
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.getOperations().delete(token);
        return Result.success("更新密码成功");
    }
}

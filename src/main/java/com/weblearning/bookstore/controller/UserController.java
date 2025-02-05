package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.JwtUtil;
import com.weblearning.bookstore.utils.Md5Util;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@RequestParam(value = "username") @Pattern(regexp = "^\\S{5,16}$") String username,
                           @RequestParam(value = "password") @Pattern(regexp = "^\\S{5,16}$") String password){
        User user = userService.findByUserName(username);
        if(user ==null){
            userService.add(username,password);
            return Result.success("register success");
        }
        else{
            return Result.error("用户名已存在");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password){
        User loginUser = userService.findByUserName(username);
        if(loginUser ==null){
            return Result.error("用户名不存在");
        }
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            Map<String,Object> map = new HashMap<>();
            map.put("userId",loginUser.getUserId());
            map.put("username",loginUser.getUsername());
            map.put("role",loginUser.getRole());
            String token = JwtUtil.genToken(map);
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            ops.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    @GetMapping("/user/userInfo")
    public Result<User> userInfo(){
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping("/user/update")
    public Result update(@RequestBody User user){
        userService.update(user);
        return Result.success("update success");
    }

    @PatchMapping("/user/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("参数不能为空");
        }
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = map.get("username").toString();
        User loginUser = userService.findByUserName(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码错误");
        }
        if(!rePwd.equals(newPwd)){
            return Result.error("两次密码不一致");
        }
        userService.updatePwd(username,newPwd);
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.getOperations().delete(token);
        return Result.success("更新密码成功");
    }
}

package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.JwtUtil;
import com.weblearning.bookstore.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result adminLogin(@RequestParam("username") String username,
                             @RequestParam("password") String password){
        User loginUser = userService.findByUserName(username);
        if(loginUser.getPassword().equals(Md5Util.getMD5String(password))){
            Map<String,Object> map = new HashMap<>();
            map.put("userId",loginUser.getUserId());
            map.put("username",loginUser.getUsername());
            String token = JwtUtil.genToken(map);
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            ops.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
        }

        @PutMapping("/addBalance")
        public Result addBalance(@RequestParam("userId") Integer userId,@RequestParam("balance") Double balance){
            userService.addBalance(userId,balance);
            return Result.success("添加成功");
        }

        @GetMapping("/show/{userId}")
        public Result showUser(@PathVariable("userId") Integer userId){
            User user = userService.findById(userId);
            return Result.success(user);
        }
}


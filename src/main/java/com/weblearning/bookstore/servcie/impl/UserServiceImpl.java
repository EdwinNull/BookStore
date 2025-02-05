package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.Md5Util;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByUserName(String username) {
        User user = userMapper.findByUserName(username);
        return user;
    }

    @Override
    public void add(String username, String password) {
        String encodePassword = Md5Util.getMD5String(password);
        userMapper.add(username, encodePassword);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void updatePwd(String username, String newPwd) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),userId);
    }

    @Override
    public void addBalance(Integer userId, Double balance) {
        userMapper.addBalance(userId, balance);
        User user = userMapper.findById(userId);
        if(user.getAccountBalance() < 500){
            userMapper.updateCreditLevel(userId,1,0.10);
        }
        if(user.getAccountBalance() >= 500 && user.getAccountBalance() < 1000){
            userMapper.updateCreditLevel(userId,2,0.15);
        }
        if(user.getAccountBalance() >= 1000 && user.getAccountBalance() < 2000){
            userMapper.updateCreditLevel(userId,3,0.15);
            userMapper.updateOverBalance(userId,100.00);
        }
        if(user.getAccountBalance() >= 2000 && user.getAccountBalance() < 5000){
            userMapper.updateCreditLevel(userId,4,0.20);
            userMapper.updateOverBalance(userId,200.00);
        }
        if(user.getAccountBalance() >= 5000){
            userMapper.updateCreditLevel(userId,5,0.25);
            userMapper.updateOverBalance(userId,500.00);
        }
    }

    @Override
    public User findById(Integer userId) {
        User user = userMapper.findById(userId);
        return user;
    }
}

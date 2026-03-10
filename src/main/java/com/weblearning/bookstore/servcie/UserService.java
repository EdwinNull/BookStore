package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.User;

import java.util.List;

public interface UserService {
    User findByUserName(String username);

    void add(String username, String password);

    void update(User user, Integer userId);

    void updatePwd(String username, String newPwd);

    void addBalance(Integer userId, Double balance);

    User findById(Integer userId);

    // 获取用户列表（分页）
    PageBean<User> getUserList(Integer pageNum, Integer pageSize, String username, String role);

    // 获取所有用户
    List<User> getAllUsers();
}
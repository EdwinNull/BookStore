package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUserName(String username);

    void add(String username, String encodePassword);

    void update(User user, Integer userId);

    void updatePwd(String encodePassword, Integer userId);

    void addBalance(Integer userId, Double balance);

    User findById(Integer userId);

    void updateCreditLevel(Integer userId, int level, double discount);

    void updateOverBalance(Integer userId, double overBalance);

    Order findOrderByUserId(Integer userId);

    // 获取用户列表（带条件搜索）
    List<User> getUserList(@Param("username") String username, @Param("role") String role);

    // 获取所有用户
    List<User> getAllUsers();
}

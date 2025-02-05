package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUserName(String username);

    void add(String username, String encodePassword);

    void update(User user);


    void updatePwd(String encodePassword, Integer userId);

    void addBalance(Integer userId, Double balance);

    User findById(Integer userId);

    void updateCreditLevel(Integer userId, int level,double discount);

    void updateOverBalance(Integer userId, double overBalance);

    Order findOrderByUserId(Integer userId);

}

package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.User;

public interface UserService {
    User findByUserName(String username);

    void add(String username, String password);

    void update(User user);

    void updatePwd(String username, String newPwd);

    void addBalance(Integer userId, Double balance);

    User findById(Integer userId);
}

package com.weblearning.bookstore.servcie.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.UserService;
import com.weblearning.bookstore.utils.Md5Util;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void update(User user, Integer userId) {
        userMapper.update(user, userId);
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

    /**
     * 获取用户列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param username 用户名（可选，用于搜索）
     * @param role 角色（可选，用于筛选）
     * @return 分页用户列表
     */
    @Override
    public PageBean<User> getUserList(Integer pageNum, Integer pageSize, String username, String role) {
        // 创建PageBean对象
        PageBean<User> pageBean = new PageBean<>();

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询用户列表
        List<User> users = userMapper.getUserList(username, role);

        // 获取分页信息
        Page<User> page = (Page<User>) users;

        // 封装PageBean
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());

        return pageBean;
    }

    /**
     * 获取所有用户
     * @return 所有用户列表
     */
    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }
}

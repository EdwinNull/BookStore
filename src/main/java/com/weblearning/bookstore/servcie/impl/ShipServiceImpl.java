package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.mapper.OrderMapper;
import com.weblearning.bookstore.mapper.ShipMapper;
import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.Ship;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipMapper shipMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void addShip(Integer orderId) {
        Order order = orderMapper.findById(orderId);
        Integer userId = order.getUserId();
        User user = userMapper.findById(userId);
        String status = order.getStatus();
        String address = user.getAddress();
        LocalDateTime now = LocalDateTime.now();
        if(status.equals("待发货")){
            status = "已发货";
            shipMapper.addShip(orderId,address,now,status);
            orderMapper.updateStatus(orderId, status);
        }
    }

    @Override
    public Ship getShip(Integer orderId) {
        Ship ship = shipMapper.findByOrderId(orderId);
        return ship;
    }
}

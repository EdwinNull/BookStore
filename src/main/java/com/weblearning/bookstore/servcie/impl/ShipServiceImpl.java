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
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipMapper shipMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 添加物流记录
     * 支持一个订单多次发货（如分批次发货）
     */
    @Override
    public void addShip(Integer orderId) {
        Order order = orderMapper.findById(orderId);
        Integer userId = order.getUserId();
        User user = userMapper.findById(userId);
        String status = order.getStatus();
        String address = user.getAddress();
        LocalDateTime now = LocalDateTime.now();

        // 订单状态: confirmed=已确认(待发货), shipped=已发货
        // 允许多次发货，每次都会创建新的物流记录
        if (status.equals("confirmed") || status.equals("shipped")) {
            String shipStatus = "shipped";
            shipMapper.addShip(orderId, address, now, shipStatus);
            // 只有第一次发货时才更新订单状态
            if (status.equals("confirmed")) {
                orderMapper.updateStatus(orderId, shipStatus);
            }
        }
    }

    /**
     * 获取订单的最新一条物流信息
     */
    @Override
    public Ship getShip(Integer orderId) {
        return shipMapper.findByOrderId(orderId);
    }

    /**
     * 获取订单的所有物流记录
     * 支持一个订单对应多个物流的场景
     */
    @Override
    public List<Ship> getShipsByOrderId(Integer orderId) {
        return shipMapper.findAllByOrderId(orderId);
    }
}

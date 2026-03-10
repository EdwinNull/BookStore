package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.Ship;
import java.util.List;

public interface ShipService {
    // 添加物流记录
    void addShip(Integer orderId);

    // 获取订单的最新一条物流信息（兼容旧接口）
    Ship getShip(Integer orderId);

    // 获取订单的所有物流记录（支持一个订单多次发货）
    List<Ship> getShipsByOrderId(Integer orderId);
}

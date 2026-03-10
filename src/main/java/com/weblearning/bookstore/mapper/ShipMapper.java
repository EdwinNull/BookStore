package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Ship;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ShipMapper {
    // 添加物流记录
    void addShip(Integer orderId, String address, LocalDateTime now, String status);

    // 根据订单ID查询单个物流（兼容旧接口）
    Ship findByOrderId(Integer orderId);

    // 根据订单ID查询所有物流记录（支持一个订单多次发货）
    List<Ship> findAllByOrderId(Integer orderId);
}

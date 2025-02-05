package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Ship;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface ShipMapper {
    void addShip(Integer orderId, String address, LocalDateTime now, String status);

    Ship findByOrderId(Integer orderId);
}

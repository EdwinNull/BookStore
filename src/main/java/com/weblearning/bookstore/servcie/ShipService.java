package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.Ship;

public interface ShipService {
    void addShip(Integer orderId);

    Ship getShip(Integer orderId);
}

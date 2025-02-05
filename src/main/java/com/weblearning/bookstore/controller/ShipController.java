package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Ship;
import com.weblearning.bookstore.servcie.BookService;
import com.weblearning.bookstore.servcie.OrderService;
import com.weblearning.bookstore.servcie.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ship")
public class ShipController {

    @Autowired
    private ShipService shipService;


    @PostMapping("/add")
    public Result addShip(@RequestParam("orderId") Integer orderId) {
        shipService.addShip(orderId);
        return Result.success("添加成功");
    }

    @GetMapping("/{orderId}")
    public Result getShip(@PathVariable("orderId") Integer orderId) {
        Ship ship = shipService.getShip(orderId);
        return Result.success(ship);
    }
}

package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.servcie.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/add1")
    public Result addMissStraight(@RequestBody MissingBooks missingBooks) {
        purchaseService.addMissStraight(missingBooks);
        return Result.success();
    }

    @PostMapping("/add2")
    public Result addPurchase(@RequestBody Purchase purchase) {
        purchaseService.addPurchase(purchase);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updatePurchase(@RequestParam Integer purchaseOrderId,
                                 @RequestParam String status) {
        purchaseService.updatePurchase(purchaseOrderId, status);
        return Result.success();
    }

    @GetMapping("/{purchaseOrderId}")
    public Result getPurchaseById(@PathVariable("purchaseOrderId") Integer purchaseOrderId) {
        Purchase purchase = purchaseService.getPurchaseById(purchaseOrderId);
        return Result.success(purchase);
    }

    @GetMapping("/missingBooks/{missingBookId}")
    public Result getMissingBooksById(@PathVariable("missingBookId") Integer missingBookId){
        MissingBooks missingBooks = purchaseService.getMissingBooksById(missingBookId);
        return Result.success(missingBooks);
    }
}

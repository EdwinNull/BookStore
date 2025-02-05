package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.mapper.BookMapper;
import com.weblearning.bookstore.mapper.PurchaseMapper;
import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import com.weblearning.bookstore.servcie.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private BookMapper bookMapper;


    @Override
    public void addMissStraight(MissingBooks missingBooks) {
        missingBooks.getRegistrationDate(LocalDateTime.now());
        purchaseMapper.addMissStraight(missingBooks);
    }

    @Override
    public void addPurchase(Purchase purchase) {
        purchase.setOrderDate(LocalDateTime.now());
        purchaseMapper.addPurchase(purchase);
    }

    @Override
    public void updatePurchase(Integer purchaseOrderId, String status) {
        purchaseMapper.updatePurchase(purchaseOrderId, status);
        Purchase purchase = purchaseMapper.getPurchaseById(purchaseOrderId);
        if (purchase.getStatus().equals("COMPLETED")) {
            bookMapper.increaseStock(purchase.getBookId(), purchase.getQuantity());
            purchaseMapper.deletePurchase(purchaseOrderId);
            purchaseMapper.deleteMissingBooks(purchase.getBookId());
        }
    }

    @Override
    public Purchase getPurchaseById(Integer purchaseOrderId) {
        Purchase purchase = purchaseMapper.getPurchaseById(purchaseOrderId);
        return purchase;
    }

    @Override
    public MissingBooks getMissingBooksById(Integer missingBookId) {
        MissingBooks missingBooks = purchaseMapper.getMissingBooksById(missingBookId);
        return missingBooks;
    }
}

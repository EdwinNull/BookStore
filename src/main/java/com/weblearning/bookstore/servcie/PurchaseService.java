package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;

public interface PurchaseService {

    void addMissStraight(MissingBooks missingBooks);

    void addPurchase(Purchase purchase);

    void updatePurchase(Integer purchaseOrderId,String status);

    Purchase getPurchaseById(Integer purchaseOrderId);

    MissingBooks getMissingBooksById(Integer missingBookId);
}

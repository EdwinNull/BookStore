package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseMapper {
    void addMissStraight(MissingBooks missingBooks);

    void addPurchase(Purchase purchase);

    Purchase getPurchaseById(Integer purchaseOrderId);

    void updatePurchase(Integer purchaseOrderId, String status);

    void deletePurchase(Integer purchaseOrderId);

    void deleteMissingBooks(Integer bookId);

    MissingBooks getMissingBooksById(Integer missingBookId);
}

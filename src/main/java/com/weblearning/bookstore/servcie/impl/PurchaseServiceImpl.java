package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.mapper.BookMapper;
import com.weblearning.bookstore.mapper.PurchaseMapper;
import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import com.weblearning.bookstore.servcie.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购订单服务实现类
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public void addMissStraight(MissingBooks missingBooks) {
        missingBooks.setRegistrationDate(LocalDateTime.now());
        purchaseMapper.addMissStraight(missingBooks);
    }

    @Override
    public void addPurchase(Purchase purchase) {
        purchase.setOrderDate(LocalDateTime.now());
        // 计算总金额
        if (purchase.getPrice() != null && purchase.getQuantity() != null) {
            purchase.setTotalAmount(purchase.getPrice() * purchase.getQuantity());
        }
        purchaseMapper.addPurchase(purchase);
    }

    @Override
    public void updatePurchase(Integer purchaseOrderId, String status) {
        purchaseMapper.updatePurchase(purchaseOrderId, status);

        // 如果状态变为已完成，增加库存
        if ("completed".equals(status)) {
            Purchase purchase = purchaseMapper.getPurchaseById(purchaseOrderId);
            if (purchase != null) {
                bookMapper.increaseStock(purchase.getBookId(), purchase.getQuantity());
                // 删除相关的缺书记录
                purchaseMapper.deleteMissingBooks(purchase.getBookId());
            }
        }
    }

    @Override
    public Purchase getPurchaseById(Integer purchaseOrderId) {
        return purchaseMapper.getPurchaseById(purchaseOrderId);
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseMapper.getAllPurchases();
    }

    @Override
    public List<Purchase> getPurchasesBySupplierId(Integer supplierId) {
        return purchaseMapper.getPurchasesBySupplierId(supplierId);
    }

    @Override
    public List<Purchase> getPurchasesByStatus(String status) {
        return purchaseMapper.getPurchasesByStatus(status);
    }

    @Override
    public void deletePurchase(Integer purchaseOrderId) {
        purchaseMapper.deletePurchase(purchaseOrderId);
    }

    @Override
    public Map<String, Object> getSupplierOrderStats(Integer supplierId) {
        return purchaseMapper.getSupplierOrderStats(supplierId);
    }

    @Override
    public MissingBooks getMissingBooksById(Integer missingBookId) {
        return purchaseMapper.getMissingBooksById(missingBookId);
    }

    @Override
    public List<MissingBooks> getAllMissingBooks() {
        return purchaseMapper.getAllMissingBooks();
    }

    @Override
    public void deleteMissingBooks(Integer bookId) {
        purchaseMapper.deleteMissingBooks(bookId);
    }
}

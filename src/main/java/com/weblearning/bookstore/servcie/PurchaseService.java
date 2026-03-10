package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;

import java.util.List;
import java.util.Map;

/**
 * 采购订单服务接口
 */
public interface PurchaseService {

    /**
     * 添加缺书登记
     */
    void addMissStraight(MissingBooks missingBooks);

    /**
     * 添加采购订单
     */
    void addPurchase(Purchase purchase);

    /**
     * 更新采购订单状态
     */
    void updatePurchase(Integer purchaseOrderId, String status);

    /**
     * 根据ID获取采购订单
     */
    Purchase getPurchaseById(Integer purchaseOrderId);

    /**
     * 获取所有采购订单列表
     */
    List<Purchase> getAllPurchases();

    /**
     * 根据供应商ID获取采购订单列表
     */
    List<Purchase> getPurchasesBySupplierId(Integer supplierId);

    /**
     * 根据状态获取采购订单列表
     */
    List<Purchase> getPurchasesByStatus(String status);

    /**
     * 删除采购订单
     */
    void deletePurchase(Integer purchaseOrderId);

    /**
     * 获取供应商的订单统计
     */
    Map<String, Object> getSupplierOrderStats(Integer supplierId);

    /**
     * 获取缺书登记
     */
    MissingBooks getMissingBooksById(Integer missingBookId);

    /**
     * 获取所有缺书登记列表
     */
    List<MissingBooks> getAllMissingBooks();

    /**
     * 删除缺书记录
     */
    void deleteMissingBooks(Integer bookId);
}

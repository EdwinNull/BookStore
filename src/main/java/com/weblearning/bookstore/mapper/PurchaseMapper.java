package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.MissingBooks;
import com.weblearning.bookstore.pojo.Purchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 采购订单数据访问接口
 */
@Mapper
public interface PurchaseMapper {

    // ==================== 采购订单相关 ====================

    /**
     * 添加采购订单
     */
    void addPurchase(Purchase purchase);

    /**
     * 更新采购订单状态
     */
    void updatePurchase(@Param("purchaseOrderId") Integer purchaseOrderId, @Param("status") String status);

    /**
     * 更新采购订单（完整更新）
     */
    void updatePurchaseFull(Purchase purchase);

    /**
     * 删除采购订单
     */
    void deletePurchase(Integer purchaseOrderId);

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
     * 获取供应商的订单统计
     */
    Map<String, Object> getSupplierOrderStats(Integer supplierId);

    // ==================== 缺书登记相关 ====================

    /**
     * 添加缺书登记
     */
    void addMissStraight(MissingBooks missingBooks);

    /**
     * 根据ID获取缺书登记
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

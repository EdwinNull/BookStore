/**
 * 采购相关 API
 */
import { get, post, put } from '@/utils/request';
import type { Purchase, MissingBook } from '@/types';

/**
 * 获取采购订单列表
 */
export function getPurchaseList(): Promise<Purchase[]> {
  return get('/purchases/list');
}

/**
 * 根据状态获取采购订单列表
 */
export function getPurchasesByStatus(status: string): Promise<Purchase[]> {
  return get(`/purchases/status/${status}`);
}

/**
 * 添加缺书登记
 */
export function addMissingBook(data: Partial<MissingBook>): Promise<void> {
  return post('/purchases/add1', data);
}

/**
 * 获取缺书登记列表
 */
export function getMissingBookList(): Promise<MissingBook[]> {
  return get('/purchases/missingBooks/list');
}

/**
 * 添加采购订单
 */
export function addPurchase(data: Partial<Purchase>): Promise<void> {
  return post('/purchases/add2', data);
}

/**
 * 更新采购订单状态
 */
export function updatePurchaseStatus(purchaseOrderId: number, status: string): Promise<void> {
  return put(`/purchases/update?purchaseOrderId=${purchaseOrderId}&status=${status}`);
}

/**
 * 获取采购订单详情
 */
export function getPurchaseById(purchaseOrderId: number): Promise<Purchase> {
  return get(`/purchases/${purchaseOrderId}`);
}

/**
 * 获取缺书登记详情
 */
export function getMissingBookById(missingBookId: number): Promise<MissingBook> {
  return get(`/purchases/missingBooks/${missingBookId}`);
}

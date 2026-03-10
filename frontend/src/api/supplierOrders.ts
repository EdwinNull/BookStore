/**
 * 供应商订单相关 API
 */
import { get, post } from '@/utils/request';
import type { Purchase, Book } from '@/types';

/**
 * 采购订单详情响应
 */
export interface PurchaseOrderDetail {
  order: Purchase;
  book: Book;
}

/**
 * 订单统计
 */
export interface OrderStats {
  pending: number;
  confirmed: number;
  shipped: number;
  completed: number;
  rejected: number;
}

/**
 * 获取供应商的采购订单列表
 */
export function getSupplierOrders(): Promise<{ orders: Purchase[]; total: number }> {
  return get('/api/supplier/orders');
}

/**
 * 获取采购订单详情
 */
export function getSupplierOrderDetail(orderId: number): Promise<PurchaseOrderDetail> {
  return get(`/api/supplier/orders/${orderId}`);
}

/**
 * 确认接单
 */
export function confirmOrder(orderId: number): Promise<void> {
  return post(`/api/supplier/orders/${orderId}/confirm`);
}

/**
 * 发货
 */
export function shipOrder(orderId: number, trackingNumber?: string, carrier?: string): Promise<void> {
  const params = new URLSearchParams();
  if (trackingNumber) params.append('trackingNumber', trackingNumber);
  if (carrier) params.append('carrier', carrier);
  const query = params.toString() ? `?${params.toString()}` : '';
  return post(`/api/supplier/orders/${orderId}/ship${query}`);
}

/**
 * 拒绝订单
 */
export function rejectOrder(orderId: number, reason?: string): Promise<void> {
  const query = reason ? `?reason=${encodeURIComponent(reason)}` : '';
  return post(`/api/supplier/orders/${orderId}/reject${query}`);
}

/**
 * 获取订单统计
 */
export function getOrderStats(): Promise<OrderStats> {
  return get('/api/supplier/orders/stats');
}

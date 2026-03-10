/**
 * 订单相关 API
 */
import { get, post } from '@/utils/request';
import type { Order, OrderDetailResponse, OrderRequest, PageBean } from '@/types';

/**
 * 创建订单
 */
export function createOrder(order: OrderRequest): Promise<void> {
  return post('/api/order/add', order);
}

/**
 * 获取订单基本信息
 */
export function getOrder(orderId: number): Promise<Order> {
  return get(`/api/order/get/${orderId}`);
}

/**
 * 获取订单详细信息
 */
export function getOrderDetail(orderId: number): Promise<OrderDetailResponse> {
  return get(`/api/order/detail/${orderId}`);
}

/**
 * 获取当前用户订单列表
 */
export function getMyOrders(params?: { pageNum?: number; pageSize?: number }): Promise<PageBean<Order>> {
  return get('/api/order/list', { params });
}

/**
 * 获取所有订单（管理员）
 */
export function getAllOrders(params?: { pageNum?: number; pageSize?: number; status?: string }): Promise<PageBean<Order>> {
  return get('/api/order/all', { params });
}

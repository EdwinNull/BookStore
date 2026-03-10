/**
 * 供应商相关 API
 */
import { get, post, put, del } from '@/utils/request';
import type { Supplier } from '@/types';

/**
 * 添加供应商
 */
export function addSupplier(supplier: Partial<Supplier>): Promise<void> {
  return post('/api/supplier/add', supplier);
}

/**
 * 更新供应商
 */
export function updateSupplier(supplier: Partial<Supplier>): Promise<void> {
  return put('/api/supplier/update', supplier);
}

/**
 * 删除供应商
 */
export function deleteSupplier(id: number): Promise<void> {
  return del(`/api/supplier/delete/${id}`);
}

/**
 * 根据名称查询供应商
 */
export function findSupplierByName(name: string): Promise<Supplier> {
  return get(`/api/supplier/findByName/${encodeURIComponent(name)}`);
}

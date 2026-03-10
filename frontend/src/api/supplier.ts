/**
 * 供应商相关 API
 * 注意：管理员操作供应商使用 /api/admin/suppliers 路径
 *       供应商自己的操作使用 /api/supplier/auth 路径
 */
import { get, post, put, del } from '@/utils/request';
import type { Supplier } from '@/types';

// ==================== 管理员操作供应商接口 ====================

/**
 * 获取所有供应商列表（管理员）
 */
export function getSuppliers(): Promise<Supplier[]> {
  return get('/api/admin/suppliers/list');
}

/**
 * 获取待审核供应商列表（管理员）
 */
export function getPendingSuppliers(): Promise<Supplier[]> {
  return get('/api/admin/suppliers/pending');
}

/**
 * 获取供应商详情（管理员）
 */
export function getSupplierById(id: number): Promise<Supplier> {
  return get(`/api/admin/suppliers/${id}`);
}

/**
 * 添加供应商（管理员）
 */
export function addSupplier(supplier: Partial<Supplier>): Promise<void> {
  return post('/api/admin/suppliers/add', supplier);
}

/**
 * 更新供应商（管理员）
 */
export function updateSupplier(supplier: Partial<Supplier>): Promise<void> {
  return put('/api/admin/suppliers/update', supplier);
}

/**
 * 删除供应商（管理员）
 */
export function deleteSupplier(id: number): Promise<void> {
  return del(`/api/admin/suppliers/delete/${id}`);
}

/**
 * 更新供应商状态/审核（管理员）
 */
export function updateSupplierStatus(id: number, status: 'active' | 'inactive' | 'pending'): Promise<void> {
  return put(`/api/admin/suppliers/status/${id}?status=${status}`);
}

/**
 * 根据名称查询供应商（管理员）
 */
export function findSupplierByName(name: string): Promise<Supplier> {
  return get(`/api/admin/suppliers/findByName/${encodeURIComponent(name)}`);
}

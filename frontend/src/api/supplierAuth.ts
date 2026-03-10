/**
 * 供应商相关 API
 */
import { get, post, put, patch } from '@/utils/request';
import type {
  Supplier,
  SupplierLoginParams,
  SupplierRegisterParams,
  SupplierProfileResponse,
} from '@/types';

// ==================== 供应商认证相关 ====================

/**
 * 供应商登录
 * @returns JWT token
 */
export function supplierLogin(params: SupplierLoginParams): Promise<string> {
  return post('/api/supplier/auth/login', params);
}

/**
 * 供应商注册申请
 * 注册后需要管理员审核
 */
export function supplierRegister(params: SupplierRegisterParams): Promise<string> {
  return post('/api/supplier/auth/register', params);
}

/**
 * 获取当前登录供应商信息
 */
export function getSupplierProfile(): Promise<SupplierProfileResponse> {
  return get('/api/supplier/auth/profile');
}

/**
 * 更新供应商信息
 */
export function updateSupplierProfile(supplier: Partial<Supplier>): Promise<void> {
  return put('/api/supplier/auth/profile', supplier);
}

/**
 * 修改供应商密码
 */
export function updateSupplierPassword(oldPwd: string, newPwd: string, confirmPwd: string): Promise<void> {
  return patch(`/api/supplier/auth/password?oldPwd=${oldPwd}&newPwd=${newPwd}&confirmPwd=${confirmPwd}`);
}

/**
 * 供应商登出
 */
export function supplierLogout(): Promise<void> {
  return post('/api/supplier/auth/logout');
}

// ==================== 管理员操作供应商相关 ====================

/**
 * 获取待审核供应商列表（管理员）
 */
export function getPendingSuppliers(): Promise<Supplier[]> {
  return get('/api/supplier/auth/admin/pending');
}

/**
 * 获取所有供应商列表（管理员）
 */
export function getAllSuppliers(): Promise<Supplier[]> {
  return get('/api/supplier/auth/admin/all');
}

/**
 * 审核供应商（管理员）
 * @param supplierId 供应商ID
 * @param status 状态：active-通过，inactive-拒绝
 */
export function auditSupplier(supplierId: number, status: 'active' | 'inactive'): Promise<void> {
  return post(`/api/supplier/auth/admin/audit/${supplierId}?status=${status}`);
}

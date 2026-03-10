/**
 * 管理员相关 API
 */
import { get, post, put } from '@/utils/request';
import type { User, PageBean } from '@/types';

/**
 * 管理员登录
 */
export function adminLogin(params: { username: string; password: string }): Promise<string> {
  return post('/api/admin/login', params);
}

/**
 * 获取用户列表（分页）
 */
export function getUserList(params?: {
  pageNum?: number;
  pageSize?: number;
  username?: string;
  role?: string;
}): Promise<PageBean<User>> {
  return get('/api/admin/users', { params });
}

/**
 * 获取所有用户
 */
export function getAllUsers(): Promise<User[]> {
  return get('/api/admin/users/all');
}

/**
 * 添加用户余额
 */
export function addUserBalance(params: { userId: number; balance: number }): Promise<void> {
  return put('/api/admin/addBalance', params);
}

/**
 * 获取用户详情
 */
export function getUserDetail(userId: number): Promise<User> {
  return get(`/api/admin/show/${userId}`);
}

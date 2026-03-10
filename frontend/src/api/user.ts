/**
 * 用户相关 API
 */
import { get, post, put, patch } from '@/utils/request';
import type {
  User,
  LoginParams,
  RegisterParams,
  UpdatePwdParams,
} from '@/types';

/**
 * 用户登录
 */
export function login(params: LoginParams): Promise<string> {
  return post('/api/login', params);
}

/**
 * 用户注册
 */
export function register(params: RegisterParams): Promise<void> {
  return post('/api/register', params);
}

/**
 * 获取当前用户信息
 */
export function getUserInfo(): Promise<User> {
  return get('/api/user/userInfo');
}

/**
 * 更新用户信息
 */
export function updateUser(user: Partial<User>): Promise<void> {
  return put('/api/user/update', user);
}

/**
 * 修改密码
 */
export function updatePassword(params: UpdatePwdParams): Promise<void> {
  return patch('/api/user/updatePwd', params);
}

import request from '@/utils/request'
import type { User, LoginRequest, RegisterRequest, ApiResponse } from '@/types'

export const userApi = {
  // 用户注册
  register(data: RegisterRequest) {
    return request.post<ApiResponse<string>>('/register', data)
  },

  // 用户登录
  login(data: LoginRequest) {
    return request.post<ApiResponse<string>>('/login', data)
  },

  // 管理员登录
  adminLogin(data: LoginRequest) {
    return request.post<ApiResponse<string>>('/admin/login', data)
  },

  // 获取用户信息
  getUserInfo() {
    return request.get<ApiResponse<User>>('/user/userInfo')
  },

  // 更新用户信息
  updateUser(data: Partial<User>) {
    return request.put<ApiResponse<string>>('/user/update', data)
  },

  // 修改密码
  updatePassword(data: { old_pwd: string; new_pwd: string; re_pwd: string }) {
    return request.patch<ApiResponse<string>>('/user/updatePwd', data)
  }
}
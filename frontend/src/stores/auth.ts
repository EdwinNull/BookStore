import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest, RegisterRequest } from '@/types'
import { userApi } from '@/api/user'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'admin')
  const username = computed(() => user.value?.username || '')
  const userId = computed(() => user.value?.user_id || 0)

  // 初始化用户信息
  const initUserInfo = async () => {
    if (!token.value) return

    try {
      const response = await userApi.getUserInfo()
      user.value = response.data
    } catch (error) {
      // Token无效，清除登录状态
      logout()
    }
  }

  // 用户登录
  const login = async (loginData: LoginRequest) => {
    loading.value = true
    try {
      const response = await userApi.login(loginData)
      token.value = response.data
      localStorage.setItem('token', response.data)

      // 获取用户信息
      await initUserInfo()

      // 根据用户角色跳转到不同页面
      if (isAdmin.value) {
        router.push('/admin/dashboard')
      } else {
        router.push('/dashboard')
      }

      return { success: true }
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || '登录失败'
      }
    } finally {
      loading.value = false
    }
  }

  // 用户注册
  const register = async (registerData: RegisterRequest) => {
    loading.value = true
    try {
      const response = await userApi.register(registerData)
      return { success: true, message: response.message }
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || '注册失败'
      }
    } finally {
      loading.value = false
    }
  }

  // 管理员登录
  const adminLogin = async (loginData: LoginRequest) => {
    loading.value = true
    try {
      const response = await userApi.adminLogin(loginData)
      token.value = response.data
      localStorage.setItem('token', response.data)

      // 获取用户信息
      await initUserInfo()

      router.push('/admin/dashboard')
      return { success: true }
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || '管理员登录失败'
      }
    } finally {
      loading.value = false
    }
  }

  // 更新用户信息
  const updateUser = async (userData: Partial<User>) => {
    try {
      await userApi.updateUser(userData)
      if (user.value) {
        user.value = { ...user.value, ...userData }
      }
      return { success: true }
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || '更新失败'
      }
    }
  }

  // 修改密码
  const changePassword = async (oldPassword: string, newPassword: string) => {
    try {
      await userApi.updatePassword({
        old_pwd: oldPassword,
        new_pwd: newPassword,
        re_pwd: newPassword
      })
      return { success: true, message: '密码修改成功' }
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || '密码修改失败'
      }
    }
  }

  // 退出登录
  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  // 初始化
  if (token.value) {
    initUserInfo()
  }

  return {
    // 状态
    user,
    token,
    loading,

    // 计算属性
    isAuthenticated,
    isAdmin,
    username,
    userId,

    // 方法
    initUserInfo,
    login,
    register,
    adminLogin,
    updateUser,
    changePassword,
    logout
  }
})
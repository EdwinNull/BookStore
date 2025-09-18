import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')

    if (token && config.headers) {
      config.headers['Authorization'] = token
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 如果返回的code不是200，说明有错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未登录或token过期
      if (res.code === 401) {
        const authStore = useAuthStore()
        authStore.logout()
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  (error) => {
    // 处理网络错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未登录或登录已过期')
          const authStore = useAuthStore()
          authStore.logout()
          break
        case 403:
          ElMessage.error('没有权限访问')
          router.push('/unauthorized')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      // 请求配置出错
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export default request
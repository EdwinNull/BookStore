/**
 * Axios 实例封装
 * 统一处理请求拦截、响应拦截、错误处理
 * 支持用户token和供应商token
 */
import axios, { type AxiosError, type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios';
import type { Result } from '@/types';

// 创建 axios 实例
const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 添加 Token
instance.interceptors.request.use(
  (config) => {
    // 根据请求URL判断使用哪种token
    const url = config.url || '';

    // 供应商接口使用 supplier_token
    if (url.startsWith('/api/supplier/')) {
      const supplierToken = localStorage.getItem('supplier_token');
      if (supplierToken) {
        config.headers.Authorization = `Bearer ${supplierToken}`;
      }
    } else {
      // 其他接口使用普通用户 token
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    // 调试：打印请求信息
    console.log('[Request]', config.method?.toUpperCase(), config.url, config.data);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一处理响应和错误
instance.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    // 调试：打印响应信息
    console.log('[Response]', response.status, response.config.url, response.data);

    const { data } = response;

    // 业务状态码判断
    if (data.code === 0) {
      return response;
    }

    // 业务错误
    console.error('[Business Error]', data.code, data.message);
    return Promise.reject(new Error(data.message || '请求失败'));
  },
  (error: AxiosError<Result>) => {
    // 调试：打印错误信息
    console.error('[HTTP Error]', error.message, error.response?.status, error.response?.data);

    // HTTP 错误处理
    if (error.response) {
      const { status, data, config } = error.response;
      const url = config?.url || '';

      // 判断是否是供应商接口
      const isSupplierApi = url.startsWith('/api/supplier/');

      switch (status) {
        case 401:
          // 未授权，根据接口类型清除对应 token 并跳转登录
          if (isSupplierApi) {
            localStorage.removeItem('supplier_token');
            localStorage.removeItem('supplier_info');
            window.location.href = '/supplier/login';
          } else {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
          }
          return Promise.reject(new Error('登录已过期，请重新登录'));
        case 403:
          return Promise.reject(new Error('没有权限访问'));
        case 404:
          return Promise.reject(new Error('请求的资源不存在'));
        case 500:
          return Promise.reject(new Error('服务器错误'));
        default:
          return Promise.reject(new Error(data?.message || `请求失败 (${status})`));
      }
    }

    // 网络错误
    if (error.message.includes('timeout')) {
      return Promise.reject(new Error('请求超时，请稍后重试'));
    }

    if (error.message.includes('Network Error')) {
      return Promise.reject(new Error('网络连接失败，请检查网络'));
    }

    return Promise.reject(error);
  }
);

/**
 * 封装 GET 请求
 */
export async function get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await instance.get<Result<T>>(url, config);
  console.log('[GET Result]', url, '->', response.data.data);
  return response.data.data;
}

/**
 * 封装 POST 请求
 */
export async function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  const response = await instance.post<Result<T>>(url, data, config);
  console.log('[POST Result]', url, '->', response.data.data);
  return response.data.data;
}

/**
 * 封装 PUT 请求
 */
export async function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  const response = await instance.put<Result<T>>(url, data, config);
  return response.data.data;
}

/**
 * 封装 PATCH 请求
 */
export async function patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  const response = await instance.patch<Result<T>>(url, data, config);
  return response.data.data;
}

/**
 * 封装 DELETE 请求
 */
export async function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await instance.delete<Result<T>>(url, config);
  return response.data.data;
}

export default instance;

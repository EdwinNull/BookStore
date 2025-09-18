// 用户类型定义
export interface User {
  user_id: number
  username: string
  password: string
  name: string
  address?: string
  account_balance: number
  credit_level: number
  role: 'admin' | 'user'
  discount: number
  over_balance: number
  created_at: string
  updated_at: string
}

// 图书类型定义
export interface Book {
  book_id: number
  title: string
  author1?: string
  author2?: string
  author3?: string
  author4?: string
  publisher?: string
  price: number
  keywords?: string
  table_of_contents?: string
  cover_image?: string
  stock_quantity: number
  supplier_id?: number
  series_id?: number
  storage_location?: string
  low_quantity_flag: number
  created_at: string
  updated_at: string
}

// 订单类型定义
export interface Order {
  order_id: number
  user_id: number
  total_price: number
  order_date: string
  status: 'pending' | 'confirmed' | 'processing' | 'shipped' | 'delivered' | 'cancelled'
  created_at: string
  updated_at: string
}

export interface OrderDetail {
  id: number
  order_id: number
  book_id: number
  quantity: number
  price: number
  created_at: string
}

// 供应商类型定义
export interface Supplier {
  supplier_id: number
  name: string
  address?: string
  contact_info?: string
  created_at: string
  updated_at: string
}

// 分页类型定义
export interface PageBean<T> {
  total: number
  items: T[]
}

// API响应类型定义
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 登录请求类型
export interface LoginRequest {
  username: string
  password: string
}

// 注册请求类型
export interface RegisterRequest {
  username: string
  password: string
}

// 购物车项目类型
export interface CartItem {
  book_id: number
  title: string
  price: number
  quantity: number
  cover_image?: string
}

// 订单创建请求类型
export interface CreateOrderRequest {
  book_items: {
    book_id: number
    quantity: number
  }[]
}

// 搜索条件类型
export interface BookSearchParams {
  title?: string
  keywords?: string
  author1?: string
  author2?: string
  author3?: string
  author4?: string
  publisher?: string
  pageNum?: number
  pageSize?: number
}
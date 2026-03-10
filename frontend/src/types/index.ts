/**
 * 后端API相关类型定义
 * 根据后端接口文档定义的类型
 */

// ==================== 通用类型 ====================

/**
 * 统一响应结果
 */
export interface Result<T = unknown> {
  code: number;       // 0=成功, 1=失败
  message: string;
  data: T;
}

/**
 * 分页结果
 */
export interface PageBean<T> {
  total: number;
  items: T[];
}

/**
 * 分页查询参数
 */
export interface PageParams {
  pageNum?: number;
  pageSize?: number;
}

// ==================== 用户相关 ====================

/**
 * 用户实体
 */
export interface User {
  userId: number;
  username: string;
  name: string;
  address: string;
  accountBalance: number;
  creditLevel: number;
  role: string;       // 'user' | 'admin'
  discount: number;
  overBalance: number;
}

/**
 * 登录请求参数
 */
export interface LoginParams {
  username: string;
  password: string;
}

/**
 * 注册请求参数
 */
export interface RegisterParams {
  username: string;
  password: string;
}

/**
 * 修改密码参数
 */
export interface UpdatePwdParams {
  old_pwd: string;
  new_pwd: string;
  re_pwd: string;
}

// ==================== 图书相关 ====================

/**
 * 图书实体
 */
export interface Book {
  bookId: number;
  title: string;
  author1: string;
  author2?: string;
  author3?: string;
  author4?: string;
  publisher: string;
  price: number;
  keywords?: string;
  tableOfContents?: string;
  coverImage?: string;
  stockQuantity: number;
  supplierId?: number;
  seriesId?: number;
  storageLocation?: string;
  lowQuantityFlag?: number;
}

/**
 * 图书搜索参数
 */
export interface BookSearchParams {
  title?: string;
  keywords?: string;
  author1?: string;
  author2?: string;
  author3?: string;
  author4?: string;
  publisher?: string;
}

/**
 * 图书列表查询参数
 */
export interface BookListParams extends PageParams {
  supplierId?: number;
  title?: string;
}

// ==================== 订单相关 ====================

/**
 * 订单实体
 */
export interface Order {
  orderId: number;
  userId: number;
  totalPrice: number;
  orderDate: string;
  status: string;     // pending, shipped, completed, cancelled
}

/**
 * 订单详情项（后端返回的完整结构）
 */
export interface OrderItem {
  id: number;
  orderId: number;
  bookId: number;
  quantity: number;
  price: number;
}

/**
 * 创建订单项（简化结构，用于创建订单）
 */
export interface CreateOrderItem {
  bookId: number;
  quantity: number;
  price: number;
}

/**
 * 创建订单请求
 */
export interface OrderRequest {
  userId: number;
  items: CreateOrderItem[];
}

/**
 * 订单详情响应
 */
export interface OrderDetailResponse {
  userId: number;
  finalPrice: number;
  status: string;
  orderDate: string;
  items: OrderItemResponse[];
}

/**
 * 订单项响应
 */
export interface OrderItemResponse {
  bookId: number;
  quantity: number;
  price: number;
}

/**
 * 订单列表查询参数
 */
export interface OrderListParams extends PageParams {
  status?: string;
}

// ==================== 供应商相关 ====================

/**
 * 供应商实体
 */
export interface Supplier {
  supplierId: number;
  name: string;
  address: string;
  contactInfo: string;
}

/**
 * 供应商图书关联
 */
export interface SupplierBooks {
  supplierBookId?: number;
  supplierName?: number;
  bookName?: number;
  quantity: number;
  price: number;
}

// ==================== 系列相关 ====================

/**
 * 系列实体
 */
export interface Series {
  seriesId: number;
  seriesName: string;
}

// ==================== 采购相关 ====================

/**
 * 采购订单
 */
export interface Purchase {
  purchaseOrderId: number;
  supplierId: number;
  bookId: number;
  quantity: number;
  price: number;
  orderDate: string;
  status: string;
  updateDate: string;
}

/**
 * 缺书登记
 */
export interface MissingBook {
  missingBooksId: number;
  bookId: number;
  bookName: string;
  supplierId: number;
  quantity: number;
  registrationDate: string;
  userId: number;
  publisher: string;
  requestedQuantity: number;
}

// ==================== 物流相关 ====================

/**
 * 物流实体
 */
export interface Ship {
  shipmentId: number;
  orderId: number;
  shippingAddress: string;
  shippingDate: string;
  trackingNumber: string;
  status: string;
}

// ==================== 购物车相关（前端使用）====================

/**
 * 购物车项
 */
export interface CartItem {
  bookId: number;
  book: Book;
  quantity: number;
}

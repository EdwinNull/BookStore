/**
 * 路由配置
 * 使用 React Router v7 的类型定义
 */
import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { MainLayout, AdminLayout, SupplierLayout } from '@/components/layout';
import { LoginPage, RegisterPage, AdminLoginPage } from '@/pages/auth';
import { HomePage, BookListPage, BookDetailPage } from '@/pages/book';
import { CartPage, UserDashboardPage, OrdersPage } from '@/pages/user';
import DiscountRulesPage from '@/pages/user/DiscountRules';
import { AdminDashboard, BookManagement, OrderManagement, UserManagement, SupplierManagement, PurchaseManagement } from '@/pages/admin';
import { SupplierLoginPage, SupplierRegisterPage, SupplierDashboard, SupplierOrders, SupplierProfile } from '@/pages/supplier';
import { NotFoundPage } from '@/pages/NotFound';

// 定义前台路由
const mainRoutes: RouteObject[] = [
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'books', element: <BookListPage /> },
      { path: 'books/:id', element: <BookDetailPage /> },
      { path: 'cart', element: <CartPage /> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      { path: 'user', element: <UserDashboardPage /> },
      { path: 'user/orders', element: <OrdersPage /> },
      { path: 'user/level', element: <DiscountRulesPage /> },
    ],
  },
];

// 定义管理员路由
const adminRoutes: RouteObject[] = [
  // 管理员登录（独立页面）
  {
    path: '/admin/login',
    element: <AdminLoginPage />,
  },
  // 管理员后台路由
  {
    path: '/admin',
    element: <AdminLayout />,
    children: [
      { index: true, element: <AdminDashboard /> },
      { path: 'books', element: <BookManagement /> },
      { path: 'orders', element: <OrderManagement /> },
      { path: 'users', element: <UserManagement /> },
      { path: 'suppliers', element: <SupplierManagement /> },
      { path: 'purchases', element: <PurchaseManagement /> },
    ],
  },
];

// 定义供应商路由
const supplierRoutes: RouteObject[] = [
  // 供应商登录/注册（独立页面，不需要布局）
  {
    path: '/supplier/login',
    element: <SupplierLoginPage />,
  },
  {
    path: '/supplier/register',
    element: <SupplierRegisterPage />,
  },
  // 供应商后台路由（需要登录）
  {
    path: '/supplier',
    element: <SupplierLayout />,
    children: [
      { path: 'dashboard', element: <SupplierDashboard /> },
      { path: 'orders', element: <SupplierOrders /> },
      { path: 'profile', element: <SupplierProfile /> },
    ],
  },
];

// 404 路由
const notFoundRoute: RouteObject = {
  path: '*',
  element: <NotFoundPage />,
};

// 创建路由器
export const router = createBrowserRouter([
  ...mainRoutes,
  ...adminRoutes,
  ...supplierRoutes,
  notFoundRoute,
]);

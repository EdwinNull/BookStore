/**
 * 路由配置
 * 使用 React Router v7 的类型定义
 */
import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { MainLayout, AdminLayout } from '@/components/layout';
import { LoginPage, RegisterPage, AdminLoginPage } from '@/pages/auth';
import { HomePage, BookListPage, BookDetailPage } from '@/pages/book';
import { CartPage, UserDashboardPage, OrdersPage } from '@/pages/user';
import { AdminDashboard, BookManagement, OrderManagement, UserManagement, SupplierManagement, PurchaseManagement } from '@/pages/admin';
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

// 404 路由
const notFoundRoute: RouteObject = {
  path: '*',
  element: <NotFoundPage />,
};

// 创建路由器
export const router = createBrowserRouter([
  ...mainRoutes,
  ...adminRoutes,
  notFoundRoute,
]);

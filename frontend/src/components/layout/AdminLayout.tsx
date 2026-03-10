/**
 * 管理员布局组件
 * 包含路由保护：必须登录且为管理员才能访问
 */
import { useEffect } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { Header } from './Header';
import { useAuthStore } from '@/stores';
import { Loading } from '@/components/common';

const menuItems = [
  { path: '/admin', label: '仪表盘', icon: '📊' },
  { path: '/admin/books', label: '图书管理', icon: '📚' },
  { path: '/admin/orders', label: '订单管理', icon: '📦' },
  { path: '/admin/users', label: '用户管理', icon: '👥' },
  { path: '/admin/suppliers', label: '供应商管理', icon: '🏭' },
  { path: '/admin/purchases', label: '采购管理', icon: '🛒' },
];

export function AdminLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isAdmin, token } = useAuthStore();

  // 路由保护：检查登录状态和管理员权限
  useEffect(() => {
    // 未登录，跳转到管理员登录页
    if (!token) {
      navigate('/admin/login', { replace: true });
      return;
    }

    // 已登录但不是管理员，跳转到普通用户首页
    if (token && user && !isAdmin) {
      navigate('/', { replace: true });
      return;
    }
  }, [token, user, isAdmin, navigate]);

  // 加载中或未验证时显示加载状态
  if (!token || !user || !isAdmin) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <Loading text="验证权限中..." />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <div className="flex-1 flex">
        {/* 侧边栏 */}
        <aside className="w-64 bg-gray-800 text-white flex-shrink-0">
          <nav className="p-4">
            <ul className="space-y-2">
              {menuItems.map((item) => (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    className={`
                      flex items-center gap-3 px-4 py-2 rounded-md
                      transition-colors
                      ${location.pathname === item.path
                        ? 'bg-blue-500 text-white'
                        : 'text-gray-300 hover:bg-gray-700'
                      }
                    `}
                  >
                    <span>{item.icon}</span>
                    <span>{item.label}</span>
                  </Link>
                </li>
              ))}
            </ul>
          </nav>
        </aside>

        {/* 内容区域 */}
        <main className="flex-1 bg-gray-100 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

/**
 * 供应商布局组件
 * 包含路由保护：必须以供应商身份登录才能访问
 */
import { useEffect, useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { Loading } from '@/components/common';
import { getSupplierProfile } from '@/api/supplierAuth';
import type { SupplierProfileResponse } from '@/types';

// 供应商菜单项
const menuItems = [
  { path: '/supplier/dashboard', label: '控制台', icon: '📊' },
  { path: '/supplier/orders', label: '采购订单', icon: '📦' },
  { path: '/supplier/profile', label: '账号信息', icon: '👤' },
];

export function SupplierLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const [supplier, setSupplier] = useState<SupplierProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);

  // 路由保护：检查供应商登录状态
  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('supplier_token');

      // 未登录，跳转到供应商登录页
      if (!token) {
        navigate('/supplier/login', { replace: true });
        return;
      }

      try {
        // 获取供应商信息
        const supplierInfo = await getSupplierProfile();
        setSupplier(supplierInfo);
      } catch (error) {
        console.error('[SupplierLayout] 获取供应商信息失败:', error);
        // token 无效，清除并跳转登录页
        localStorage.removeItem('supplier_token');
        localStorage.removeItem('supplier_info');
        navigate('/supplier/login', { replace: true });
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, [navigate]);

  // 登出
  const handleLogout = () => {
    localStorage.removeItem('supplier_token');
    localStorage.removeItem('supplier_info');
    navigate('/supplier/login');
  };

  // 加载中
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <Loading text="验证登录状态..." />
      </div>
    );
  }

  // 未登录
  if (!supplier) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <Loading text="跳转登录页..." />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-gray-100">
      {/* 顶部导航栏 */}
      <header className="bg-green-600 text-white shadow-md">
        <div className="px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <h1 className="text-xl font-bold">供应商管理平台</h1>
            <span className="text-green-200 text-sm">|</span>
            <span className="text-green-100">{supplier.name}</span>
          </div>
          <div className="flex items-center gap-4">
            <Link to="/" className="text-green-100 hover:text-white text-sm">
              返回首页
            </Link>
            <button
              onClick={handleLogout}
              className="text-green-100 hover:text-white text-sm"
            >
              退出登录
            </button>
          </div>
        </div>
      </header>

      <div className="flex-1 flex">
        {/* 侧边栏 */}
        <aside className="w-56 bg-white shadow-sm flex-shrink-0">
          <nav className="p-4">
            <ul className="space-y-1">
              {menuItems.map((item) => (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    className={`
                      flex items-center gap-3 px-4 py-2 rounded-md
                      transition-colors text-sm
                      ${location.pathname === item.path
                        ? 'bg-green-50 text-green-600 font-medium'
                        : 'text-gray-600 hover:bg-gray-50'
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

          {/* 供应商状态 */}
          <div className="p-4 border-t">
            <div className="text-xs text-gray-500 mb-1">账号状态</div>
            <span className={`
              inline-block px-2 py-1 rounded text-xs
              ${supplier.status === 'active' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}
            `}>
              {supplier.status === 'active' ? '正常' : '待审核'}
            </span>
          </div>
        </aside>

        {/* 内容区域 */}
        <main className="flex-1 p-6 overflow-auto">
          <Outlet context={{ supplier }} />
        </main>
      </div>
    </div>
  );
}

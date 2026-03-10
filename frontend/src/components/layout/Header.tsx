/**
 * 头部导航组件
 */
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore, useCartStore } from '@/stores';

export function Header() {
  const { user, isAdmin, logout } = useAuthStore();
  const { getTotalCount } = useCartStore();
  const navigate = useNavigate();
  const location = useLocation();
  const cartCount = getTotalCount();

  // 判断是否在管理员页面
  const isAdminPage = location.pathname.startsWith('/admin');

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="bg-white shadow-sm sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <svg
              className="h-8 w-8 text-blue-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
              />
            </svg>
            <span className="text-xl font-bold text-gray-900">书店</span>
          </Link>

          {/* 导航链接 */}
          <nav className="hidden md:flex items-center gap-6">
            {!isAdminPage ? (
              <>
                <Link
                  to="/"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  首页
                </Link>
                <Link
                  to="/books"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  图书列表
                </Link>
              </>
            ) : (
              <>
                <Link
                  to="/admin"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  仪表盘
                </Link>
                <Link
                  to="/admin/books"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  图书管理
                </Link>
                <Link
                  to="/admin/orders"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  订单管理
                </Link>
                <Link
                  to="/admin/users"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  用户管理
                </Link>
              </>
            )}
          </nav>

          {/* 右侧操作区 */}
          <div className="flex items-center gap-4">
            {/* 购物车（仅用户端显示） */}
            {!isAdminPage && (
              <Link
                to="/cart"
                className="relative text-gray-600 hover:text-blue-500 transition-colors"
              >
                <svg
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
                {cartCount > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">
                    {cartCount > 99 ? '99+' : cartCount}
                  </span>
                )}
              </Link>
            )}

            {/* 用户信息 */}
            {user ? (
              <div className="relative group">
                <button className="flex items-center gap-2 text-gray-600 hover:text-blue-500 transition-colors">
                  <svg
                    className="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                  <span className="hidden sm:inline">{user.username}</span>
                </button>

                {/* 下拉菜单 */}
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all">
                  {!isAdminPage && (
                    <>
                      <Link
                        to="/user"
                        className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                      >
                        个人中心
                      </Link>
                      <Link
                        to="/user/orders"
                        className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                      >
                        我的订单
                      </Link>
                    </>
                  )}
                  {isAdmin && !isAdminPage && (
                    <Link
                      to="/admin"
                      className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                    >
                      管理后台
                    </Link>
                  )}
                  {isAdminPage && (
                    <Link
                      to="/"
                      className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                    >
                      返回前台
                    </Link>
                  )}
                  <hr className="my-1" />
                  <button
                    onClick={handleLogout}
                    className="block w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100"
                  >
                    退出登录
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  登录
                </Link>
                <span className="text-gray-300">|</span>
                <Link
                  to="/register"
                  className="text-gray-600 hover:text-blue-500 transition-colors"
                >
                  注册
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

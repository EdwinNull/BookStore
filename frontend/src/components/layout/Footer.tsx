/**
 * 页脚组件
 */
import { Link } from 'react-router-dom';

export function Footer() {
  return (
    <footer className="bg-gray-800 text-gray-300 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* 关于我们 */}
          <div>
            <h3 className="text-white font-semibold mb-4">关于我们</h3>
            <p className="text-sm">
              这是一个在线书店管理系统，提供图书浏览、搜索、购买等功能。
            </p>
          </div>

          {/* 快速链接 */}
          <div>
            <h3 className="text-white font-semibold mb-4">快速链接</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link to="/" className="hover:text-white transition-colors">
                  首页
                </Link>
              </li>
              <li>
                <Link to="/books" className="hover:text-white transition-colors">
                  图书列表
                </Link>
              </li>
              <li>
                <Link to="/login" className="hover:text-white transition-colors">
                  登录
                </Link>
              </li>
            </ul>
          </div>

          {/* 联系方式 */}
          <div>
            <h3 className="text-white font-semibold mb-4">联系方式</h3>
            <ul className="space-y-2 text-sm">
              <li>客服电话：400-123-4567</li>
              <li>客服邮箱：support@bookstore.com</li>
              <li>工作时间：周一至周五 9:00-18:00</li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-8 border-t border-gray-700 text-center text-sm">
          <p>&copy; {new Date().getFullYear()} 书店管理系统. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}

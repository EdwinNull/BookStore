/**
 * 管理员仪表盘页面
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, Loading } from '@/components/common';
import { getAllOrders } from '@/api/order';
import { getUserList } from '@/api/admin';
import { getBookList } from '@/api/book';

interface DashboardStats {
  totalBooks: number;
  totalUsers: number;
  totalOrders: number;
  pendingOrders: number;
}

export function AdminDashboard() {
  const [stats, setStats] = useState<DashboardStats>({
    totalBooks: 0,
    totalUsers: 0,
    totalOrders: 0,
    pendingOrders: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const [books, users, orders] = await Promise.all([
        getBookList({ pageNum: 1, pageSize: 1 }),
        getUserList({ pageNum: 1, pageSize: 1 }),
        getAllOrders({ pageNum: 1, pageSize: 100 }),
      ]);

      setStats({
        totalBooks: books.total,
        totalUsers: users.total,
        totalOrders: orders.total,
        pendingOrders: orders.items?.filter((o) => o.status === 'pending').length || 0,
      });
    } catch (error) {
      console.error('获取统计数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loading text="加载中..." />;
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">仪表盘</h1>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
          <CardBody className="flex items-center gap-4">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center text-2xl">
              📚
            </div>
            <div>
              <p className="text-sm text-gray-500">图书总数</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalBooks}</p>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardBody className="flex items-center gap-4">
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center text-2xl">
              👥
            </div>
            <div>
              <p className="text-sm text-gray-500">用户总数</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalUsers}</p>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardBody className="flex items-center gap-4">
            <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center text-2xl">
              📦
            </div>
            <div>
              <p className="text-sm text-gray-500">订单总数</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalOrders}</p>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardBody className="flex items-center gap-4">
            <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center text-2xl">
              ⏳
            </div>
            <div>
              <p className="text-sm text-gray-500">待处理订单</p>
              <p className="text-2xl font-bold text-gray-900">{stats.pendingOrders}</p>
            </div>
          </CardBody>
        </Card>
      </div>

      {/* 快捷操作 */}
      <h2 className="text-lg font-semibold text-gray-900 mb-4">快捷操作</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <a
          href="/admin/books"
          className="block p-4 bg-white rounded-lg border border-gray-200 hover:border-blue-500 hover:shadow transition-all"
        >
          <div className="text-2xl mb-2">➕</div>
          <p className="font-medium">添加图书</p>
        </a>
        <a
          href="/admin/orders"
          className="block p-4 bg-white rounded-lg border border-gray-200 hover:border-blue-500 hover:shadow transition-all"
        >
          <div className="text-2xl mb-2">📋</div>
          <p className="font-medium">查看订单</p>
        </a>
        <a
          href="/admin/users"
          className="block p-4 bg-white rounded-lg border border-gray-200 hover:border-blue-500 hover:shadow transition-all"
        >
          <div className="text-2xl mb-2">👤</div>
          <p className="font-medium">用户管理</p>
        </a>
        <a
          href="/admin/suppliers"
          className="block p-4 bg-white rounded-lg border border-gray-200 hover:border-blue-500 hover:shadow transition-all"
        >
          <div className="text-2xl mb-2">🏭</div>
          <p className="font-medium">供应商管理</p>
        </a>
      </div>
    </div>
  );
}

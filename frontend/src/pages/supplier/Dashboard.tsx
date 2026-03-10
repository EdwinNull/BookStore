/**
 * 供应商控制台页面
 */
import { useOutletContext } from 'react-router-dom';
import { Card, CardBody } from '@/components/common';
import type { SupplierProfileResponse } from '@/types';

export function SupplierDashboard() {
  const { supplier } = useOutletContext<{ supplier: SupplierProfileResponse }>();

  // 模拟统计数据（后续接入实际API）
  const stats = [
    { label: '待处理订单', value: 0, color: 'bg-yellow-500' },
    { label: '已完成订单', value: 0, color: 'bg-green-500' },
    { label: '本月收入', value: '¥0', color: 'bg-blue-500' },
    { label: '供应图书', value: 0, color: 'bg-purple-500' },
  ];

  return (
    <div className="space-y-6">
      {/* 欢迎信息 */}
      <div className="bg-white rounded-lg shadow p-6">
        <h1 className="text-2xl font-bold text-gray-900">
          欢迎回来，{supplier.name}
        </h1>
        <p className="text-gray-500 mt-1">
          这是您的供应商管理控制台，在这里您可以管理采购订单和查看业务数据。
        </p>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <Card key={stat.label}>
            <CardBody className="flex items-center gap-4">
              <div className={`w-12 h-12 ${stat.color} rounded-lg flex items-center justify-center`}>
                <span className="text-white text-xl font-bold">
                  {typeof stat.value === 'number' ? stat.value : '¥'}
                </span>
              </div>
              <div>
                <div className="text-2xl font-bold text-gray-900">{stat.value}</div>
                <div className="text-sm text-gray-500">{stat.label}</div>
              </div>
            </CardBody>
          </Card>
        ))}
      </div>

      {/* 快捷操作 */}
      <Card>
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">快捷操作</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <a
              href="/supplier/orders"
              className="p-4 border rounded-lg hover:bg-gray-50 text-center transition-colors"
            >
              <div className="text-2xl mb-2">📦</div>
              <div className="text-sm text-gray-600">查看订单</div>
            </a>
            <a
              href="/supplier/profile"
              className="p-4 border rounded-lg hover:bg-gray-50 text-center transition-colors"
            >
              <div className="text-2xl mb-2">👤</div>
              <div className="text-sm text-gray-600">账号设置</div>
            </a>
            <a
              href="/"
              className="p-4 border rounded-lg hover:bg-gray-50 text-center transition-colors"
            >
              <div className="text-2xl mb-2">🏠</div>
              <div className="text-sm text-gray-600">返回首页</div>
            </a>
            <div className="p-4 border rounded-lg text-center opacity-50">
              <div className="text-2xl mb-2">📈</div>
              <div className="text-sm text-gray-600">数据报表</div>
              <div className="text-xs text-gray-400">即将推出</div>
            </div>
          </div>
        </CardBody>
      </Card>

      {/* 账号信息 */}
      <Card>
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">账号信息</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="flex">
              <span className="text-gray-500 w-24">账号：</span>
              <span className="text-gray-900">{supplier.username}</span>
            </div>
            <div className="flex">
              <span className="text-gray-500 w-24">供应商：</span>
              <span className="text-gray-900">{supplier.name}</span>
            </div>
            <div className="flex">
              <span className="text-gray-500 w-24">联系人：</span>
              <span className="text-gray-900">{supplier.contactPerson || '-'}</span>
            </div>
            <div className="flex">
              <span className="text-gray-500 w-24">联系电话：</span>
              <span className="text-gray-900">{supplier.phone || '-'}</span>
            </div>
            <div className="flex">
              <span className="text-gray-500 w-24">邮箱：</span>
              <span className="text-gray-900">{supplier.email || '-'}</span>
            </div>
            <div className="flex">
              <span className="text-gray-500 w-24">地址：</span>
              <span className="text-gray-900">{supplier.address || '-'}</span>
            </div>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

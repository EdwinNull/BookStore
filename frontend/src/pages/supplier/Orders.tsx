/**
 * 供应商采购订单页面
 * 支持查看订单、确认接单、发货等操作
 */
import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Button, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { getSupplierOrders, confirmOrder, shipOrder, rejectOrder, getOrderStats } from '@/api/supplierOrders';
import type { SupplierProfileResponse, Purchase } from '@/types';
import type { OrderStats } from '@/api/supplierOrders';

// 订单状态配置
const statusConfig: Record<string, { label: string; color: string; bgColor: string }> = {
  pending: { label: '待处理', color: 'text-yellow-700', bgColor: 'bg-yellow-100' },
  confirmed: { label: '已确认', color: 'text-blue-700', bgColor: 'bg-blue-100' },
  shipped: { label: '已发货', color: 'text-purple-700', bgColor: 'bg-purple-100' },
  completed: { label: '已完成', color: 'text-green-700', bgColor: 'bg-green-100' },
  rejected: { label: '已拒绝', color: 'text-red-700', bgColor: 'bg-red-100' },
};

export function SupplierOrders() {
  const { supplier } = useOutletContext<{ supplier: SupplierProfileResponse }>();

  const [activeTab, setActiveTab] = useState<string>('all');
  const [orders, setOrders] = useState<Purchase[]>([]);
  const [stats, setStats] = useState<OrderStats>({
    pending: 0,
    confirmed: 0,
    shipped: 0,
    completed: 0,
    rejected: 0,
  });
  const [loading, setLoading] = useState(false);

  // 标签页配置
  const tabs = [
    { key: 'all', label: '全部订单', count: Object.values(stats).reduce((a, b) => a + b, 0) },
    { key: 'pending', label: '待处理', count: stats.pending },
    { key: 'confirmed', label: '已确认', count: stats.confirmed },
    { key: 'shipped', label: '已发货', count: stats.shipped },
    { key: 'completed', label: '已完成', count: stats.completed },
  ];

  // 加载数据
  const loadData = async () => {
    setLoading(true);
    try {
      const [ordersRes, statsRes] = await Promise.all([
        getSupplierOrders(),
        getOrderStats(),
      ]);
      setOrders(ordersRes.orders || []);
      setStats(statsRes);
    } catch (error) {
      console.error('[Load Orders Error]', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // 确认接单
  const handleConfirm = async (orderId: number) => {
    try {
      await confirmOrder(orderId);
      message.success('接单成功');
      loadData();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '操作失败');
    }
  };

  // 发货
  const handleShip = async (orderId: number) => {
    const trackingNumber = prompt('请输入物流单号（可选）：');
    try {
      await shipOrder(orderId, trackingNumber || undefined);
      message.success('发货成功');
      loadData();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '操作失败');
    }
  };

  // 拒绝订单
  const handleReject = async (orderId: number) => {
    const reason = prompt('请输入拒绝原因（可选）：');
    if (!confirm('确定要拒绝此订单吗？')) return;

    try {
      await rejectOrder(orderId, reason || undefined);
      message.success('已拒绝订单');
      loadData();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '操作失败');
    }
  };

  // 筛选订单
  const filteredOrders = activeTab === 'all'
    ? orders
    : orders.filter(order => order.status === activeTab);

  // 格式化日期
  const formatDate = (dateStr: string) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('zh-CN');
  };

  // 格式化金额
  const formatPrice = (price: number | undefined) => {
    if (price === undefined || price === null) return '-';
    return `¥${price.toFixed(2)}`;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">采购订单</h1>
        <Button onClick={loadData} loading={loading}>
          刷新
        </Button>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        {tabs.slice(1).map((tab) => (
          <Card
            key={tab.key}
            className={`cursor-pointer transition-all ${
              activeTab === tab.key ? 'ring-2 ring-green-500' : ''
            }`}
            onClick={() => setActiveTab(tab.key)}
          >
            <CardBody className="text-center py-4">
              <div className="text-2xl font-bold text-gray-900">{tab.count}</div>
              <div className="text-sm text-gray-500">{tab.label}</div>
            </CardBody>
          </Card>
        ))}
      </div>

      {/* 标签页 */}
      <div className="border-b border-gray-200">
        <nav className="flex gap-8">
          {tabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`
                py-2 px-1 border-b-2 font-medium text-sm
                ${activeTab === tab.key
                  ? 'border-green-500 text-green-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }
              `}
            >
              {tab.label}
              <span className={`
                ml-2 py-0.5 px-2 rounded-full text-xs
                ${activeTab === tab.key ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-600'}
              `}>
                {tab.count}
              </span>
            </button>
          ))}
        </nav>
      </div>

      {/* 订单列表 */}
      <div className="space-y-4">
        {loading ? (
          <Card>
            <CardBody className="text-center py-12 text-gray-500">
              加载中...
            </CardBody>
          </Card>
        ) : filteredOrders.length === 0 ? (
          <Card>
            <CardBody className="text-center py-12 text-gray-500">
              <div className="text-4xl mb-4">📭</div>
              <p>暂无{activeTab === 'all' ? '' : tabs.find(t => t.key === activeTab)?.label}订单</p>
              {activeTab === 'pending' && (
                <p className="text-sm mt-2">当管理员创建采购订单时，您将在此处看到订单信息</p>
              )}
            </CardBody>
          </Card>
        ) : (
          filteredOrders.map((order) => {
            const status = statusConfig[order.status] || statusConfig.pending;
            return (
              <Card key={order.purchaseOrderId}>
                <CardBody>
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      {/* 订单头部 */}
                      <div className="flex items-center gap-4 mb-4">
                        <span className="text-lg font-semibold">订单 #{order.purchaseOrderId}</span>
                        <span className={`px-2 py-1 rounded text-xs ${status.bgColor} ${status.color}`}>
                          {status.label}
                        </span>
                      </div>

                      {/* 订单详情 */}
                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                        <div>
                          <span className="text-gray-500">图书：</span>
                          <span className="text-gray-900">{order.bookTitle || `ID: ${order.bookId}`}</span>
                        </div>
                        <div>
                          <span className="text-gray-500">数量：</span>
                          <span className="text-gray-900">{order.quantity} 本</span>
                        </div>
                        <div>
                          <span className="text-gray-500">单价：</span>
                          <span className="text-gray-900">{formatPrice(order.price)}</span>
                        </div>
                        <div>
                          <span className="text-gray-500">总价：</span>
                          <span className="text-gray-900 font-semibold">
                            {formatPrice(order.totalAmount || (order.price || 0) * (order.quantity || 0))}
                          </span>
                        </div>
                        <div>
                          <span className="text-gray-500">下单时间：</span>
                          <span className="text-gray-900">{formatDate(order.orderDate?.toString() || '')}</span>
                        </div>
                        <div>
                          <span className="text-gray-500">更新时间：</span>
                          <span className="text-gray-900">{formatDate(order.updateDate?.toString() || '')}</span>
                        </div>
                        {order.expectedDeliveryDate && (
                          <div>
                            <span className="text-gray-500">预计到货：</span>
                            <span className="text-gray-900">{formatDate(order.expectedDeliveryDate)}</span>
                          </div>
                        )}
                        {order.remark && (
                          <div className="col-span-2">
                            <span className="text-gray-500">备注：</span>
                            <span className="text-gray-900">{order.remark}</span>
                          </div>
                        )}
                      </div>
                    </div>

                    {/* 操作按钮 */}
                    <div className="flex flex-col gap-2 ml-4">
                      {order.status === 'pending' && (
                        <>
                          <Button
                            size="sm"
                            onClick={() => handleConfirm(order.purchaseOrderId)}
                          >
                            确认接单
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            className="text-red-500 border-red-500 hover:bg-red-50"
                            onClick={() => handleReject(order.purchaseOrderId)}
                          >
                            拒绝
                          </Button>
                        </>
                      )}
                      {order.status === 'confirmed' && (
                        <Button
                          size="sm"
                          onClick={() => handleShip(order.purchaseOrderId)}
                        >
                          发货
                        </Button>
                      )}
                      {order.status === 'shipped' && (
                        <span className="text-sm text-gray-500 text-center">
                          等待管理员确认收货
                        </span>
                      )}
                      {order.status === 'completed' && (
                        <span className="text-sm text-green-500 text-center">
                          订单已完成
                        </span>
                      )}
                      {order.status === 'rejected' && (
                        <span className="text-sm text-red-500 text-center">
                          订单已拒绝
                        </span>
                      )}
                    </div>
                  </div>
                </CardBody>
              </Card>
            );
          })
        )}
      </div>

      {/* 说明信息 */}
      <Card>
        <CardBody>
          <h3 className="font-semibold text-gray-900 mb-3">订单处理流程</h3>
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <span className="px-2 py-1 bg-yellow-100 text-yellow-700 rounded">待处理</span>
            <span>→</span>
            <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded">已确认</span>
            <span>→</span>
            <span className="px-2 py-1 bg-purple-100 text-purple-700 rounded">已发货</span>
            <span>→</span>
            <span className="px-2 py-1 bg-green-100 text-green-700 rounded">已完成</span>
          </div>
          <ul className="mt-3 space-y-1 text-sm text-gray-500 list-disc list-inside">
            <li>待处理：管理员创建的采购订单，等待您确认接单</li>
            <li>已确认：您已确认接单，准备发货</li>
            <li>已发货：您已发货，等待管理员确认收货入库</li>
            <li>已完成：管理员已确认收货，订单完成</li>
          </ul>
        </CardBody>
      </Card>
    </div>
  );
}

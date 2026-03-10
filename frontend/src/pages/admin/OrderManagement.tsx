/**
 * 管理员 - 订单管理页面
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, Button, Pagination, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { getAllOrders, getOrderDetail } from '@/api/order';
import type { Order, PageBean, OrderDetailResponse } from '@/types';

const statusMap: Record<string, { label: string; color: string }> = {
  pending: { label: '待处理', color: 'text-yellow-600 bg-yellow-50' },
  shipped: { label: '已发货', color: 'text-blue-600 bg-blue-50' },
  completed: { label: '已完成', color: 'text-green-600 bg-green-50' },
  cancelled: { label: '已取消', color: 'text-gray-600 bg-gray-50' },
};

export function OrderManagement() {
  const [orders, setOrders] = useState<PageBean<Order> | null>(null);
  const [loading, setLoading] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const pageSize = 10;

  // 订单详情
  const [selectedOrder, setSelectedOrder] = useState<OrderDetailResponse | null>(null);

  useEffect(() => {
    fetchOrders();
  }, [pageNum, statusFilter]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getAllOrders({
        pageNum,
        pageSize,
        status: statusFilter || undefined,
      });
      setOrders(data);
    } catch (error) {
      console.error('获取订单列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = async (orderId: number) => {
    try {
      const detail = await getOrderDetail(orderId);
      setSelectedOrder(detail);
    } catch (error) {
      message.error('获取订单详情失败');
    }
  };

  const getStatusDisplay = (status: string) => {
    return statusMap[status] || { label: status, color: 'text-gray-600 bg-gray-50' };
  };

  const formatDate = (dateStr: string) => {
    try {
      return new Date(dateStr).toLocaleString('zh-CN');
    } catch {
      return dateStr;
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">订单管理</h1>

        {/* 状态筛选 */}
        <div className="flex items-center gap-2">
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPageNum(1);
            }}
            className="px-3 py-2 border border-gray-300 rounded-md"
          >
            <option value="">全部状态</option>
            <option value="pending">待处理</option>
            <option value="shipped">已发货</option>
            <option value="completed">已完成</option>
            <option value="cancelled">已取消</option>
          </select>
        </div>
      </div>

      <Card>
        <CardBody className="p-0">
          {loading ? (
            <Loading />
          ) : orders?.items?.length ? (
            <>
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">订单ID</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">用户ID</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">金额</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">状态</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">下单时间</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {orders.items.map((order) => {
                    const status = getStatusDisplay(order.status);
                    return (
                      <tr key={order.orderId} className="hover:bg-gray-50">
                        <td className="px-4 py-3 text-sm font-medium">{order.orderId}</td>
                        <td className="px-4 py-3 text-sm">{order.userId}</td>
                        <td className="px-4 py-3 text-sm text-red-500">
                          ¥{order.totalPrice?.toFixed(2) || '0.00'}
                        </td>
                        <td className="px-4 py-3 text-sm">
                          <span className={`px-2 py-1 rounded ${status.color}`}>
                            {status.label}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-500">
                          {formatDate(order.orderDate)}
                        </td>
                        <td className="px-4 py-3 text-sm">
                          <Button variant="ghost" size="sm" onClick={() => handleViewDetail(order.orderId)}>
                            查看详情
                          </Button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>

              <div className="p-4 border-t">
                <Pagination
                  current={pageNum}
                  total={orders.total}
                  pageSize={pageSize}
                  onChange={setPageNum}
                />
              </div>
            </>
          ) : (
            <Empty text="暂无订单" />
          )}
        </CardBody>
      </Card>

      {/* 订单详情弹窗 */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-lg">
            <CardBody>
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-bold">订单详情</h2>
                <button
                  onClick={() => setSelectedOrder(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>

              <div className="space-y-2 mb-4 text-sm">
                <p>
                  <span className="text-gray-500">用户ID：</span>
                  {selectedOrder.userId}
                </p>
                <p>
                  <span className="text-gray-500">状态：</span>
                  <span className={`ml-2 px-2 py-1 rounded ${getStatusDisplay(selectedOrder.status).color}`}>
                    {getStatusDisplay(selectedOrder.status).label}
                  </span>
                </p>
                <p>
                  <span className="text-gray-500">下单时间：</span>
                  {formatDate(selectedOrder.orderDate)}
                </p>
                <p>
                  <span className="text-gray-500">订单金额：</span>
                  <span className="text-red-500 font-bold">
                    ¥{selectedOrder.finalPrice?.toFixed(2) || '0.00'}
                  </span>
                </p>
              </div>

              <div className="border-t pt-4">
                <h3 className="font-medium mb-2">商品列表</h3>
                <div className="space-y-2 max-h-48 overflow-auto">
                  {selectedOrder.items?.map((item, index) => (
                    <div key={index} className="flex items-center justify-between text-sm bg-gray-50 p-2 rounded">
                      <span>图书ID: {item.bookId}</span>
                      <span>× {item.quantity}</span>
                      <span className="text-red-500">¥{item.price?.toFixed(2) || '0.00'}</span>
                    </div>
                  ))}
                </div>
              </div>
            </CardBody>
          </Card>
        </div>
      )}
    </div>
  );
}

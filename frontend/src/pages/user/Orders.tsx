/**
 * 我的订单页面
 */
import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Card, CardBody, Button, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { getMyOrders, getOrderDetail } from '@/api/order';
import { useAuthStore } from '@/stores';
import type { Order, PageBean, OrderDetailResponse } from '@/types';

// 订单状态映射
const statusMap: Record<string, { label: string; color: string }> = {
  pending: { label: '待处理', color: 'text-yellow-600 bg-yellow-50' },
  shipped: { label: '已发货', color: 'text-blue-600 bg-blue-50' },
  completed: { label: '已完成', color: 'text-green-600 bg-green-50' },
  cancelled: { label: '已取消', color: 'text-gray-600 bg-gray-50' },
};

export function OrdersPage() {
  const navigate = useNavigate();
  const { token } = useAuthStore();

  const [orders, setOrders] = useState<PageBean<Order> | null>(null);
  const [loading, setLoading] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const pageSize = 10;

  // 订单详情弹窗
  const [selectedOrder, setSelectedOrder] = useState<OrderDetailResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // 路由保护：未登录时跳转到登录页
  useEffect(() => {
    if (!token) {
      navigate('/login', { replace: true });
      return;
    }
    fetchOrders();
  }, [token, navigate, pageNum]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getMyOrders({ pageNum, pageSize });
      setOrders(data);
    } catch (error) {
      console.error('获取订单列表失败:', error);
      message.error('获取订单列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = async (orderId: number) => {
    setDetailLoading(true);
    try {
      const detail = await getOrderDetail(orderId);
      setSelectedOrder(detail);
    } catch (error) {
      console.error('获取订单详情失败:', error);
      message.error('获取订单详情失败');
    } finally {
      setDetailLoading(false);
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

  // 未登录或加载中
  if (!token || loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Loading text="加载中..." />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">我的订单</h1>

      {orders?.items?.length ? (
        <>
          <div className="space-y-4 mb-6">
            {orders.items.map((order) => {
              const status = getStatusDisplay(order.status);
              return (
                <Card key={order.orderId}>
                  <CardBody>
                    <div className="flex items-center justify-between mb-4">
                      <div className="text-sm text-gray-500">
                        订单号：{order.orderId}
                      </div>
                      <div className="text-sm text-gray-500">
                        {formatDate(order.orderDate)}
                      </div>
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <span className={`px-2 py-1 rounded text-sm ${status.color}`}>
                          {status.label}
                        </span>
                        <span className="text-lg font-bold text-red-500">
                          ¥{order.totalPrice?.toFixed(2) || '0.00'}
                        </span>
                      </div>

                      <Button
                        variant="ghost"
                        onClick={() => handleViewDetail(order.orderId)}
                        loading={detailLoading}
                      >
                        查看详情
                      </Button>
                    </div>
                  </CardBody>
                </Card>
              );
            })}
          </div>

          {/* 分页 */}
          {orders.total > pageSize && (
            <div className="flex justify-center">
              <div className="flex gap-2">
                <Button
                  variant="ghost"
                  disabled={pageNum <= 1}
                  onClick={() => setPageNum(p => p - 1)}
                >
                  上一页
                </Button>
                <span className="px-4 py-2 text-gray-600">
                  第 {pageNum} 页 / 共 {Math.ceil(orders.total / pageSize)} 页
                </span>
                <Button
                  variant="ghost"
                  disabled={pageNum >= Math.ceil(orders.total / pageSize)}
                  onClick={() => setPageNum(p => p + 1)}
                >
                  下一页
                </Button>
              </div>
            </div>
          )}
        </>
      ) : (
        <Empty
          text="暂无订单"
          action={
            <Link to="/books">
              <Button>去选购图书</Button>
            </Link>
          }
        />
      )}

      {/* 订单详情弹窗 */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-lg mx-4 max-h-[80vh] overflow-auto">
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

              <div className="space-y-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">订单状态</span>
                  <span className={getStatusDisplay(selectedOrder.status).color + ' px-2 py-0.5 rounded'}>
                    {getStatusDisplay(selectedOrder.status).label}
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">下单时间</span>
                  <span>{formatDate(selectedOrder.orderDate)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">订单金额</span>
                  <span className="text-red-500 font-bold">¥{selectedOrder.finalPrice?.toFixed(2)}</span>
                </div>

                <hr />

                <div>
                  <h3 className="font-medium mb-2">商品明细</h3>
                  <div className="space-y-2">
                    {selectedOrder.items.map((item, idx) => (
                      <div key={idx} className="flex justify-between text-sm">
                        <span className="text-gray-600">
                          图书ID: {item.bookId} × {item.quantity}
                        </span>
                        <span>¥{item.price?.toFixed(2)}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="mt-6 flex justify-end">
                <Button onClick={() => setSelectedOrder(null)}>关闭</Button>
              </div>
            </CardBody>
          </Card>
        </div>
      )}
    </div>
  );
}

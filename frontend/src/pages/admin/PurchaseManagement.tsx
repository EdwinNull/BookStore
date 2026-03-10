/**
 * 采购管理页面
 * 提供采购订单和缺书登记的管理功能
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Button, Input, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { addPurchase, addMissingBook, updatePurchaseStatus } from '@/api/purchase';
import type { Purchase, MissingBook } from '@/types';

// 采购订单状态映射
const statusMap: Record<string, { label: string; color: string }> = {
  pending: { label: '待处理', color: 'bg-yellow-100 text-yellow-800' },
  processing: { label: '处理中', color: 'bg-blue-100 text-blue-800' },
  completed: { label: '已完成', color: 'bg-green-100 text-green-800' },
  cancelled: { label: '已取消', color: 'bg-gray-100 text-gray-800' },
};

// 标签页类型
type TabType = 'purchases' | 'missingBooks';

export function PurchaseManagement() {
  const [activeTab, setActiveTab] = useState<TabType>('purchases');
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [missingBooks, setMissingBooks] = useState<MissingBook[]>([]);

  // 表单状态
  const [showPurchaseForm, setShowPurchaseForm] = useState(false);
  const [showMissingBookForm, setShowMissingBookForm] = useState(false);
  const [purchaseForm, setPurchaseForm] = useState<Partial<Purchase>>({
    supplierId: undefined,
    bookId: undefined,
    quantity: 1,
    price: 0,
  });
  const [missingBookForm, setMissingBookForm] = useState<Partial<MissingBook>>({
    bookId: undefined,
    bookName: '',
    supplierId: undefined,
    quantity: 1,
    publisher: '',
  });

  // 加载数据
  useEffect(() => {
    // 模拟数据 - 实际项目中应从后端获取
    setPurchases([
      {
        purchaseOrderId: 1,
        supplierId: 1,
        bookId: 1,
        quantity: 100,
        price: 50.00,
        orderDate: '2024-01-15T10:00:00',
        status: 'completed',
        updateDate: '2024-01-15T14:00:00',
      },
      {
        purchaseOrderId: 2,
        supplierId: 2,
        bookId: 3,
        quantity: 50,
        price: 45.00,
        orderDate: '2024-01-16T09:00:00',
        status: 'pending',
        updateDate: '2024-01-16T09:00:00',
      },
    ]);

    setMissingBooks([
      {
        missingBooksId: 1,
        bookId: 5,
        bookName: '深入理解计算机系统',
        supplierId: 1,
        quantity: 20,
        registrationDate: '2024-01-14T15:00:00',
        userId: 1,
        publisher: '机械工业出版社',
        requestedQuantity: 20,
      },
    ]);
  }, []);

  // 格式化日期
  const formatDate = (dateStr: string) => {
    try {
      return new Date(dateStr).toLocaleString('zh-CN');
    } catch {
      return dateStr;
    }
  };

  // 获取状态显示
  const getStatusDisplay = (status: string) => {
    return statusMap[status] || { label: status, color: 'bg-gray-100 text-gray-800' };
  };

  // 添加采购订单
  const handleAddPurchase = async () => {
    if (!purchaseForm.supplierId || !purchaseForm.bookId || !purchaseForm.quantity) {
      message.error('请填写完整信息');
      return;
    }

    try {
      await addPurchase(purchaseForm as Purchase);
      message.success('采购订单添加成功');
      setShowPurchaseForm(false);
      setPurchaseForm({
        supplierId: undefined,
        bookId: undefined,
        quantity: 1,
        price: 0,
      });
    } catch (error) {
      message.error(error instanceof Error ? error.message : '添加失败');
    }
  };

  // 添加缺书登记
  const handleAddMissingBook = async () => {
    if (!missingBookForm.bookName || !missingBookForm.quantity) {
      message.error('请填写完整信息');
      return;
    }

    try {
      await addMissingBook(missingBookForm as MissingBook);
      message.success('缺书登记添加成功');
      setShowMissingBookForm(false);
      setMissingBookForm({
        bookId: undefined,
        bookName: '',
        supplierId: undefined,
        quantity: 1,
        publisher: '',
      });
    } catch (error) {
      message.error(error instanceof Error ? error.message : '添加失败');
    }
  };

  // 更新采购订单状态
  const handleUpdateStatus = async (purchaseOrderId: number, status: string) => {
    try {
      await updatePurchaseStatus(purchaseOrderId, status);
      message.success('状态更新成功');
      // 更新本地状态
      setPurchases(purchases.map(p =>
        p.purchaseOrderId === purchaseOrderId ? { ...p, status } : p
      ));
    } catch (error) {
      message.error(error instanceof Error ? error.message : '更新失败');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">采购管理</h1>
      </div>

      {/* 标签页 */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setActiveTab('purchases')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'purchases'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            采购订单
          </button>
          <button
            onClick={() => setActiveTab('missingBooks')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'missingBooks'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            缺书登记
          </button>
        </nav>
      </div>

      {/* 采购订单列表 */}
      {activeTab === 'purchases' && (
        <Card>
          <CardHeader className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">采购订单列表</h2>
            <Button onClick={() => setShowPurchaseForm(true)}>
              新增采购订单
            </Button>
          </CardHeader>
          <CardBody>
            {purchases.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        订单ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        供应商ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        图书ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        数量
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        单价
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        状态
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        下单时间
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        操作
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {purchases.map((purchase) => {
                      const status = getStatusDisplay(purchase.status);
                      return (
                        <tr key={purchase.purchaseOrderId} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            #{purchase.purchaseOrderId}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {purchase.supplierId}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {purchase.bookId}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {purchase.quantity}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            ¥{purchase.price.toFixed(2)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span className={`px-2 py-1 text-xs font-medium rounded-full ${status.color}`}>
                              {status.label}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(purchase.orderDate)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            {purchase.status === 'pending' && (
                              <>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  onClick={() => handleUpdateStatus(purchase.purchaseOrderId, 'processing')}
                                >
                                  处理
                                </Button>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  onClick={() => handleUpdateStatus(purchase.purchaseOrderId, 'completed')}
                                >
                                  完成
                                </Button>
                              </>
                            )}
                            {purchase.status === 'processing' && (
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => handleUpdateStatus(purchase.purchaseOrderId, 'completed')}
                              >
                                完成
                              </Button>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            ) : (
              <Empty text="暂无采购订单" />
            )}
          </CardBody>
        </Card>
      )}

      {/* 缺书登记列表 */}
      {activeTab === 'missingBooks' && (
        <Card>
          <CardHeader className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">缺书登记列表</h2>
            <Button onClick={() => setShowMissingBookForm(true)}>
              新增缺书登记
            </Button>
          </CardHeader>
          <CardBody>
            {missingBooks.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        登记ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        图书名称
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        出版社
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        缺货数量
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        登记时间
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        操作
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {missingBooks.map((item) => (
                      <tr key={item.missingBooksId} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          #{item.missingBooksId}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {item.bookName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {item.publisher}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {item.quantity}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {formatDate(item.registrationDate)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <Button
                            variant="primary"
                            size="sm"
                            onClick={() => {
                              // 根据缺书登记创建采购订单
                              setPurchaseForm({
                                bookId: item.bookId,
                                supplierId: item.supplierId,
                                quantity: item.quantity,
                                price: 0,
                              });
                              setActiveTab('purchases');
                              setShowPurchaseForm(true);
                            }}
                          >
                            创建采购
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <Empty text="暂无缺书登记" />
            )}
          </CardBody>
        </Card>
      )}

      {/* 新增采购订单弹窗 */}
      {showPurchaseForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md mx-4">
            <CardHeader>
              <h3 className="text-lg font-semibold">新增采购订单</h3>
            </CardHeader>
            <CardBody>
              <div className="space-y-4">
                <Input
                  label="供应商ID"
                  type="number"
                  placeholder="请输入供应商ID"
                  value={purchaseForm.supplierId || ''}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, supplierId: Number(e.target.value) })}
                />
                <Input
                  label="图书ID"
                  type="number"
                  placeholder="请输入图书ID"
                  value={purchaseForm.bookId || ''}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, bookId: Number(e.target.value) })}
                />
                <Input
                  label="采购数量"
                  type="number"
                  placeholder="请输入采购数量"
                  value={purchaseForm.quantity || ''}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, quantity: Number(e.target.value) })}
                />
                <Input
                  label="单价"
                  type="number"
                  step="0.01"
                  placeholder="请输入单价"
                  value={purchaseForm.price || ''}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, price: Number(e.target.value) })}
                />
              </div>
              <div className="mt-6 flex justify-end gap-3">
                <Button variant="ghost" onClick={() => setShowPurchaseForm(false)}>
                  取消
                </Button>
                <Button onClick={handleAddPurchase}>
                  确认添加
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      )}

      {/* 新增缺书登记弹窗 */}
      {showMissingBookForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md mx-4">
            <CardHeader>
              <h3 className="text-lg font-semibold">新增缺书登记</h3>
            </CardHeader>
            <CardBody>
              <div className="space-y-4">
                <Input
                  label="图书ID"
                  type="number"
                  placeholder="请输入图书ID（可选）"
                  value={missingBookForm.bookId || ''}
                  onChange={(e) => setMissingBookForm({ ...missingBookForm, bookId: Number(e.target.value) })}
                />
                <Input
                  label="图书名称"
                  placeholder="请输入图书名称"
                  value={missingBookForm.bookName || ''}
                  onChange={(e) => setMissingBookForm({ ...missingBookForm, bookName: e.target.value })}
                />
                <Input
                  label="出版社"
                  placeholder="请输入出版社"
                  value={missingBookForm.publisher || ''}
                  onChange={(e) => setMissingBookForm({ ...missingBookForm, publisher: e.target.value })}
                />
                <Input
                  label="供应商ID"
                  type="number"
                  placeholder="请输入供应商ID（可选）"
                  value={missingBookForm.supplierId || ''}
                  onChange={(e) => setMissingBookForm({ ...missingBookForm, supplierId: Number(e.target.value) })}
                />
                <Input
                  label="缺货数量"
                  type="number"
                  placeholder="请输入缺货数量"
                  value={missingBookForm.quantity || ''}
                  onChange={(e) => setMissingBookForm({ ...missingBookForm, quantity: Number(e.target.value) })}
                />
              </div>
              <div className="mt-6 flex justify-end gap-3">
                <Button variant="ghost" onClick={() => setShowMissingBookForm(false)}>
                  取消
                </Button>
                <Button onClick={handleAddMissingBook}>
                  确认添加
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      )}
    </div>
  );
}

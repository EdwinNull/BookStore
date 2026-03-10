/**
 * 供应商管理页面
 * 提供供应商的增删改查功能
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Button, Input, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { getSuppliers, addSupplier, updateSupplier, deleteSupplier, updateSupplierStatus } from '@/api/supplier';
import type { Supplier } from '@/types';

// 状态标签配置
const statusConfig: Record<string, { label: string; color: string }> = {
  active: { label: '已激活', color: 'text-green-600 bg-green-100' },
  inactive: { label: '已停用', color: 'text-red-600 bg-red-100' },
  pending: { label: '待审核', color: 'text-yellow-600 bg-yellow-100' },
};

export function SupplierManagement() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState<Supplier | null>(null);
  const [formData, setFormData] = useState<Partial<Supplier>>({
    name: '',
    address: '',
    contactInfo: '',
    contactPerson: '',
    phone: '',
    email: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  // 加载供应商列表
  const loadSuppliers = async () => {
    setLoading(true);
    try {
      const data = await getSuppliers();
      setSuppliers(data || []);
    } catch (error) {
      console.error('[Load Suppliers Error]', error);
      message.error('加载供应商列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSuppliers();
  }, []);

  // 表单验证
  const validate = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.name?.trim()) {
      newErrors.name = '请输入供应商名称';
    }

    if (!formData.address?.trim()) {
      newErrors.address = '请输入供应商地址';
    }

    if (!formData.contactInfo?.trim() && !formData.phone?.trim()) {
      newErrors.contactInfo = '请输入联系方式或电话';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 打开添加表单
  const handleAdd = () => {
    setEditingSupplier(null);
    setFormData({
      name: '',
      address: '',
      contactInfo: '',
      contactPerson: '',
      phone: '',
      email: '',
    });
    setErrors({});
    setShowForm(true);
  };

  // 打开编辑表单
  const handleEdit = (supplier: Supplier) => {
    setEditingSupplier(supplier);
    setFormData({
      name: supplier.name,
      address: supplier.address,
      contactInfo: supplier.contactInfo,
      contactPerson: supplier.contactPerson,
      phone: supplier.phone,
      email: supplier.email,
    });
    setErrors({});
    setShowForm(true);
  };

  // 提交表单
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setLoading(true);
    try {
      if (editingSupplier) {
        await updateSupplier({ ...editingSupplier, ...formData });
        message.success('更新成功');
      } else {
        await addSupplier(formData);
        message.success('添加成功');
      }
      setShowForm(false);
      loadSuppliers();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '操作失败');
    } finally {
      setLoading(false);
    }
  };

  // 删除供应商
  const handleDelete = async (id: number) => {
    if (!confirm('确定要删除这个供应商吗？')) return;

    setLoading(true);
    try {
      await deleteSupplier(id);
      message.success('删除成功');
      loadSuppliers();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '删除失败');
    } finally {
      setLoading(false);
    }
  };

  // 更新供应商状态
  const handleStatusChange = async (id: number, status: 'active' | 'inactive') => {
    setLoading(true);
    try {
      await updateSupplierStatus(id, status);
      message.success('状态更新成功');
      loadSuppliers();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '操作失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* 页面标题和操作栏 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">供应商管理</h1>
          <p className="text-gray-500 text-sm mt-1">管理供应商信息和审核</p>
        </div>
        <Button onClick={handleAdd}>
          <span className="mr-2">+</span>
          添加供应商
        </Button>
      </div>

      {/* 添加/编辑表单 */}
      {showForm && (
        <Card>
          <CardHeader>
            <h2 className="text-lg font-semibold">
              {editingSupplier ? '编辑供应商' : '添加供应商'}
            </h2>
          </CardHeader>
          <CardBody>
            <form onSubmit={handleSubmit} className="space-y-4 max-w-lg">
              <Input
                label="供应商名称"
                placeholder="请输入供应商名称"
                value={formData.name || ''}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                error={errors.name}
                required
              />

              <Input
                label="地址"
                placeholder="请输入供应商地址"
                value={formData.address || ''}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                error={errors.address}
                required
              />

              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="联系人"
                  placeholder="联系人姓名"
                  value={formData.contactPerson || ''}
                  onChange={(e) => setFormData({ ...formData, contactPerson: e.target.value })}
                />

                <Input
                  label="联系电话"
                  placeholder="联系电话"
                  value={formData.phone || ''}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                />
              </div>

              <Input
                label="邮箱"
                type="email"
                placeholder="邮箱地址"
                value={formData.email || ''}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />

              <Input
                label="其他联系方式"
                placeholder="QQ、微信等"
                value={formData.contactInfo || ''}
                onChange={(e) => setFormData({ ...formData, contactInfo: e.target.value })}
                error={errors.contactInfo}
              />

              <div className="flex gap-3">
                <Button type="submit" loading={loading}>
                  {editingSupplier ? '更新' : '添加'}
                </Button>
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => setShowForm(false)}
                >
                  取消
                </Button>
              </div>
            </form>
          </CardBody>
        </Card>
      )}

      {/* 供应商列表 */}
      <Card>
        <CardBody className="p-0">
          {loading ? (
            <Loading text="加载中..." />
          ) : suppliers.length === 0 ? (
            <Empty text="暂无供应商数据" />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      ID
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      供应商名称
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      联系人
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      联系方式
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      状态
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      操作
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {suppliers.map((supplier) => {
                    const status = statusConfig[supplier.status || 'active'];
                    return (
                      <tr key={supplier.supplierId} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {supplier.supplierId}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm font-medium text-gray-900">{supplier.name}</div>
                          {supplier.username && (
                            <div className="text-xs text-gray-400">账号: {supplier.username}</div>
                          )}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {supplier.contactPerson || '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm text-gray-500">{supplier.phone || '-'}</div>
                          <div className="text-xs text-gray-400">{supplier.email || ''}</div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`px-2 py-1 text-xs rounded-full ${status.color}`}>
                            {status.label}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleEdit(supplier)}
                          >
                            编辑
                          </Button>
                          {supplier.status === 'pending' && (
                            <>
                              <Button
                                variant="ghost"
                                size="sm"
                                className="text-green-500"
                                onClick={() => handleStatusChange(supplier.supplierId, 'active')}
                              >
                                激活
                              </Button>
                              <Button
                                variant="ghost"
                                size="sm"
                                className="text-red-500"
                                onClick={() => handleStatusChange(supplier.supplierId, 'inactive')}
                              >
                                拒绝
                              </Button>
                            </>
                          )}
                          {supplier.status === 'active' && (
                            <Button
                              variant="ghost"
                              size="sm"
                              className="text-yellow-500"
                              onClick={() => handleStatusChange(supplier.supplierId, 'inactive')}
                            >
                              停用
                            </Button>
                          )}
                          {supplier.status === 'inactive' && (
                            <Button
                              variant="ghost"
                              size="sm"
                              className="text-green-500"
                              onClick={() => handleStatusChange(supplier.supplierId, 'active')}
                            >
                              激活
                            </Button>
                          )}
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-red-500 hover:text-red-600"
                            onClick={() => handleDelete(supplier.supplierId)}
                          >
                            删除
                          </Button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
}

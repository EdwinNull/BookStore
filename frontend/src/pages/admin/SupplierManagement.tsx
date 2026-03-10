/**
 * 供应商管理页面
 * 提供供应商的增删改查功能
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Button, Input, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { addSupplier, updateSupplier, deleteSupplier } from '@/api/supplier';
import type { Supplier } from '@/types';

export function SupplierManagement() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState<Supplier | null>(null);
  const [formData, setFormData] = useState<Partial<Supplier>>({
    name: '',
    address: '',
    contactInfo: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  // 模拟数据 - 实际项目中应该从后端获取
  useEffect(() => {
    // 由于后端没有获取所有供应商的接口，这里使用模拟数据
    // 实际使用时需要后端添加相应接口
    setSuppliers([
      { supplierId: 1, name: '清华大学出版社', address: '北京市海淀区', contactInfo: '010-62770175' },
      { supplierId: 2, name: '机械工业出版社', address: '北京市西城区', contactInfo: '010-68326677' },
      { supplierId: 3, name: '人民邮电出版社', address: '北京市丰台区', contactInfo: '010-81055512' },
    ]);
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

    if (!formData.contactInfo?.trim()) {
      newErrors.contactInfo = '请输入联系方式';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 打开添加表单
  const handleAdd = () => {
    setEditingSupplier(null);
    setFormData({ name: '', address: '', contactInfo: '' });
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
        // 更新
        await updateSupplier({ ...editingSupplier, ...formData });
        setSuppliers(suppliers.map(s =>
          s.supplierId === editingSupplier.supplierId
            ? { ...s, ...formData } as Supplier
            : s
        ));
        message.success('更新成功');
      } else {
        // 添加
        await addSupplier(formData);
        // 由于后端没有返回ID，这里模拟添加
        const newSupplier: Supplier = {
          supplierId: Date.now(),
          name: formData.name || '',
          address: formData.address || '',
          contactInfo: formData.contactInfo || '',
        };
        setSuppliers([...suppliers, newSupplier]);
        message.success('添加成功');
      }
      setShowForm(false);
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
      setSuppliers(suppliers.filter(s => s.supplierId !== id));
      message.success('删除成功');
    } catch (error) {
      message.error(error instanceof Error ? error.message : '删除失败');
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
          <p className="text-gray-500 text-sm mt-1">管理图书供应商信息</p>
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
              />

              <Input
                label="地址"
                placeholder="请输入供应商地址"
                value={formData.address || ''}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                error={errors.address}
              />

              <Input
                label="联系方式"
                placeholder="请输入联系方式"
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
                      地址
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      联系方式
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      操作
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {suppliers.map((supplier) => (
                    <tr key={supplier.supplierId} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {supplier.supplierId}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {supplier.name}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {supplier.address}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {supplier.contactInfo}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEdit(supplier)}
                        >
                          编辑
                        </Button>
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
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
}

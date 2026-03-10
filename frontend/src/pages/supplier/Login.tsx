/**
 * 供应商登录页面
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { supplierLogin, getSupplierProfile } from '@/api/supplierAuth';

export function SupplierLoginPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  // 表单验证
  const validate = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.username) {
      newErrors.username = '请输入用户名';
    } else if (formData.username.length < 5 || formData.username.length > 20) {
      newErrors.username = '用户名长度应为5-20位';
    }

    if (!formData.password) {
      newErrors.password = '请输入密码';
    } else if (formData.password.length < 5 || formData.password.length > 16) {
      newErrors.password = '密码长度应为5-16位';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 提交登录
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setLoading(true);
    try {
      // 1. 登录获取 token
      const token = await supplierLogin(formData);
      console.log('[SupplierLogin] Token received:', token);

      // 2. 保存 token 到 localStorage
      localStorage.setItem('supplier_token', token);

      // 3. 获取供应商信息
      const supplier = await getSupplierProfile();
      console.log('[SupplierLogin] Supplier info received:', supplier);

      // 4. 保存供应商信息到 localStorage
      localStorage.setItem('supplier_info', JSON.stringify(supplier));

      message.success('登录成功');
      navigate('/supplier/dashboard');
    } catch (error) {
      console.error('[SupplierLogin Error]', error);
      message.error(error instanceof Error ? error.message : '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-200px)] flex items-center justify-center py-12 px-4 bg-gray-50">
      <Card className="w-full max-w-md">
        <CardBody>
          <h1 className="text-2xl font-bold text-center text-gray-900 mb-2">
            供应商登录
          </h1>
          <p className="text-center text-gray-500 text-sm mb-6">
            欢迎使用供应商管理平台
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="用户名"
              placeholder="请输入供应商账号"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              error={errors.username}
            />

            <Input
              label="密码"
              type="password"
              placeholder="请输入密码"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              error={errors.password}
            />

            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              登录
            </Button>
          </form>

          <div className="mt-4 text-center text-sm text-gray-600">
            还没有账号？{' '}
            <Link to="/supplier/register" className="text-blue-500 hover:underline">
              申请入驻
            </Link>
          </div>

          <div className="mt-4 pt-4 border-t border-gray-200 text-center">
            <Link to="/" className="text-sm text-gray-500 hover:text-blue-500">
              返回首页
            </Link>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

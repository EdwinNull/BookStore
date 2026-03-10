/**
 * 供应商注册页面
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { supplierRegister } from '@/api/supplierAuth';
import type { SupplierRegisterParams } from '@/types';

export function SupplierRegisterPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState<SupplierRegisterParams>({
    username: '',
    password: '',
    name: '',
    address: '',
    contactPerson: '',
    phone: '',
    email: '',
    contactInfo: '',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
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

    if (!confirmPassword) {
      newErrors.confirmPassword = '请确认密码';
    } else if (formData.password !== confirmPassword) {
      newErrors.confirmPassword = '两次密码不一致';
    }

    if (!formData.name) {
      newErrors.name = '请输入供应商名称';
    }

    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = '请输入有效的邮箱地址';
    }

    if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
      newErrors.phone = '请输入有效的手机号码';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 提交注册
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setLoading(true);
    try {
      await supplierRegister(formData);
      message.success('注册申请已提交，请等待管理员审核');
      navigate('/supplier/login');
    } catch (error) {
      console.error('[SupplierRegister Error]', error);
      message.error(error instanceof Error ? error.message : '注册失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-200px)] flex items-center justify-center py-12 px-4 bg-gray-50">
      <Card className="w-full max-w-lg">
        <CardBody>
          <h1 className="text-2xl font-bold text-center text-gray-900 mb-2">
            供应商入驻申请
          </h1>
          <p className="text-center text-gray-500 text-sm mb-6">
            填写以下信息申请成为供应商，审核通过后即可登录
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="登录账号"
                placeholder="5-20位字符"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                error={errors.username}
                required
              />

              <Input
                label="供应商名称"
                placeholder="公司/店铺名称"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                error={errors.name}
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="密码"
                type="password"
                placeholder="5-16位字符"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                error={errors.password}
                required
              />

              <Input
                label="确认密码"
                type="password"
                placeholder="再次输入密码"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                error={errors.confirmPassword}
                required
              />
            </div>

            <Input
              label="地址"
              placeholder="公司/店铺地址"
              value={formData.address || ''}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
            />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="联系人"
                placeholder="联系人姓名"
                value={formData.contactPerson || ''}
                onChange={(e) => setFormData({ ...formData, contactPerson: e.target.value })}
              />

              <Input
                label="联系电话"
                placeholder="手机号码"
                value={formData.phone || ''}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                error={errors.phone}
              />
            </div>

            <Input
              label="邮箱"
              type="email"
              placeholder="邮箱地址"
              value={formData.email || ''}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              error={errors.email}
            />

            <Input
              label="其他联系方式"
              placeholder="QQ、微信等（选填）"
              value={formData.contactInfo || ''}
              onChange={(e) => setFormData({ ...formData, contactInfo: e.target.value })}
            />

            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              提交申请
            </Button>
          </form>

          <div className="mt-4 text-center text-sm text-gray-600">
            已有账号？{' '}
            <Link to="/supplier/login" className="text-blue-500 hover:underline">
              立即登录
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

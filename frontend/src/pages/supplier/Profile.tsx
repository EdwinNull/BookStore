/**
 * 供应商账号信息页面
 */
import { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { updateSupplierProfile, updateSupplierPassword } from '@/api/supplierAuth';
import type { SupplierProfileResponse } from '@/types';

export function SupplierProfile() {
  const { supplier } = useOutletContext<{ supplier: SupplierProfileResponse }>();

  const [formData, setFormData] = useState({
    name: supplier.name,
    address: supplier.address || '',
    contactPerson: supplier.contactPerson || '',
    phone: supplier.phone || '',
    email: supplier.email || '',
    contactInfo: supplier.contactInfo || '',
  });

  const [passwordData, setPasswordData] = useState({
    oldPwd: '',
    newPwd: '',
    confirmPwd: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);
  const [pwdLoading, setPwdLoading] = useState(false);

  // 验证基本信息表单
  const validate = () => {
    const newErrors: Record<string, string> = {};

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

  // 更新基本信息
  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setLoading(true);
    try {
      await updateSupplierProfile(formData);
      message.success('信息更新成功');
      // 刷新页面获取最新数据
      window.location.reload();
    } catch (error) {
      console.error('[UpdateProfile Error]', error);
      message.error(error instanceof Error ? error.message : '更新失败');
    } finally {
      setLoading(false);
    }
  };

  // 验证密码表单
  const validatePassword = () => {
    const newErrors: Record<string, string> = {};

    if (!passwordData.oldPwd) {
      newErrors.oldPwd = '请输入原密码';
    }

    if (!passwordData.newPwd) {
      newErrors.newPwd = '请输入新密码';
    } else if (passwordData.newPwd.length < 5 || passwordData.newPwd.length > 16) {
      newErrors.newPwd = '密码长度应为5-16位';
    }

    if (!passwordData.confirmPwd) {
      newErrors.confirmPwd = '请确认新密码';
    } else if (passwordData.newPwd !== passwordData.confirmPwd) {
      newErrors.confirmPwd = '两次密码不一致';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 修改密码
  const handleUpdatePassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validatePassword()) return;

    setPwdLoading(true);
    try {
      await updateSupplierPassword(
        passwordData.oldPwd,
        passwordData.newPwd,
        passwordData.confirmPwd
      );
      message.success('密码修改成功，请重新登录');
      // 清除登录状态，跳转登录页
      localStorage.removeItem('supplier_token');
      localStorage.removeItem('supplier_info');
      window.location.href = '/supplier/login';
    } catch (error) {
      console.error('[UpdatePassword Error]', error);
      message.error(error instanceof Error ? error.message : '密码修改失败');
    } finally {
      setPwdLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-900">账号信息</h1>

      {/* 基本信息 */}
      <Card>
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">基本信息</h2>
          <form onSubmit={handleUpdateProfile} className="space-y-4">
            <Input
              label="登录账号"
              value={supplier.username}
              disabled
              className="bg-gray-50"
            />
            <p className="text-xs text-gray-500 -mt-2">登录账号不可修改</p>

            <Input
              label="供应商名称"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              error={errors.name}
              required
            />

            <Input
              label="地址"
              value={formData.address}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
            />

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="联系人"
                value={formData.contactPerson}
                onChange={(e) => setFormData({ ...formData, contactPerson: e.target.value })}
              />

              <Input
                label="联系电话"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                error={errors.phone}
              />
            </div>

            <Input
              label="邮箱"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              error={errors.email}
            />

            <Input
              label="其他联系方式"
              value={formData.contactInfo}
              onChange={(e) => setFormData({ ...formData, contactInfo: e.target.value })}
              placeholder="QQ、微信等"
            />

            <Button type="submit" loading={loading}>
              保存修改
            </Button>
          </form>
        </CardBody>
      </Card>

      {/* 修改密码 */}
      <Card>
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">修改密码</h2>
          <form onSubmit={handleUpdatePassword} className="space-y-4">
            <Input
              label="原密码"
              type="password"
              value={passwordData.oldPwd}
              onChange={(e) => setPasswordData({ ...passwordData, oldPwd: e.target.value })}
              error={errors.oldPwd}
            />

            <Input
              label="新密码"
              type="password"
              value={passwordData.newPwd}
              onChange={(e) => setPasswordData({ ...passwordData, newPwd: e.target.value })}
              error={errors.newPwd}
              placeholder="5-16位字符"
            />

            <Input
              label="确认新密码"
              type="password"
              value={passwordData.confirmPwd}
              onChange={(e) => setPasswordData({ ...passwordData, confirmPwd: e.target.value })}
              error={errors.confirmPwd}
            />

            <Button type="submit" loading={pwdLoading}>
              修改密码
            </Button>
          </form>
        </CardBody>
      </Card>

      {/* 账号状态 */}
      <Card>
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">账号状态</h2>
          <div className="flex items-center gap-4">
            <span className={`
              inline-block px-3 py-1 rounded-full text-sm
              ${supplier.status === 'active'
                ? 'bg-green-100 text-green-700'
                : supplier.status === 'pending'
                  ? 'bg-yellow-100 text-yellow-700'
                  : 'bg-red-100 text-red-700'
              }
            `}>
              {supplier.status === 'active'
                ? '正常'
                : supplier.status === 'pending'
                  ? '待审核'
                  : '已停用'}
            </span>
            <span className="text-sm text-gray-500">
              {supplier.status === 'pending' && '您的账号正在审核中，审核通过后可正常使用'}
              {supplier.status === 'inactive' && '您的账号已被停用，如有疑问请联系管理员'}
            </span>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

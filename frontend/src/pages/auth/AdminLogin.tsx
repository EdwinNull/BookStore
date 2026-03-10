/**
 * 管理员登录页面
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { adminLogin } from '@/api/admin';
import { getUserInfo } from '@/api/user';
import { useAuthStore } from '@/stores';

export function AdminLoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();

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
      newErrors.username = '请输入管理员用户名';
    }

    if (!formData.password) {
      newErrors.password = '请输入密码';
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
      // 1. 管理员登录获取 token
      const token = await adminLogin(formData);
      console.log('[Admin Login] Token received:', token);

      // 2. 将 token 保存到 localStorage（这样后续请求才能携带 Authorization 头）
      localStorage.setItem('token', token);

      // 3. 获取用户信息（此时请求会携带 token）
      const user = await getUserInfo();
      console.log('[Admin Login] User info received:', user);

      // 4. 检查是否为管理员
      if (user.role !== 'admin') {
        message.error('您不是管理员，无法登录后台');
        localStorage.removeItem('token');
        return;
      }

      // 5. 保存认证状态
      setAuth(token, user);
      message.success('登录成功');

      // 6. 跳转到管理员后台首页
      navigate('/admin');
    } catch (error) {
      console.error('[Admin Login Error]', error);
      message.error(error instanceof Error ? error.message : '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <Card className="w-full max-w-md">
        <CardBody>
          <h1 className="text-2xl font-bold text-center text-gray-900 mb-2">
            管理员登录
          </h1>
          <p className="text-center text-gray-500 text-sm mb-6">
            后台管理系统
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="管理员用户名"
              placeholder="请输入管理员用户名"
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

          <div className="mt-4 text-center">
            <Link to="/login" className="text-sm text-gray-500 hover:text-blue-500">
              返回用户登录
            </Link>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

/**
 * 登录页面
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { login, getUserInfo } from '@/api/user';
import { useAuthStore } from '@/stores';

export function LoginPage() {
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
      newErrors.username = '请输入用户名';
    } else if (formData.username.length < 5 || formData.username.length > 16) {
      newErrors.username = '用户名长度应为5-16位';
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
      // 1. 先登录获取 token
      const token = await login(formData);
      console.log('[Login] Token received:', token);

      // 2. 将 token 保存到 localStorage（这样后续请求才能携带 Authorization 头）
      localStorage.setItem('token', token);

      // 3. 获取用户信息（此时请求会携带 token）
      const user = await getUserInfo();
      console.log('[Login] User info received:', user);

      // 4. 保存到状态管理
      setAuth(token, user);

      message.success('登录成功');
      navigate('/');
    } catch (error) {
      console.error('[Login Error]', error);
      message.error(error instanceof Error ? error.message : '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-200px)] flex items-center justify-center py-12 px-4">
      <Card className="w-full max-w-md">
        <CardBody>
          <h1 className="text-2xl font-bold text-center text-gray-900 mb-6">
            用户登录
          </h1>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="用户名"
              placeholder="请输入用户名"
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
            <Link to="/register" className="text-blue-500 hover:underline">
              立即注册
            </Link>
          </div>

          <div className="mt-4 pt-4 border-t border-gray-200 text-center">
            <Link to="/admin/login" className="text-sm text-gray-500 hover:text-blue-500">
              管理员登录
            </Link>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

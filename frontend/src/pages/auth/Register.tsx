/**
 * 注册页面
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { register } from '@/api/user';

export function RegisterPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
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

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = '请确认密码';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = '两次输入的密码不一致';
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
      await register({
        username: formData.username,
        password: formData.password,
      });
      message.success('注册成功，请登录');
      navigate('/login');
    } catch (error) {
      message.error(error instanceof Error ? error.message : '注册失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-200px)] flex items-center justify-center py-12 px-4">
      <Card className="w-full max-w-md">
        <CardBody>
          <h1 className="text-2xl font-bold text-center text-gray-900 mb-6">
            用户注册
          </h1>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="用户名"
              placeholder="请输入用户名（5-16位）"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              error={errors.username}
            />

            <Input
              label="密码"
              type="password"
              placeholder="请输入密码（5-16位）"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              error={errors.password}
            />

            <Input
              label="确认密码"
              type="password"
              placeholder="请再次输入密码"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              error={errors.confirmPassword}
            />

            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              注册
            </Button>
          </form>

          <div className="mt-4 text-center text-sm text-gray-600">
            已有账号？{' '}
            <Link to="/login" className="text-blue-500 hover:underline">
              立即登录
            </Link>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

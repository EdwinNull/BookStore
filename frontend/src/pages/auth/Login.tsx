/**
 * 统一登录页面
 * 支持用户、管理员、供应商三种角色登录
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Input, Card, CardBody } from '@/components/common';
import { message } from '@/components/common/Message';
import { login, getUserInfo } from '@/api/user';
import { supplierLogin, getSupplierProfile } from '@/api/supplierAuth';
import { useAuthStore } from '@/stores';

// 角色类型定义
type RoleType = 'user' | 'admin' | 'supplier';

// 角色配置
const roleConfig = {
  user: {
    title: '用户登录',
    color: 'blue',
    bgColor: 'bg-blue-500',
    hoverBg: 'hover:bg-blue-600',
    borderColor: 'border-blue-500',
    textColor: 'text-blue-500',
    registerPath: '/register',
    loginApi: login,
    afterLogin: '/',
    tokenKey: 'token',
    userKey: 'user',
  },
  admin: {
    title: '管理员登录',
    color: 'purple',
    bgColor: 'bg-purple-500',
    hoverBg: 'hover:bg-purple-600',
    borderColor: 'border-purple-500',
    textColor: 'text-purple-500',
    registerPath: null,
    loginApi: login,
    afterLogin: '/admin',
    tokenKey: 'token',
    userKey: 'user',
  },
  supplier: {
    title: '供应商登录',
    color: 'green',
    bgColor: 'bg-green-500',
    hoverBg: 'hover:bg-green-600',
    borderColor: 'border-green-500',
    textColor: 'text-green-500',
    registerPath: '/supplier/register',
    afterLogin: '/supplier/dashboard',
    tokenKey: 'supplier_token',
    userKey: 'supplier_info',
  },
};

export function LoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();

  // 当前选中的角色
  const [selectedRole, setSelectedRole] = useState<RoleType>('user');
  const config = roleConfig[selectedRole];

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
      if (selectedRole === 'supplier') {
        // 供应商登录流程
        const token = await supplierLogin(formData);
        console.log('[SupplierLogin] Token received:', token);

        localStorage.setItem('supplier_token', token);

        const supplier = await getSupplierProfile();
        console.log('[SupplierLogin] Supplier info received:', supplier);

        localStorage.setItem('supplier_info', JSON.stringify(supplier));

        message.success('登录成功');
        navigate('/supplier/dashboard');
      } else {
        // 用户/管理员登录流程
        const token = await login(formData);
        console.log('[Login] Token received:', token);

        localStorage.setItem('token', token);

        const user = await getUserInfo();
        console.log('[Login] User info received:', user);

        setAuth(token, user);

        message.success('登录成功');

        // 根据角色跳转不同页面
        if (selectedRole === 'admin' || user.role === 'admin') {
          navigate('/admin');
        } else {
          navigate('/');
        }
      }
    } catch (error) {
      console.error('[Login Error]', error);
      message.error(error instanceof Error ? error.message : '登录失败');
    } finally {
      setLoading(false);
    }
  };

  // 角色切换时清空表单
  const handleRoleChange = (role: RoleType) => {
    setSelectedRole(role);
    setFormData({ username: '', password: '' });
    setErrors({});
  };

  return (
    <div className="min-h-[calc(100vh-200px)] flex items-center justify-center py-12 px-4 bg-gray-50">
      <Card className="w-full max-w-md">
        <CardBody>
          {/* 角色选择器 */}
          <div className="flex mb-6 bg-gray-100 rounded-lg p-1">
            {(['user', 'admin', 'supplier'] as RoleType[]).map((role) => {
              const cfg = roleConfig[role];
              const isSelected = selectedRole === role;

              return (
                <button
                  key={role}
                  onClick={() => handleRoleChange(role)}
                  className={`
                    flex-1 py-2 px-4 rounded-md text-sm font-medium
                    transition-all duration-200
                    ${isSelected
                      ? `${cfg.bgColor} text-white shadow-sm`
                      : 'text-gray-600 hover:text-gray-900'
                    }
                  `}
                >
                  {role === 'user' && '用户'}
                  {role === 'admin' && '管理员'}
                  {role === 'supplier' && '供应商'}
                </button>
              );
            })}
          </div>

          {/* 标题 */}
          <h1 className={`text-2xl font-bold text-center text-gray-900 mb-2`}>
            {config.title}
          </h1>
          {selectedRole === 'supplier' && (
            <p className="text-center text-gray-500 text-sm mb-6">
              欢迎使用供应商管理平台
            </p>
          )}

          {/* 登录表单 */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="用户名"
              placeholder={
                selectedRole === 'supplier'
                  ? '请输入供应商账号'
                  : '请输入用户名'
              }
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
              className={`w-full ${config.bgColor} ${config.hoverBg}`}
              loading={loading}
            >
              登录
            </Button>
          </form>

          {/* 注册链接 */}
          {config.registerPath && (
            <div className="mt-4 text-center text-sm text-gray-600">
              还没有账号？{' '}
              <Link
                to={config.registerPath}
                className={`${config.textColor} hover:underline`}
              >
                {selectedRole === 'supplier' ? '申请入驻' : '立即注册'}
              </Link>
            </div>
          )}

          {/* 返回首页 */}
          <div className="mt-4 pt-4 border-t border-gray-200 text-center">
            <Link to="/" className="text-sm text-gray-500 hover:text-blue-500">
              返回首页
            </Link>
          </div>

          {/* 测试账号提示（开发环境） */}
          {import.meta.env.DEV && selectedRole === 'supplier' && (
            <div className="mt-4 p-3 bg-yellow-50 rounded-md text-xs text-yellow-700">
              <p className="font-medium">测试账号：</p>
              <p>用户名: test_supplier</p>
              <p>密码: 123456</p>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
}

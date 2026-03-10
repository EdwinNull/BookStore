/**
 * 用户中心页面
 * 显示和编辑用户信息
 */
import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Card, CardBody, Button, Input, Loading } from '@/components/common';
import { message } from '@/components/common/Message';
import { getUserInfo, updateUser, updatePassword } from '@/api/user';
import { getMyLevel } from '@/api/discount';
import { useAuthStore } from '@/stores';
import type { User, UpdatePwdParams, UserLevelResponse } from '@/types';

export function UserDashboardPage() {
  const navigate = useNavigate();
  const { token, setUser } = useAuthStore();

  const [user, setUserState] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [levelInfo, setLevelInfo] = useState<UserLevelResponse | null>(null);

  // 修改密码表单
  const [pwdForm, setPwdForm] = useState<UpdatePwdParams>({
    old_pwd: '',
    new_pwd: '',
    re_pwd: '',
  });
  const [pwdErrors, setPwdErrors] = useState<Record<string, string>>({});
  const [showPwdForm, setShowPwdForm] = useState(false);

  // 路由保护：未登录时跳转到登录页
  useEffect(() => {
    if (!token) {
      navigate('/login', { replace: true });
      return;
    }
    fetchUserInfo();
  }, [token, navigate]);

  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      const data = await getUserInfo();
      setUserState(data);
      // 同步更新全局状态
      setUser(data);
      // 获取用户等级信息
      fetchLevelInfo();
    } catch (error) {
      message.error('获取用户信息失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchLevelInfo = async () => {
    try {
      const res = await getMyLevel();
      if (res.data.code === 0) {
        setLevelInfo(res.data.data);
      }
    } catch (error) {
      // 获取等级信息失败不影响页面显示
      console.error('获取等级信息失败', error);
    }
  };

  const handleUpdateUser = async () => {
    if (!user) return;
    setSaving(true);
    try {
      await updateUser(user);
      setUser(user);
      message.success('更新成功');
    } catch (error) {
      message.error(error instanceof Error ? error.message : '更新失败');
    } finally {
      setSaving(false);
    }
  };

  const validatePwdForm = () => {
    const errors: Record<string, string> = {};

    if (!pwdForm.old_pwd) {
      errors.old_pwd = '请输入原密码';
    }
    if (!pwdForm.new_pwd) {
      errors.new_pwd = '请输入新密码';
    } else if (pwdForm.new_pwd.length < 5 || pwdForm.new_pwd.length > 16) {
      errors.new_pwd = '密码长度应为5-16位';
    }
    if (!pwdForm.re_pwd) {
      errors.re_pwd = '请确认新密码';
    } else if (pwdForm.re_pwd !== pwdForm.new_pwd) {
      errors.re_pwd = '两次输入的密码不一致';
    }

    setPwdErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleChangePwd = async () => {
    if (!validatePwdForm()) return;

    try {
      await updatePassword(pwdForm);
      message.success('密码修改成功，请重新登录');
      // 清空表单
      setPwdForm({ old_pwd: '', new_pwd: '', re_pwd: '' });
      setShowPwdForm(false);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '修改密码失败');
    }
  };

  // 未登录或加载中
  if (!token || loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Loading text="加载中..." />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <p className="text-gray-500">无法获取用户信息</p>
      </div>
    );
  }

  // 计算折扣百分比
  const discountPercent = user.discount ? Math.round((1 - user.discount) * 100) : 0;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">个人中心</h1>

      {/* 基本信息卡片 */}
      <Card className="mb-6">
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">基本信息</h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-gray-500 mb-1">用户名</label>
              <Input
                value={user.username}
                disabled
                className="bg-gray-50"
              />
            </div>
            <div>
              <label className="block text-sm text-gray-500 mb-1">姓名</label>
              <Input
                value={user.name || ''}
                onChange={(e) => setUserState({ ...user, name: e.target.value })}
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm text-gray-500 mb-1">地址</label>
              <Input
                value={user.address || ''}
                onChange={(e) => setUserState({ ...user, address: e.target.value })}
              />
            </div>
          </div>

          <div className="mt-4 flex justify-end">
            <Button onClick={handleUpdateUser} loading={saving}>
              保存修改
            </Button>
          </div>
        </CardBody>
      </Card>

      {/* 会员等级信息卡片（阶段三新增） */}
      <Card className="mb-6">
        <CardBody>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">会员等级</h2>
            <Link
              to="/user/level"
              className="text-sm text-blue-600 hover:text-blue-800"
            >
              查看等级体系
            </Link>
          </div>

          {levelInfo ? (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="bg-gradient-to-br from-yellow-50 to-yellow-100 p-4 rounded-lg border border-yellow-200">
                <p className="text-sm text-gray-500">当前等级</p>
                <p className="text-xl font-bold text-yellow-700">
                  {levelInfo.levelName}
                </p>
              </div>
              <div className="bg-gradient-to-br from-green-50 to-green-100 p-4 rounded-lg border border-green-200">
                <p className="text-sm text-gray-500">累计消费</p>
                <p className="text-xl font-bold text-green-700">
                  ¥{levelInfo.totalSpent?.toFixed(2) || '0.00'}
                </p>
              </div>
              <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-4 rounded-lg border border-blue-200">
                <p className="text-sm text-gray-500">当前折扣</p>
                <p className="text-xl font-bold text-blue-700">
                  {discountPercent > 0 ? `${discountPercent}% OFF` : '无折扣'}
                </p>
              </div>
              <div className="bg-gradient-to-br from-purple-50 to-purple-100 p-4 rounded-lg border border-purple-200">
                <p className="text-sm text-gray-500">距下一等级</p>
                {levelInfo.nextLevelName ? (
                  <div>
                    <p className="text-xl font-bold text-purple-700">
                      ¥{levelInfo.nextLevelNeeded?.toFixed(2) || '0.00'}
                    </p>
                    <p className="text-xs text-purple-500">
                      升级到 {levelInfo.nextLevelName}
                    </p>
                  </div>
                ) : (
                  <p className="text-xl font-bold text-purple-700">最高等级</p>
                )}
              </div>
            </div>
          ) : (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-500">当前等级</p>
                <p className="text-xl font-bold text-gray-900">
                  {user.creditLevel || 1}级
                </p>
              </div>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-500">账户余额</p>
                <p className="text-xl font-bold text-gray-900">
                  ¥{user.accountBalance?.toFixed(2) || '0.00'}
                </p>
              </div>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-500">折扣</p>
                <p className="text-xl font-bold text-green-600">
                  {(user.discount || 1) * 10}折
                </p>
              </div>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-500">角色</p>
                <p className="text-xl font-bold text-gray-900">
                  {user.role === 'admin' ? '管理员' : '普通用户'}
                </p>
              </div>
            </div>
          )}
        </CardBody>
      </Card>

      {/* 账户信息卡片 */}
      <Card className="mb-6">
        <CardBody>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">账户信息</h2>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500">账户余额</p>
              <p className="text-xl font-bold text-gray-900">
                ¥{user.accountBalance?.toFixed(2) || '0.00'}
              </p>
            </div>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500">信用等级</p>
              <p className="text-xl font-bold text-gray-900">
                {user.creditLevel || 1}
              </p>
            </div>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500">折扣</p>
              <p className="text-xl font-bold text-green-600">
                {(user.discount || 1) * 10}折
              </p>
            </div>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500">角色</p>
              <p className="text-xl font-bold text-gray-900">
                {user.role === 'admin' ? '管理员' : '普通用户'}
              </p>
            </div>
          </div>
        </CardBody>
      </Card>

      {/* 修改密码 */}
      <Card>
        <CardBody>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">修改密码</h2>
            <Button
              variant="ghost"
              onClick={() => setShowPwdForm(!showPwdForm)}
            >
              {showPwdForm ? '取消' : '修改密码'}
            </Button>
          </div>

          {showPwdForm && (
            <div className="space-y-4 max-w-md">
              <Input
                label="原密码"
                type="password"
                value={pwdForm.old_pwd}
                onChange={(e) => setPwdForm({ ...pwdForm, old_pwd: e.target.value })}
                error={pwdErrors.old_pwd}
              />
              <Input
                label="新密码"
                type="password"
                value={pwdForm.new_pwd}
                onChange={(e) => setPwdForm({ ...pwdForm, new_pwd: e.target.value })}
                error={pwdErrors.new_pwd}
              />
              <Input
                label="确认新密码"
                type="password"
                value={pwdForm.re_pwd}
                onChange={(e) => setPwdForm({ ...pwdForm, re_pwd: e.target.value })}
                error={pwdErrors.re_pwd}
              />
              <Button onClick={handleChangePwd}>确认修改</Button>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
}

/**
 * 管理员 - 用户管理页面
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, Button, Input, Pagination, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { getUserList, addUserBalance } from '@/api/admin';
import type { User, PageBean } from '@/types';

export function UserManagement() {
  const [users, setUsers] = useState<PageBean<User> | null>(null);
  const [loading, setLoading] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const [usernameFilter, setUsernameFilter] = useState('');
  const pageSize = 10;

  // 充值弹窗
  const [showBalanceModal, setShowBalanceModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [balanceAmount, setBalanceAmount] = useState<number>(0);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchUsers();
  }, [pageNum]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await getUserList({
        pageNum,
        pageSize,
        username: usernameFilter || undefined,
      });
      setUsers(data);
    } catch (error) {
      console.error('获取用户列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPageNum(1);
    fetchUsers();
  };

  const handleAddBalance = (user: User) => {
    setSelectedUser(user);
    setBalanceAmount(0);
    setShowBalanceModal(true);
  };

  const handleSaveBalance = async () => {
    if (!selectedUser || balanceAmount <= 0) {
      message.warning('请输入有效金额');
      return;
    }

    setSaving(true);
    try {
      await addUserBalance({
        userId: selectedUser.userId,
        balance: balanceAmount,
      });
      message.success('充值成功');
      setShowBalanceModal(false);
      fetchUsers();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '充值失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">用户管理</h1>

        {/* 搜索 */}
        <div className="flex items-center gap-2">
          <Input
            placeholder="用户名搜索"
            value={usernameFilter}
            onChange={(e) => setUsernameFilter(e.target.value)}
            className="w-48"
          />
          <Button onClick={handleSearch}>搜索</Button>
        </div>
      </div>

      <Card>
        <CardBody className="p-0">
          {loading ? (
            <Loading />
          ) : users?.items?.length ? (
            <>
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">ID</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">用户名</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">姓名</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">角色</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">余额</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">折扣</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {users.items.map((user) => (
                    <tr key={user.userId} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-sm">{user.userId}</td>
                      <td className="px-4 py-3 text-sm font-medium">{user.username}</td>
                      <td className="px-4 py-3 text-sm">{user.name || '-'}</td>
                      <td className="px-4 py-3 text-sm">
                        <span className={`px-2 py-1 rounded text-xs ${
                          user.role === 'admin' ? 'bg-purple-100 text-purple-600' : 'bg-gray-100 text-gray-600'
                        }`}>
                          {user.role === 'admin' ? '管理员' : '用户'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm text-green-600">
                        ¥{user.accountBalance?.toFixed(2) || '0.00'}
                      </td>
                      <td className="px-4 py-3 text-sm">
                        {(user.discount || 1) * 10}折
                      </td>
                      <td className="px-4 py-3 text-sm">
                        <Button variant="ghost" size="sm" onClick={() => handleAddBalance(user)}>
                          充值
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="p-4 border-t">
                <Pagination
                  current={pageNum}
                  total={users.total}
                  pageSize={pageSize}
                  onChange={setPageNum}
                />
              </div>
            </>
          ) : (
            <Empty text="暂无用户" />
          )}
        </CardBody>
      </Card>

      {/* 充值弹窗 */}
      {showBalanceModal && selectedUser && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-md">
            <CardBody>
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-bold">用户充值</h2>
                <button
                  onClick={() => setShowBalanceModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>

              <div className="mb-4">
                <p className="text-sm text-gray-500 mb-2">
                  为用户 <span className="font-medium text-gray-900">{selectedUser.username}</span> 充值
                </p>
                <p className="text-sm text-gray-500">
                  当前余额：<span className="text-green-600 font-medium">
                    ¥{selectedUser.accountBalance?.toFixed(2) || '0.00'}
                  </span>
                </p>
              </div>

              <Input
                label="充值金额"
                type="number"
                value={balanceAmount}
                onChange={(e) => setBalanceAmount(parseFloat(e.target.value) || 0)}
              />

              <div className="mt-4 flex justify-end gap-2">
                <Button variant="secondary" onClick={() => setShowBalanceModal(false)}>
                  取消
                </Button>
                <Button onClick={handleSaveBalance} loading={saving}>
                  确认充值
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      )}
    </div>
  );
}

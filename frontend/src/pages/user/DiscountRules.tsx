/**
 * 折扣规则说明页面
 * 展示会员等级体系和各等级对应的折扣
 * 使用 Tailwind CSS 样式
 */
import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/common';
import type { DiscountRule } from '@/types';
import { getDiscountRules } from '@/api/discount';

// 等级对应的颜色和图标
const levelConfig: Record<string, { icon: string; bgColor: string; textColor: string; borderColor: string }> = {
  '普通会员': { icon: '⭐', bgColor: 'bg-gray-100', textColor: 'text-gray-700', borderColor: 'border-gray-300' },
  '铜牌会员': { icon: '🏅', bgColor: 'bg-orange-50', textColor: 'text-orange-700', borderColor: 'border-orange-300' },
  '银牌会员': { icon: '🥈', bgColor: 'bg-slate-100', textColor: 'text-slate-700', borderColor: 'border-slate-300' },
  '金牌会员': { icon: '👑', bgColor: 'bg-yellow-50', textColor: 'text-yellow-700', borderColor: 'border-yellow-300' },
  '钻石会员': { icon: '💎', bgColor: 'bg-cyan-50', textColor: 'text-cyan-700', borderColor: 'border-cyan-300' },
};

const DiscountRulesPage: React.FC = () => {
  const [rules, setRules] = useState<DiscountRule[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchRules();
  }, []);

  const fetchRules = async () => {
    try {
      setLoading(true);
      const res = await getDiscountRules();
      if (res.data.code === 0) {
        setRules(res.data.data || []);
      } else {
        setError(res.data.message);
      }
    } catch (err) {
      setError('获取折扣规则失败');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-16">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-500">加载中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-center">
          <p className="text-red-600">{error}</p>
          <Button onClick={fetchRules} className="mt-4">重试</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 标题 */}
      <h1 className="text-2xl font-bold text-gray-900 text-center mb-6 flex items-center justify-center">
        <span className="mr-2">👑</span>
        会员等级体系
      </h1>

      {/* 规则说明卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="font-semibold text-gray-900 mb-4">如何升级？</h3>
          <ul className="space-y-2 text-sm text-gray-600">
            <li className="flex items-start">
              <span className="text-green-500 mr-2">✓</span>
              消费越多，等级越高，享受更多折扣
            </li>
            <li className="flex items-start">
              <span className="text-green-500 mr-2">✓</span>
              累计消费金额在订单确认收货后自动累计
            </li>
            <li className="flex items-start">
              <span className="text-green-500 mr-2">✓</span>
              等级升级后自动享受新的折扣率
            </li>
            <li className="flex items-start">
              <span className="text-green-500 mr-2">✓</span>
              折扣在下单时自动应用
            </li>
          </ul>
          <div className="mt-4 pt-4 border-t border-gray-100">
            <Link to="/user" className="text-blue-600 text-sm hover:underline">
              查看我的等级 →
            </Link>
          </div>
        </div>

        {/* 等级列表 */}
        <div className="bg-white rounded-lg shadow p-6 md:col-span-2">
          <h3 className="font-semibold text-gray-900 mb-4">会员等级说明</h3>
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-2 text-sm font-medium text-gray-500">会员等级</th>
                  <th className="text-left py-3 px-2 text-sm font-medium text-gray-500">消费金额要求</th>
                  <th className="text-left py-3 px-2 text-sm font-medium text-gray-500">折扣</th>
                </tr>
              </thead>
              <tbody>
                {rules.map((rule) => {
                  const config = levelConfig[rule.levelName] || levelConfig['普通会员'];
                  const discountPercent = Math.round((1 - Number(rule.discountRate)) * 100);
                  return (
                    <tr key={rule.ruleId} className="border-b border-gray-100">
                      <td className="py-3 px-2">
                        <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium border ${config.bgColor} ${config.textColor} ${config.borderColor}`}>
                          <span className="mr-1">{config.icon}</span>
                          {rule.levelName}
                        </span>
                      </td>
                      <td className="py-3 px-2 text-sm text-gray-600">
                        {rule.maxSpent === null
                          ? `¥${rule.minSpent} 以上`
                          : `¥${rule.minSpent} - ¥${rule.maxSpent}`}
                      </td>
                      <td className="py-3 px-2">
                        {discountPercent > 0 ? (
                          <span className="text-red-500 font-bold">
                            {Number(rule.discountRate) * 10}折
                          </span>
                        ) : (
                          <span className="text-gray-400">无折扣</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* 会员特权卡片 */}
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="font-semibold text-gray-900 mb-6">会员特权</h3>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
          {rules.slice(1).map((rule) => {
            const config = levelConfig[rule.levelName] || levelConfig['普通会员'];
            return (
              <div
                key={rule.ruleId}
                className={`text-center p-4 rounded-lg border-2 ${config.bgColor} ${config.borderColor} hover:shadow-md transition-shadow`}
              >
                <div className="text-3xl mb-2">{config.icon}</div>
                <h4 className={`font-medium ${config.textColor}`}>{rule.levelName}</h4>
                <p className="text-red-500 font-bold text-lg mt-1">
                  {Number(rule.discountRate) * 10}折
                </p>
                <p className="text-xs text-gray-500 mt-1">满¥{rule.minSpent}享受</p>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default DiscountRulesPage;

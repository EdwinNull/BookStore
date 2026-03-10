/**
 * 用户等级展示组件
 * 显示用户当前等级、折扣率、累计消费和下一等级进度
 * 使用 Tailwind CSS 样式
 */
import React from 'react';
import type { UserLevelResponse } from '@/types';

interface UserLevelCardProps {
  levelInfo: UserLevelResponse | null;
  loading?: boolean;
}

// 等级对应的颜色
const levelColors: Record<string, string> = {
  '普通会员': 'bg-gray-100 text-gray-700 border-gray-300',
  '铜牌会员': 'bg-orange-50 text-orange-700 border-orange-300',
  '银牌会员': 'bg-slate-100 text-slate-700 border-slate-300',
  '金牌会员': 'bg-yellow-50 text-yellow-700 border-yellow-300',
  '钻石会员': 'bg-cyan-50 text-cyan-700 border-cyan-300',
};

const UserLevelCard: React.FC<UserLevelCardProps> = ({ levelInfo, loading }) => {
  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow p-6 mb-4">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="h-8 bg-gray-200 rounded w-1/2 mb-4"></div>
          <div className="h-4 bg-gray-200 rounded w-3/4"></div>
        </div>
      </div>
    );
  }

  if (!levelInfo) {
    return null;
  }

  const levelColorClass = levelColors[levelInfo.levelName] || levelColors['普通会员'];
  const discountPercent = levelInfo.discountRate
    ? Math.round((1 - Number(levelInfo.discountRate)) * 100)
    : 0;

  // 计算进度百分比（如果还有下一等级）
  let progressPercent = 100;
  if (levelInfo.nextLevelNeeded !== null && levelInfo.totalSpent > 0) {
    const nextLevelMin = levelInfo.totalSpent + levelInfo.nextLevelNeeded;
    progressPercent = Math.round((levelInfo.totalSpent / nextLevelMin) * 100);
  }

  return (
    <div className="bg-white rounded-lg shadow p-6 mb-4">
      <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
        <span className="mr-2">👑</span>
        会员等级
      </h2>

      {/* 等级标签 */}
      <div className="text-center mb-4">
        <span className={`inline-block px-4 py-1 rounded-full text-base font-medium border ${levelColorClass}`}>
          {levelInfo.levelName}
        </span>
      </div>

      {/* 信息展示 */}
      <div className="space-y-2 mb-4">
        <div className="flex justify-between items-center text-sm">
          <span className="text-gray-500">累计消费</span>
          <span className="font-medium text-gray-900">¥{levelInfo.totalSpent?.toFixed(2) || '0.00'}</span>
        </div>
        <div className="flex justify-between items-center text-sm">
          <span className="text-gray-500">当前折扣</span>
          <span className={`font-bold ${discountPercent > 0 ? 'text-green-600' : 'text-gray-400'}`}>
            {discountPercent > 0 ? `${discountPercent}% OFF` : '无折扣'}
          </span>
        </div>
      </div>

      {/* 下一等级进度 */}
      {levelInfo.nextLevelName && levelInfo.nextLevelNeeded !== null && (
        <div className="mt-4">
          <div className="text-sm text-gray-500 mb-2">
            距离 <span className="inline-block px-2 py-0.5 rounded bg-blue-100 text-blue-700 text-xs">{levelInfo.nextLevelName}</span> 还需消费
            <span className="text-blue-600 font-bold ml-1">¥{levelInfo.nextLevelNeeded.toFixed(2)}</span>
          </div>
          {/* 进度条 */}
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className="bg-gradient-to-r from-blue-500 to-green-500 h-2 rounded-full transition-all duration-300"
              style={{ width: `${progressPercent}%` }}
            ></div>
          </div>
        </div>
      )}

      {/* 最高等级提示 */}
      {levelInfo.nextLevelName === null && (
        <div className="mt-4 text-center text-yellow-600 text-sm">
          🔥 您已是最高等级会员！
        </div>
      )}
    </div>
  );
};

export default UserLevelCard;

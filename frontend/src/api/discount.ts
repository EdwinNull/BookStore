/**
 * 折扣规则相关API
 * 提供折扣规则查询、用户等级信息查询等接口
 */
import request from '@/utils/request';
import type { Result, DiscountRule, UserLevelResponse } from '@/types';

/**
 * 获取所有启用的折扣规则
 * 用于前端展示会员等级体系
 */
export function getDiscountRules() {
  return request.get<Result<DiscountRule[]>>('/api/discount/rules');
}

/**
 * 获取当前用户的等级信息
 * 包括累计消费、当前等级、折扣率、距离下一等级还需消费等
 */
export function getMyLevel() {
  return request.get<Result<UserLevelResponse>>('/api/discount/my-level');
}

/**
 * 确认收货
 * @param orderId 订单ID
 */
export function confirmReceive(orderId: number) {
  return request.post<Result<string>>(`/api/order/confirm-receive/${orderId}`);
}

// ==================== 管理员接口 ====================

/**
 * 获取所有折扣规则（管理员用）
 */
export function getAllDiscountRules() {
  return request.get<Result<DiscountRule[]>>('/api/discount/admin/rules');
}

/**
 * 添加折扣规则（管理员用）
 * @param rule 折扣规则
 */
export function addDiscountRule(rule: Partial<DiscountRule>) {
  return request.post<Result<string>>('/api/discount/admin/rules', rule);
}

/**
 * 更新折扣规则（管理员用）
 * @param rule 折扣规则
 */
export function updateDiscountRule(rule: Partial<DiscountRule>) {
  return request.put<Result<string>>('/api/discount/admin/rules', rule);
}

/**
 * 删除折扣规则（管理员用）
 * @param ruleId 规则ID
 */
export function deleteDiscountRule(ruleId: number) {
  return request.delete<Result<string>>(`/api/discount/admin/rules/${ruleId}`);
}

/**
 * 切换折扣规则状态（管理员用）
 * @param ruleId 规则ID
 * @param isActive 是否启用
 */
export function toggleDiscountRule(ruleId: number, isActive: boolean) {
  return request.put<Result<string>>(`/api/discount/admin/rules/${ruleId}/status`, null, {
    params: { isActive }
  });
}

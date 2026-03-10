package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.DiscountRule;

import java.math.BigDecimal;
import java.util.List;

/**
 * 折扣计算服务接口
 * 提供折扣计算、用户等级管理等功能
 */
public interface DiscountService {

    /**
     * 根据累计消费金额计算对应的折扣率
     * @param totalSpent 累计消费金额
     * @return 折扣率（如0.95表示95折）
     */
    BigDecimal calculateDiscount(BigDecimal totalSpent);

    /**
     * 根据累计消费金额获取对应的折扣规则（包含等级信息）
     * @param totalSpent 累计消费金额
     * @return 匹配的折扣规则
     */
    DiscountRule getDiscountRule(BigDecimal totalSpent);

    /**
     * 更新用户的折扣率（根据累计消费金额重新计算）
     * @param userId 用户ID
     */
    void updateUserDiscount(Integer userId);

    /**
     * 增加用户的累计消费金额，并更新折扣率
     * @param userId 用户ID
     * @param amount 本次消费金额
     */
    void addTotalSpent(Integer userId, BigDecimal amount);

    /**
     * 获取用户的会员等级名称
     * @param totalSpent 累计消费金额
     * @return 会员等级名称（如：普通会员、铜牌会员等）
     */
    String getUserLevel(BigDecimal totalSpent);

    /**
     * 获取所有启用的折扣规则（供前端展示）
     * @return 折扣规则列表
     */
    List<DiscountRule> getActiveDiscountRules();

    /**
     * 获取所有折扣规则（供管理后台使用）
     * @return 所有折扣规则列表
     */
    List<DiscountRule> getAllDiscountRules();

    /**
     * 添加新的折扣规则（管理员操作）
     * @param rule 折扣规则
     */
    void addDiscountRule(DiscountRule rule);

    /**
     * 更新折扣规则（管理员操作）
     * @param rule 折扣规则
     */
    void updateDiscountRule(DiscountRule rule);

    /**
     * 删除折扣规则（管理员操作）
     * @param ruleId 规则ID
     */
    void deleteDiscountRule(Integer ruleId);

    /**
     * 切换折扣规则的启用状态（管理员操作）
     * @param ruleId 规则ID
     * @param isActive 是否启用
     */
    void toggleDiscountRule(Integer ruleId, Boolean isActive);
}

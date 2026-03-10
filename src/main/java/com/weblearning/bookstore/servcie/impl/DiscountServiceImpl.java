package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.mapper.DiscountRuleMapper;
import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.DiscountRule;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.DiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 折扣计算服务实现类
 * 实现基于累计消费金额的会员等级折扣计算逻辑
 */
@Service
public class DiscountServiceImpl implements DiscountService {

    private static final Logger logger = LoggerFactory.getLogger(DiscountServiceImpl.class);

    /**
     * 默认折扣率（无折扣）
     */
    private static final BigDecimal DEFAULT_DISCOUNT_RATE = new BigDecimal("1.00");

    /**
     * 默认会员等级名称
     */
    private static final String DEFAULT_LEVEL_NAME = "普通会员";

    @Autowired
    private DiscountRuleMapper discountRuleMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据累计消费金额计算对应的折扣率
     * @param totalSpent 累计消费金额
     * @return 折扣率（如0.95表示95折）
     */
    @Override
    public BigDecimal calculateDiscount(BigDecimal totalSpent) {
        if (totalSpent == null || totalSpent.compareTo(BigDecimal.ZERO) < 0) {
            return DEFAULT_DISCOUNT_RATE;
        }

        DiscountRule rule = discountRuleMapper.findByTotalSpent(totalSpent);
        if (rule != null) {
            return rule.getDiscountRate();
        }

        return DEFAULT_DISCOUNT_RATE;
    }

    /**
     * 根据累计消费金额获取对应的折扣规则（包含等级信息）
     * @param totalSpent 累计消费金额
     * @return 匹配的折扣规则
     */
    @Override
    public DiscountRule getDiscountRule(BigDecimal totalSpent) {
        if (totalSpent == null || totalSpent.compareTo(BigDecimal.ZERO) < 0) {
            totalSpent = BigDecimal.ZERO;
        }

        DiscountRule rule = discountRuleMapper.findByTotalSpent(totalSpent);
        if (rule != null) {
            return rule;
        }

        // 如果没有匹配的规则，返回默认规则
        DiscountRule defaultRule = new DiscountRule();
        defaultRule.setDiscountRate(DEFAULT_DISCOUNT_RATE);
        defaultRule.setLevelName(DEFAULT_LEVEL_NAME);
        defaultRule.setMinSpent(BigDecimal.ZERO);
        return defaultRule;
    }

    /**
     * 更新用户的折扣率（根据累计消费金额重新计算）
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void updateUserDiscount(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("更新用户折扣失败：用户不存在，userId={}", userId);
            return;
        }

        // 获取累计消费金额，如果为null则使用0
        BigDecimal totalSpent = user.getTotalSpent();
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        // 计算新的折扣率
        BigDecimal newDiscount = calculateDiscount(totalSpent);

        // 更新用户的折扣率和更新时间
        userMapper.updateDiscount(userId, newDiscount.doubleValue());
        userMapper.updateDiscountUpdatedAt(userId, LocalDateTime.now());

        logger.info("更新用户折扣成功：userId={}, totalSpent={}, newDiscount={}",
                userId, totalSpent, newDiscount);
    }

    /**
     * 增加用户的累计消费金额，并更新折扣率
     * @param userId 用户ID
     * @param amount 本次消费金额
     */
    @Override
    @Transactional
    public void addTotalSpent(Integer userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("增加累计消费金额失败：金额无效，userId={}, amount={}", userId, amount);
            return;
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("增加累计消费金额失败：用户不存在，userId={}", userId);
            return;
        }

        // 获取当前累计消费金额
        BigDecimal currentTotalSpent = user.getTotalSpent();
        if (currentTotalSpent == null) {
            currentTotalSpent = BigDecimal.ZERO;
        }

        // 计算新的累计消费金额
        BigDecimal newTotalSpent = currentTotalSpent.add(amount);

        // 更新累计消费金额
        userMapper.updateTotalSpent(userId, newTotalSpent);

        logger.info("增加用户累计消费金额：userId={}, oldTotalSpent={}, amount={}, newTotalSpent={}",
                userId, currentTotalSpent, amount, newTotalSpent);

        // 更新用户折扣率
        updateUserDiscount(userId);
    }

    /**
     * 获取用户的会员等级名称
     * @param totalSpent 累计消费金额
     * @return 会员等级名称（如：普通会员、铜牌会员等）
     */
    @Override
    public String getUserLevel(BigDecimal totalSpent) {
        DiscountRule rule = getDiscountRule(totalSpent);
        return rule.getLevelName();
    }

    /**
     * 获取所有启用的折扣规则（供前端展示）
     * @return 折扣规则列表
     */
    @Override
    public List<DiscountRule> getActiveDiscountRules() {
        return discountRuleMapper.findAllActiveRules();
    }

    /**
     * 获取所有折扣规则（供管理后台使用）
     * @return 所有折扣规则列表
     */
    @Override
    public List<DiscountRule> getAllDiscountRules() {
        return discountRuleMapper.findAllRules();
    }

    /**
     * 添加新的折扣规则（管理员操作）
     * @param rule 折扣规则
     */
    @Override
    @Transactional
    public void addDiscountRule(DiscountRule rule) {
        // 验证折扣规则
        validateDiscountRule(rule);

        // 默认启用
        if (rule.getIsActive() == null) {
            rule.setIsActive(true);
        }

        discountRuleMapper.insert(rule);
        logger.info("添加折扣规则成功：levelName={}, discountRate={}",
                rule.getLevelName(), rule.getDiscountRate());
    }

    /**
     * 更新折扣规则（管理员操作）
     * @param rule 折扣规则
     */
    @Override
    @Transactional
    public void updateDiscountRule(DiscountRule rule) {
        if (rule.getRuleId() == null) {
            throw new IllegalArgumentException("更新折扣规则时，规则ID不能为空");
        }

        // 验证折扣规则
        validateDiscountRule(rule);

        discountRuleMapper.update(rule);
        logger.info("更新折扣规则成功：ruleId={}, levelName={}",
                rule.getRuleId(), rule.getLevelName());
    }

    /**
     * 删除折扣规则（管理员操作）
     * @param ruleId 规则ID
     */
    @Override
    @Transactional
    public void deleteDiscountRule(Integer ruleId) {
        discountRuleMapper.delete(ruleId);
        logger.info("删除折扣规则成功：ruleId={}", ruleId);
    }

    /**
     * 切换折扣规则的启用状态（管理员操作）
     * @param ruleId 规则ID
     * @param isActive 是否启用
     */
    @Override
    @Transactional
    public void toggleDiscountRule(Integer ruleId, Boolean isActive) {
        discountRuleMapper.updateStatus(ruleId, isActive);
        logger.info("切换折扣规则状态：ruleId={}, isActive={}", ruleId, isActive);
    }

    /**
     * 验证折扣规则的有效性
     * @param rule 折扣规则
     */
    private void validateDiscountRule(DiscountRule rule) {
        if (rule.getMinSpent() == null) {
            throw new IllegalArgumentException("最低消费金额不能为空");
        }
        if (rule.getDiscountRate() == null) {
            throw new IllegalArgumentException("折扣率不能为空");
        }
        if (rule.getLevelName() == null || rule.getLevelName().trim().isEmpty()) {
            throw new IllegalArgumentException("等级名称不能为空");
        }

        // 验证折扣率范围（0-1之间）
        BigDecimal discountRate = rule.getDiscountRate();
        if (discountRate.compareTo(BigDecimal.ZERO) < 0 ||
            discountRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("折扣率必须在0-1之间");
        }

        // 验证消费金额区间
        if (rule.getMaxSpent() != null &&
            rule.getMinSpent().compareTo(rule.getMaxSpent()) > 0) {
            throw new IllegalArgumentException("最低消费金额不能大于最高消费金额");
        }
    }
}

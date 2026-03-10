package com.weblearning.bookstore.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 折扣规则实体类
 * 用于定义不同消费金额区间对应的折扣率和会员等级
 */
public class DiscountRule {

    /**
     * 规则ID（主键）
     */
    private Integer ruleId;

    /**
     * 最低消费金额（满足此金额才能享受该折扣）
     */
    private BigDecimal minSpent;

    /**
     * 最高消费金额（NULL表示无上限）
     */
    private BigDecimal maxSpent;

    /**
     * 折扣率（如0.95表示95折，即原价的95%）
     */
    private BigDecimal discountRate;

    /**
     * 会员等级名称（如：普通会员、铜牌会员、银牌会员等）
     */
    private String levelName;

    /**
     * 等级描述说明
     */
    private String description;

    /**
     * 是否启用该规则
     */
    private Boolean isActive;

    /**
     * 规则创建时间
     */
    private LocalDateTime createdAt;

    // ==================== Getter 和 Setter 方法 ====================

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public BigDecimal getMinSpent() {
        return minSpent;
    }

    public void setMinSpent(BigDecimal minSpent) {
        this.minSpent = minSpent;
    }

    public BigDecimal getMaxSpent() {
        return maxSpent;
    }

    public void setMaxSpent(BigDecimal maxSpent) {
        this.maxSpent = maxSpent;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

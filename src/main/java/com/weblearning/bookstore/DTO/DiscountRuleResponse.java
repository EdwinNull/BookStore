package com.weblearning.bookstore.DTO;

import java.math.BigDecimal;

/**
 * 折扣规则响应DTO
 * 用于前端展示折扣规则信息
 */
public class DiscountRuleResponse {

    /**
     * 规则ID
     */
    private Integer ruleId;

    /**
     * 最低消费金额
     */
    private BigDecimal minSpent;

    /**
     * 最高消费金额
     */
    private BigDecimal maxSpent;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean isActive;

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
}

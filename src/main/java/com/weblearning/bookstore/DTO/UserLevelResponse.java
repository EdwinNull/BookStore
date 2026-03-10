package com.weblearning.bookstore.DTO;

import java.math.BigDecimal;

/**
 * 用户等级信息响应DTO
 * 用于返回当前用户的等级、折扣率和累计消费等信息
 */
public class UserLevelResponse {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 累计消费金额
     */
    private BigDecimal totalSpent;

    /**
     * 当前会员等级名称
     */
    private String levelName;

    /**
     * 当前折扣率（如0.95表示95折）
     */
    private BigDecimal discountRate;

    /**
     * 距离下一等级还需消费的金额
     * 如果已是最高等级，则为null
     */
    private BigDecimal nextLevelNeeded;

    /**
     * 下一等级名称
     */
    private String nextLevelName;

    // ==================== Getter 和 Setter 方法 ====================

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getNextLevelNeeded() {
        return nextLevelNeeded;
    }

    public void setNextLevelNeeded(BigDecimal nextLevelNeeded) {
        this.nextLevelNeeded = nextLevelNeeded;
    }

    public String getNextLevelName() {
        return nextLevelName;
    }

    public void setNextLevelName(String nextLevelName) {
        this.nextLevelName = nextLevelName;
    }
}

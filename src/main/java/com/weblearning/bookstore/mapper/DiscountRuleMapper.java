package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.DiscountRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 折扣规则数据访问层接口
 * 提供折扣规则的CRUD操作
 */
@Mapper
public interface DiscountRuleMapper {

    /**
     * 获取所有启用的折扣规则（按最低消费金额降序排列）
     * @return 折扣规则列表
     */
    List<DiscountRule> findAllActiveRules();

    /**
     * 获取所有折扣规则（包括未启用的，用于管理）
     * @return 所有折扣规则列表
     */
    List<DiscountRule> findAllRules();

    /**
     * 根据累计消费金额获取对应的折扣规则
     * 查找满足 minSpent <= totalSpent <= maxSpent 的规则
     * @param totalSpent 累计消费金额
     * @return 匹配的折扣规则，如果没有匹配则返回默认规则
     */
    DiscountRule findByTotalSpent(@Param("totalSpent") BigDecimal totalSpent);

    /**
     * 根据ID获取折扣规则
     * @param ruleId 规则ID
     * @return 折扣规则
     */
    DiscountRule findById(@Param("ruleId") Integer ruleId);

    /**
     * 添加新的折扣规则
     * @param rule 折扣规则实体
     */
    void insert(DiscountRule rule);

    /**
     * 更新折扣规则
     * @param rule 折扣规则实体
     */
    void update(DiscountRule rule);

    /**
     * 删除折扣规则
     * @param ruleId 规则ID
     */
    void delete(@Param("ruleId") Integer ruleId);

    /**
     * 切换折扣规则的启用状态
     * @param ruleId 规则ID
     * @param isActive 是否启用
     */
    void updateStatus(@Param("ruleId") Integer ruleId, @Param("isActive") Boolean isActive);
}

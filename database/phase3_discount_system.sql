-- =====================================================
-- 阶段三：折扣计算优化 - 数据库变更脚本
-- 创建时间: 2026-03-10
-- 说明: 添加用户累计消费金额字段、创建折扣规则表
-- =====================================================

-- 1. 用户表添加累计消费金额字段
ALTER TABLE users ADD COLUMN total_spent DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计消费金额';
ALTER TABLE users ADD COLUMN discount_updated_at DATETIME COMMENT '折扣更新时间';

-- 2. 创建折扣规则表
CREATE TABLE discount_rules (
    rule_id INT PRIMARY KEY AUTO_INCREMENT,
    min_spent DECIMAL(10,2) NOT NULL COMMENT '最低消费金额',
    max_spent DECIMAL(10,2) COMMENT '最高消费金额（NULL表示无上限）',
    discount_rate DECIMAL(3,2) NOT NULL COMMENT '折扣率（如0.95表示95折）',
    level_name VARCHAR(20) NOT NULL COMMENT '等级名称',
    description VARCHAR(100) COMMENT '等级描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '折扣规则表';

-- 3. 初始化折扣规则数据
INSERT INTO discount_rules (min_spent, max_spent, discount_rate, level_name, description) VALUES
(0, 99.99, 1.00, '普通会员', '消费满0元，无折扣'),
(100, 499.99, 0.98, '铜牌会员', '消费满100元，享98折'),
(500, 999.99, 0.95, '银牌会员', '消费满500元，享95折'),
(1000, 4999.99, 0.90, '金牌会员', '消费满1000元，享9折'),
(5000, NULL, 0.85, '钻石会员', '消费满5000元，享85折');

-- 4. 初始化现有用户的累计消费金额（根据已完成的订单计算）
UPDATE users u
SET total_spent = (
    SELECT COALESCE(SUM(total_price), 0)
    FROM orders o
    WHERE o.user_id = u.user_id AND o.status = 'delivered'
),
discount_updated_at = NOW()
WHERE total_spent = 0 OR total_spent IS NULL;

-- 5. 根据累计消费金额更新用户折扣率
UPDATE users u
SET discount = (
    SELECT discount_rate
    FROM discount_rules
    WHERE is_active = TRUE
      AND u.total_spent >= min_spent
      AND (max_spent IS NULL OR u.total_spent <= max_spent)
    ORDER BY min_spent DESC
    LIMIT 1
)
WHERE discount IS NULL OR discount = 0;

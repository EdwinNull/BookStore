-- =====================================================
-- 阶段一：供应商用户体系 数据库变更脚本（修复版）
-- 创建时间：2026-03-10
-- 更新时间：2026-03-10
-- 说明：为供应商表添加用户认证相关字段
-- 注意：由于表可能已存在部分字段，采用逐条执行方式
-- =====================================================

-- 首先查看当前表结构（帮助确认哪些字段已存在）
-- SHOW COLUMNS FROM suppliers;

-- =====================================================
-- 1. 添加用户认证相关字段
-- 注意：如果字段已存在会报错，可以忽略或注释掉对应的语句
-- =====================================================

-- 添加 username 字段（登录账号）
ALTER TABLE suppliers ADD COLUMN username VARCHAR(50) UNIQUE COMMENT '供应商登录账号' AFTER supplier_id;

-- 添加 password 字段
ALTER TABLE suppliers ADD COLUMN password VARCHAR(255) COMMENT '密码（MD5加密存储）' AFTER username;

-- 添加 contact_person 字段（联系人）
ALTER TABLE suppliers ADD COLUMN contact_person VARCHAR(50) COMMENT '联系人' AFTER contact_info;

-- 添加 phone 字段（联系电话）
ALTER TABLE suppliers ADD COLUMN phone VARCHAR(20) COMMENT '联系电话' AFTER contact_person;

-- 添加 email 字段
ALTER TABLE suppliers ADD COLUMN email VARCHAR(100) COMMENT '邮箱' AFTER phone;

-- 添加 status 字段（状态）
ALTER TABLE suppliers ADD COLUMN status ENUM('active', 'inactive', 'pending') DEFAULT 'pending' COMMENT '状态：active-激活，inactive-停用，pending-待审核' AFTER email;

-- 添加 updated_at 字段（更新时间）
-- 注意：如果 created_at 已存在，只添加 updated_at
ALTER TABLE suppliers ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- =====================================================
-- 2. 创建索引优化查询性能
-- =====================================================
CREATE INDEX idx_supplier_username ON suppliers(username);
CREATE INDEX idx_supplier_status ON suppliers(status);

-- =====================================================
-- 3. 添加注释到表
-- =====================================================
ALTER TABLE suppliers COMMENT '供应商表 - 包含供应商基本信息和登录认证信息';

-- =====================================================
-- 4. 为现有供应商创建默认登录账号（可选）
-- 密码默认为 '123456' 的MD5值：e10adc3949ba59abbe56e057f20f883e
-- 取消注释以下语句来初始化现有供应商
-- =====================================================
-- UPDATE suppliers SET
--     username = CONCAT('supplier_', supplier_id),
--     password = 'e10adc3949ba59abbe56e057f20f883e',
--     status = 'active'
-- WHERE username IS NULL OR username = '';

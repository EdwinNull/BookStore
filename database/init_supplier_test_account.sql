-- =====================================================
-- 供应商测试账号初始化
-- =====================================================

-- 创建一个测试供应商账号
-- 用户名: test_supplier
-- 密码: 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO suppliers (
    username,
    password,
    name,
    address,
    contact_info,
    contact_person,
    phone,
    email,
    status
) VALUES (
    'test_supplier',
    'e10adc3949ba59abbe56e057f20f883e',
    '测试供应商',
    '北京市朝阳区测试路1号',
    'QQ: 12345678',
    '张三',
    '13800138000',
    'test@example.com',
    'active'
);

-- 查看插入结果
SELECT supplier_id, username, name, status FROM suppliers WHERE username = 'test_supplier';

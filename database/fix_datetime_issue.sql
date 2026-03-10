-- ======================================
-- 时间数据修复脚本
-- 用于修复订单、采购、发货等表中时间显示为1970年或NULL的问题
-- 执行前请先备份数据库
-- ======================================

USE bookstore;

-- ======================================
-- 1. 检查订单表中的异常时间数据
-- ======================================

-- 查看订单表中时间异常的记录
-- 1970-01-01 08:00:00 是 Unix 时间戳 0 对应的时间（东八区）
SELECT order_id, user_id, order_date, status
FROM orders
WHERE order_date IS NULL
   OR order_date = '1970-01-01 08:00:00'
   OR order_date < '2020-01-01';

-- 如果存在异常数据，可以使用以下语句修复
-- 将异常时间更新为记录创建时间
UPDATE orders
SET order_date = created_at
WHERE order_date IS NULL
   OR order_date = '1970-01-01 08:00:00'
   OR order_date < '2020-01-01';

-- ======================================
-- 2. 检查采购订单表中的异常时间数据
-- ======================================

SELECT purchase_order_id, supplier_id, order_date, status
FROM purchase_orders
WHERE order_date IS NULL
   OR order_date = '1970-01-01 08:00:00'
   OR order_date < '2020-01-01';

-- 修复采购订单时间
UPDATE purchase_orders
SET order_date = created_at
WHERE order_date IS NULL
   OR order_date = '1970-01-01 08:00:00'
   OR order_date < '2020-01-01';

-- ======================================
-- 3. 检查发货表中的异常时间数据
-- ======================================

SELECT shipment_id, order_id, shipping_date, status
FROM shipments
WHERE shipping_date IS NULL
   OR shipping_date = '1970-01-01 08:00:00'
   OR shipping_date < '2020-01-01';

-- 修复发货时间（设置为创建时间，表示备货开始时间）
UPDATE shipments
SET shipping_date = created_at
WHERE shipping_date IS NULL
   OR shipping_date = '1970-01-01 08:00:00'
   OR shipping_date < '2020-01-01';

-- ======================================
-- 4. 检查缺书登记表中的异常时间数据
-- ======================================

SELECT missing_books_id, book_name, registration_date, status
FROM missing_books
WHERE registration_date IS NULL
   OR registration_date = '1970-01-01 08:00:00'
   OR registration_date < '2020-01-01';

-- 修复缺书登记时间
UPDATE missing_books
SET registration_date = created_at
WHERE registration_date IS NULL
   OR registration_date = '1970-01-01 08:00:00'
   OR registration_date < '2020-01-01';

-- ======================================
-- 5. 验证修复结果
-- ======================================

-- 验证订单表
SELECT 'orders表异常记录数:' AS '检查项', COUNT(*) AS '数量'
FROM orders
WHERE order_date IS NULL
   OR order_date = '1970-01-01 08:00:00'
   OR order_date < '2020-01-01';

-- 验证采购订单表
SELECT 'purchase_orders表异常记录数:' AS '检查项', COUNT(*) AS '数量'
FROM purchase_orders
WHERE order_date IS NULL
   OR order_date = '1970-01-01 08:00:00'
   OR order_date < '2020-01-01';

-- 验证发货表
SELECT 'shipments表异常记录数:' AS '检查项', COUNT(*) AS '数量'
FROM shipments
WHERE shipping_date IS NULL
   OR shipping_date = '1970-01-01 08:00:00'
   OR shipping_date < '2020-01-01';

-- 验证缺书登记表
SELECT 'missing_books表异常记录数:' AS '检查项', COUNT(*) AS '数量'
FROM missing_books
WHERE registration_date IS NULL
   OR registration_date = '1970-01-01 08:00:00'
   OR registration_date < '2020-01-01';

-- ======================================
-- 修复完成
-- ======================================

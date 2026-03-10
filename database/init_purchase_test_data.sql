-- =====================================================
-- 采购订单测试数据（智能版本）
-- 创建时间：2026-03-10
-- 说明：自动查找供应商ID并插入测试数据
-- =====================================================

-- 步骤1：查看所有供应商及其ID
SELECT '========== 当前供应商列表 ==========' AS info;
SELECT supplier_id, username, name, status FROM suppliers;

-- 步骤2：查看所有图书
SELECT '========== 当前图书列表 ==========' AS info;
SELECT book_id, title, price, stock_quantity FROM books LIMIT 10;

-- 步骤3：为所有 active 状态的供应商插入测试采购订单
-- 注意：如果已有数据，请先清理或跳过

-- 先查看 test_supplier 的 ID
SET @test_supplier_id = (SELECT supplier_id FROM suppliers WHERE username = 'test_supplier' LIMIT 1);

-- 如果 test_supplier 存在，为其插入测试数据
INSERT INTO purchase_orders (supplier_id, book_id, quantity, price, order_date, status, total_amount, remark)
SELECT
    @test_supplier_id,
    b.book_id,
    CASE
        WHEN b.stock_quantity > 0 THEN LEAST(b.stock_quantity, 100)
        ELSE 50
    END AS quantity,
    b.price,
    NOW() AS order_date,
    'pending' AS status,
    b.price * CASE WHEN b.stock_quantity > 0 THEN LEAST(b.stock_quantity, 100) ELSE 50 END AS total_amount,
    CONCAT('测试采购订单 - ', b.title) AS remark
FROM books b
WHERE @test_supplier_id IS NOT NULL
LIMIT 6;

-- 步骤4：插入不同状态的订单（如果上面成功）
-- 为第一个 pending 订单改为 confirmed
UPDATE purchase_orders
SET status = 'confirmed', remark = '已确认的测试订单'
WHERE supplier_id = @test_supplier_id AND status = 'pending'
ORDER BY purchase_order_id DESC
LIMIT 1;

-- 为第二个 pending 订单改为 shipped
UPDATE purchase_orders
SET status = 'shipped', remark = '已发货的测试订单 - 物流单号: SF123456'
WHERE supplier_id = @test_supplier_id AND status = 'pending'
ORDER BY purchase_order_id DESC
LIMIT 1;

-- 为第三个 pending 订单改为 completed
UPDATE purchase_orders
SET status = 'completed', remark = '已完成的测试订单'
WHERE supplier_id = @test_supplier_id AND status = 'pending'
ORDER BY purchase_order_id DESC
LIMIT 1;

-- 为第四个 pending 订单改为 rejected
UPDATE purchase_orders
SET status = 'rejected', remark = '已拒绝的测试订单 - 库存不足'
WHERE supplier_id = @test_supplier_id AND status = 'pending'
ORDER BY purchase_order_id DESC
LIMIT 1;

-- 步骤5：查看插入结果
SELECT '========== 采购订单插入结果 ==========' AS info;
SELECT
    po.purchase_order_id,
    po.supplier_id,
    s.name AS supplier_name,
    s.username,
    po.book_id,
    b.title AS book_title,
    po.quantity,
    po.price,
    po.total_amount,
    po.status,
    po.order_date,
    po.remark
FROM purchase_orders po
LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id
LEFT JOIN books b ON po.book_id = b.book_id
WHERE po.supplier_id = @test_supplier_id
ORDER BY po.order_date DESC;

-- 步骤6：统计结果
SELECT '========== 订单统计 ==========' AS info;
SELECT
    status,
    COUNT(*) AS count
FROM purchase_orders
WHERE supplier_id = @test_supplier_id
GROUP BY status;

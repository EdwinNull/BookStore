-- =====================================================
-- 阶段二：完善采购、发货流程 数据库变更脚本
-- 创建时间：2026-03-10
-- 说明：增强采购订单和物流功能
-- =====================================================

-- ==================== 1. 采购订单表增强 ====================

-- 添加新字段（如果字段已存在会报错，可以忽略）
-- 如果是首次执行，直接运行以下语句
ALTER TABLE purchase_orders
    ADD COLUMN expected_delivery_date DATE COMMENT '预计到货日期',
    ADD COLUMN actual_delivery_date DATE COMMENT '实际到货日期',
    ADD COLUMN total_amount DECIMAL(12,2) COMMENT '采购总金额',
    ADD COLUMN remark TEXT COMMENT '备注';

-- 修改状态字段注释
ALTER TABLE purchase_orders MODIFY COLUMN status VARCHAR(20) COMMENT '状态：pending-待处理，confirmed-已确认，shipped-已发货，completed-已完成，rejected-已拒绝，cancelled-已取消';

-- ==================== 2. 采购订单日志表 ====================

CREATE TABLE IF NOT EXISTS purchase_order_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    purchase_order_id INT NOT NULL COMMENT '采购订单ID',
    old_status VARCHAR(20) COMMENT '旧状态',
    new_status VARCHAR(20) NOT NULL COMMENT '新状态',
    operator_id INT COMMENT '操作人ID',
    operator_type ENUM('admin', 'supplier') NOT NULL COMMENT '操作人类型',
    operator_name VARCHAR(50) COMMENT '操作人名称',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_purchase_order_id (purchase_order_id),
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(purchase_order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单状态变更日志';

-- ==================== 3. 物流信息表增强 ====================

-- 检查ship表是否存在，如果不存在则创建
CREATE TABLE IF NOT EXISTS ship (
    shipment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    shipping_address TEXT COMMENT '收货地址',
    shipping_date DATETIME COMMENT '发货日期',
    tracking_number VARCHAR(100) COMMENT '物流单号',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待发货，shipped-已发货，delivered-已送达',
    carrier VARCHAR(50) COMMENT '快递公司',
    estimated_delivery DATE COMMENT '预计送达时间',
    actual_delivery DATETIME COMMENT '实际送达时间',
    shipping_fee DECIMAL(10,2) COMMENT '运费',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- ==================== 4. 物流轨迹表 ====================

CREATE TABLE IF NOT EXISTS shipping_tracks (
    track_id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL COMMENT '物流ID',
    location VARCHAR(100) COMMENT '当前位置',
    status VARCHAR(50) COMMENT '物流状态描述',
    description TEXT COMMENT '详细描述',
    operator VARCHAR(50) COMMENT '操作人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    INDEX idx_shipment_id (shipment_id),
    FOREIGN KEY (shipment_id) REFERENCES ship(shipment_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹记录表';

-- ==================== 5. 添加测试数据（可选）====================

-- 插入测试采购订单（如果需要）
-- INSERT INTO purchase_orders (supplier_id, book_id, quantity, price, status, order_date)
-- VALUES (1, 1, 100, 25.00, 'pending', NOW());

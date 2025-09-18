-- ======================================
-- 书店管理系统数据库创建脚本
-- 数据库名称: bookstore
-- 创建日期: 2025-09-10
-- ======================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

-- ======================================
-- 1. 用户表 (users)
-- 管理用户基本信息、账户余额、信用等级等
-- ======================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名，唯一标识',
    password VARCHAR(255) NOT NULL COMMENT '密码，MD5加密',
    name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    address TEXT COMMENT '用户地址',
    account_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    credit_level INT DEFAULT 1 COMMENT '信用等级 1-5级',
    role ENUM('admin', 'user') DEFAULT 'user' COMMENT '用户角色：管理员/普通用户',
    discount DECIMAL(4,2) DEFAULT 1.00 COMMENT '折扣率，根据信用等级确定',
    over_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '透支额度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='用户表';

-- ======================================
-- 2. 丛书系列表 (series)
-- 管理图书系列分类
-- ======================================
CREATE TABLE series (
    series_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '丛书ID，主键',
    series_name VARCHAR(200) NOT NULL COMMENT '丛书名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='丛书系列表';

-- ======================================
-- 3. 供应商表 (suppliers)
-- 管理供应商基本信息
-- ======================================
CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID，主键',
    name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    address TEXT COMMENT '供应商地址',
    contact_info VARCHAR(500) COMMENT '联系方式（电话、邮箱等）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='供应商表';

-- ======================================
-- 4. 图书表 (books)
-- 管理图书详细信息和库存
-- ======================================
CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID，主键',
    title VARCHAR(300) NOT NULL COMMENT '书名',
    author1 VARCHAR(100) COMMENT '第一作者',
    author2 VARCHAR(100) COMMENT '第二作者',
    author3 VARCHAR(100) COMMENT '第三作者',
    author4 VARCHAR(100) COMMENT '第四作者',
    publisher VARCHAR(200) COMMENT '出版社',
    price DECIMAL(10,2) NOT NULL COMMENT '图书价格',
    keywords TEXT COMMENT '关键字，用于搜索',
    table_of_contents TEXT COMMENT '目录',
    cover_image VARCHAR(500) COMMENT '封面图片路径',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    supplier_id INT COMMENT '主要供应商ID',
    series_id INT COMMENT '所属丛书系列ID',
    storage_location VARCHAR(200) COMMENT '存储位置',
    low_quantity_flag INT DEFAULT 0 COMMENT '库存不足标记：0-充足，1-不足',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE SET NULL,
    INDEX idx_title (title),
    INDEX idx_author1 (author1),
    INDEX idx_publisher (publisher),
    INDEX idx_keywords (keywords(100)),
    INDEX idx_stock_quantity (stock_quantity)
) COMMENT='图书表';

-- ======================================
-- 5. 供应商图书关系表 (supplier_books)
-- 管理多个供应商提供同一本书的关系和价格
-- ======================================
CREATE TABLE supplier_books (
    supplier_book_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID，主键',
    supplier_id INT NOT NULL COMMENT '供应商ID',
    book_id INT NOT NULL COMMENT '图书ID',
    quantity INT DEFAULT 0 COMMENT '供应商可提供数量',
    price DECIMAL(10,2) COMMENT '供应商报价',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE KEY uk_supplier_book (supplier_id, book_id)
) COMMENT='供应商图书关系表';

-- ======================================
-- 6. 订单表 (orders)
-- 管理客户订单基本信息
-- ======================================
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID，主键',
    user_id INT NOT NULL COMMENT '用户ID',
    total_price DECIMAL(10,2) NOT NULL COMMENT '订单总价',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    status ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled') 
           DEFAULT 'pending' COMMENT '订单状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_date (order_date),
    INDEX idx_status (status)
) COMMENT='订单表';

-- ======================================
-- 7. 订单详情表 (order_details)
-- 管理订单中具体的图书信息
-- ======================================
CREATE TABLE order_details (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID，主键',
    order_id INT NOT NULL COMMENT '订单ID',
    book_id INT NOT NULL COMMENT '图书ID',
    quantity INT NOT NULL COMMENT '购买数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价（下单时的价格）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id)
) COMMENT='订单详情表';

-- ======================================
-- 8. 缺书登记表 (missing_books)
-- 记录缺书信息，用于生成采购需求
-- ======================================
CREATE TABLE missing_books (
    missing_books_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '缺书记录ID，主键',
    book_id INT COMMENT '图书ID（如果是现有图书）',
    book_name VARCHAR(300) NOT NULL COMMENT '图书名称',
    supplier_id INT COMMENT '推荐供应商ID',
    quantity INT NOT NULL COMMENT '缺少数量',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    user_id INT COMMENT '登记用户ID',
    publisher VARCHAR(200) COMMENT '出版社',
    requested_quantity INT COMMENT '需求数量',
    status ENUM('pending', 'ordered', 'received', 'cancelled') 
           DEFAULT 'pending' COMMENT '状态：待处理/已订购/已到货/已取消',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_book_id (book_id),
    INDEX idx_registration_date (registration_date),
    INDEX idx_status (status)
) COMMENT='缺书登记表';

-- ======================================
-- 9. 采购订单表 (purchase_orders)
-- 管理向供应商的采购订单
-- ======================================
CREATE TABLE purchase_orders (
    purchase_order_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '采购订单ID，主键',
    supplier_id INT NOT NULL COMMENT '供应商ID',
    book_id INT NOT NULL COMMENT '图书ID',
    quantity INT NOT NULL COMMENT '采购数量',
    price DECIMAL(10,2) NOT NULL COMMENT '采购单价',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    status ENUM('pending', 'ordered', 'received', 'cancelled') 
           DEFAULT 'pending' COMMENT '状态：待处理/已下单/已收货/已取消',
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_book_id (book_id),
    INDEX idx_order_date (order_date),
    INDEX idx_status (status)
) COMMENT='采购订单表';

-- ======================================
-- 10. 发货表 (shipments)
-- 管理订单的发货信息
-- ======================================
CREATE TABLE shipments (
    shipment_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '发货ID，主键',
    order_id INT NOT NULL COMMENT '订单ID',
    shipping_address TEXT NOT NULL COMMENT '收货地址',
    shipping_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发货时间',
    tracking_number VARCHAR(100) COMMENT '快递跟踪号',
    status ENUM('preparing', 'shipped', 'in_transit', 'delivered', 'returned') 
           DEFAULT 'preparing' COMMENT '发货状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_shipping_date (shipping_date),
    INDEX idx_status (status)
) COMMENT='发货表';

-- ======================================
-- 初始化数据
-- ======================================

-- 插入默认管理员用户
INSERT INTO users (username, password, name, role, credit_level, discount, over_balance) 
VALUES ('admin', MD5('admin123'), '系统管理员', 'admin', 5, 0.75, 999999.99);

-- 插入示例丛书系列
INSERT INTO series (series_name) VALUES 
('计算机科学与技术'),
('文学经典'),
('历史与文化'),
('科学技术');

-- 插入示例供应商
INSERT INTO suppliers (name, address, contact_info) VALUES 
('清华大学出版社', '北京市海淀区清华大学', '电话：010-62770175'),
('人民邮电出版社', '北京市西城区成方街5号', '电话：010-81055000'),
('机械工业出版社', '北京市西城区百万庄大街22号', '电话：010-88379888');

-- ======================================
-- 触发器和存储过程
-- ======================================

-- 触发器：库存更新时检查是否需要标记为缺货
DELIMITER $$
CREATE TRIGGER tr_books_stock_check 
AFTER UPDATE ON books
FOR EACH ROW
BEGIN
    -- 当库存小于10时标记为库存不足
    IF NEW.stock_quantity < 10 AND NEW.stock_quantity != OLD.stock_quantity THEN
        UPDATE books SET low_quantity_flag = 1 WHERE book_id = NEW.book_id;
        
        -- 自动创建缺书登记
        INSERT INTO missing_books (book_id, book_name, supplier_id, quantity, publisher, requested_quantity)
        VALUES (NEW.book_id, NEW.title, NEW.supplier_id, 50 - NEW.stock_quantity, NEW.publisher, 50);
    ELSEIF NEW.stock_quantity >= 10 THEN
        UPDATE books SET low_quantity_flag = 0 WHERE book_id = NEW.book_id;
    END IF;
END$$

-- 触发器：采购订单收货时更新库存
CREATE TRIGGER tr_purchase_received 
AFTER UPDATE ON purchase_orders
FOR EACH ROW
BEGIN
    IF NEW.status = 'received' AND OLD.status != 'received' THEN
        UPDATE books 
        SET stock_quantity = stock_quantity + NEW.quantity 
        WHERE book_id = NEW.book_id;
    END IF;
END$$

-- 触发器：订单确认时减少库存
CREATE TRIGGER tr_order_stock_reduce 
AFTER INSERT ON order_details
FOR EACH ROW
BEGIN
    UPDATE books 
    SET stock_quantity = stock_quantity - NEW.quantity 
    WHERE book_id = NEW.book_id;
END$$

DELIMITER ;

-- ======================================
-- 创建索引以优化查询性能
-- ======================================

-- 复合索引
CREATE INDEX idx_books_search ON books(title, author1, publisher);
CREATE INDEX idx_user_orders ON orders(user_id, order_date);
CREATE INDEX idx_order_details_composite ON order_details(order_id, book_id);

-- ======================================
-- 视图：常用查询视图
-- ======================================

-- 图书详细信息视图（包含供应商和丛书信息）
CREATE VIEW v_book_details AS
SELECT 
    b.book_id,
    b.title,
    b.author1,
    b.author2,
    b.author3,
    b.author4,
    b.publisher,
    b.price,
    b.stock_quantity,
    b.storage_location,
    s.name AS supplier_name,
    ser.series_name,
    CASE b.low_quantity_flag 
        WHEN 1 THEN '库存不足' 
        ELSE '库存充足' 
    END AS stock_status
FROM books b
LEFT JOIN suppliers s ON b.supplier_id = s.supplier_id
LEFT JOIN series ser ON b.series_id = ser.series_id;

-- 订单详情视图
CREATE VIEW v_order_details AS
SELECT 
    o.order_id,
    u.username,
    u.name AS customer_name,
    o.order_date,
    o.total_price,
    o.status AS order_status,
    od.book_id,
    b.title AS book_title,
    od.quantity,
    od.price AS unit_price,
    (od.quantity * od.price) AS subtotal
FROM orders o
JOIN users u ON o.user_id = u.user_id
JOIN order_details od ON o.order_id = od.order_id
JOIN books b ON od.book_id = b.book_id;

-- 用户信用等级视图
CREATE VIEW v_user_credit AS
SELECT 
    user_id,
    username,
    name,
    credit_level,
    CASE credit_level
        WHEN 1 THEN '一级：10%折扣，不可透支'
        WHEN 2 THEN '二级：15%折扣，不可透支'
        WHEN 3 THEN '三级：15%折扣，可透支100元'
        WHEN 4 THEN '四级：20%折扣，可透支200元'
        WHEN 5 THEN '五级：25%折扣，无透支限制'
        ELSE '未知等级'
    END AS credit_description,
    discount,
    over_balance,
    account_balance
FROM users;

-- ======================================
-- 数据库初始化完成
-- ======================================
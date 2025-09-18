# 书店管理系统数据库配置说明

## 概述
本文档详细说明了书店管理系统的MySQL数据库设计和配置方法。

## 数据库架构

### 核心表结构

| 表名 | 说明 | 主要功能 |
|------|------|----------|
| users | 用户表 | 管理用户信息、账户余额、信用等级 |
| books | 图书表 | 管理图书详细信息和库存 |
| series | 丛书系列表 | 图书分类管理 |
| suppliers | 供应商表 | 供应商信息管理 |
| supplier_books | 供应商图书关系表 | 管理供应商与图书的多对多关系 |
| orders | 订单表 | 客户订单基本信息 |
| order_details | 订单详情表 | 订单中的具体图书信息 |
| missing_books | 缺书登记表 | 记录缺书信息，生成采购需求 |
| purchase_orders | 采购订单表 | 向供应商的采购管理 |
| shipments | 发货表 | 订单发货和物流信息 |

### 核心业务逻辑

#### 1. 用户信用等级系统
- **一级**：10%折扣，不能透支
- **二级**：15%折扣，不能透支  
- **三级**：15%折扣，可透支100元
- **四级**：20%折扣，可透支200元
- **五级**：25%折扣，无透支限制

#### 2. 库存管理
- 自动监控图书库存
- 库存低于10本时自动标记并生成缺书登记
- 采购订单到货时自动更新库存

#### 3. 订单处理流程
1. 用户下单 → 检查库存
2. 库存充足 → 创建订单详情 → 扣减库存
3. 库存不足 → 提示用户并可选择缺书登记
4. 订单确认 → 发货处理 → 物流跟踪

## 安装配置

### 1. 数据库创建
```sql
-- 执行数据库创建脚本
mysql -u root -p < database/bookstore_schema.sql
```

### 2. Spring Boot配置
更新 `application.yml` 中的数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookstore?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### 3. 环境要求
- MySQL 8.0+
- Java 8+
- Spring Boot 2.x+

## 关键特性

### 自动触发器
1. **库存监控触发器**：自动检查库存并创建缺书登记
2. **采购入库触发器**：采购订单收货时自动更新库存
3. **订单扣库存触发器**：订单确认时自动减少库存

### 优化索引
- 图书搜索复合索引：title + author1 + publisher
- 用户订单索引：user_id + order_date
- 订单详情复合索引：order_id + book_id

### 实用视图
- `v_book_details`：图书详细信息视图
- `v_order_details`：订单详情视图
- `v_user_credit`：用户信用等级视图

## 初始数据
系统自动创建：
- 默认管理员账户：`admin/admin123`
- 基础图书分类
- 示例供应商信息

## 维护建议

### 定期维护任务
1. **每月信用等级更新**：根据用户消费情况调整信用等级
2. **库存盘点**：定期检查实际库存与系统记录
3. **数据备份**：建议每日备份重要数据
4. **性能监控**：监控慢查询和索引使用情况

### 扩展建议
1. **分表策略**：订单表可按年度分表
2. **读写分离**：高并发场景可考虑主从复制
3. **缓存集成**：热点图书信息可加入Redis缓存
4. **日志审计**：添加操作日志表记录关键业务操作

## 安全考虑
1. 密码使用MD5加密（建议升级为BCrypt）
2. 设置合适的数据库用户权限
3. 定期更新数据库版本
4. 监控异常登录和操作

## 故障排除

### 常见问题
1. **字符集问题**：确保数据库和表都使用utf8mb4字符集
2. **时区问题**：检查MySQL和应用时区设置
3. **连接池配置**：根据并发量调整连接池大小
4. **索引失效**：定期分析表统计信息更新索引
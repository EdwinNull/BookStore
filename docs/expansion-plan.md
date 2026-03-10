# BookStore 系统扩展计划

## 概述
本计划旨在为BookStore系统添加供应商用户体系、完善采购发货流程，并引入多项技术优化，提升系统的高并发能力和用户体验。

---

## 阶段一：供应商用户体系设计与实现

### 1.1 数据库设计

#### 1.1.1 修改现有 suppliers 表
```sql
ALTER TABLE suppliers ADD COLUMN username VARCHAR(50) UNIQUE COMMENT '供应商登录账号';
ALTER TABLE suppliers ADD COLUMN password VARCHAR(255) COMMENT '密码（加密存储）';
ALTER TABLE suppliers ADD COLUMN contact_person VARCHAR(50) COMMENT '联系人';
ALTER TABLE suppliers ADD COLUMN phone VARCHAR(20) COMMENT '联系电话';
ALTER TABLE suppliers ADD COLUMN email VARCHAR(100) COMMENT '邮箱';
ALTER TABLE suppliers ADD COLUMN status ENUM('active', 'inactive', 'pending') DEFAULT 'pending' COMMENT '状态';
ALTER TABLE suppliers ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE suppliers ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
```

#### 1.1.2 新增 supplier_users 表（可选，如果需要独立的供应商用户体系）
```sql
CREATE TABLE supplier_users (
    supplier_user_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id INT NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50),
    role ENUM('admin', 'staff') DEFAULT 'staff',
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);
```

### 1.2 后端实现

#### 1.2.1 实体类修改
- **Suppliers.java**: 添加用户相关字段（username, password, contactPerson, phone, email, status）

#### 1.2.2 新增DTO
- `SupplierLoginRequest.java` - 供应商登录请求
- `SupplierRegisterRequest.java` - 供应商注册请求
- `SupplierProfileResponse.java` - 供应商信息响应

#### 1.2.3 新增Service
- `SupplierAuthService.java` - 供应商认证服务
  - 登录验证
  - 密码加密
  - JWT令牌生成

#### 1.2.4 新增Controller
- `SupplierAuthController.java`
  - `POST /api/supplier/login` - 供应商登录
  - `POST /api/supplier/register` - 供应商注册（需管理员审核）
  - `GET /api/supplier/profile` - 获取供应商信息
  - `PUT /api/supplier/profile` - 更新供应商信息

#### 1.2.5 拦截器修改
- 扩展 `LoginInterceptor` 支持供应商角色验证
- 添加 `SupplierInterceptor` 专门处理供应商请求

### 1.3 前端实现

#### 1.3.1 新增页面
- `pages/supplier/Login.tsx` - 供应商登录页
- `pages/supplier/Dashboard.tsx` - 供应商控制台
- `pages/supplier/Profile.tsx` - 供应商信息管理

#### 1.3.2 新增组件
- `components/layout/SupplierLayout.tsx` - 供应商后台布局

#### 1.3.3 新增API
- `api/supplier.ts` - 供应商相关API

#### 1.3.4 路由配置
- 添加供应商路由 `/supplier/*`

### 1.4 功能清单
- [ ] 供应商注册申请
- [ ] 管理员审核供应商
- [ ] 供应商登录/登出
- [ ] 供应商个人信息管理
- [ ] 供应商角色权限控制

---

## 阶段二：完善采购、发货流程

### 2.1 采购流程优化

#### 2.1.1 数据库设计
```sql
-- 采购订单状态流转记录
CREATE TABLE purchase_order_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    purchase_order_id INT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    operator_id INT COMMENT '操作人ID',
    operator_type ENUM('admin', 'supplier') NOT NULL,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(purchase_order_id)
);

-- 采购订单明细（支持批量采购）
ALTER TABLE purchase_orders ADD COLUMN total_amount DECIMAL(10,2) COMMENT '采购总金额';
ALTER TABLE purchase_orders ADD COLUMN expected_delivery_date DATE COMMENT '预计到货日期';
ALTER TABLE purchase_orders ADD COLUMN actual_delivery_date DATE COMMENT '实际到货日期';
```

#### 2.1.2 后端实现

**PurchaseController 增强**:
- `POST /api/purchase/create` - 创建采购订单（管理员操作）
- `POST /api/purchase/confirm` - 供应商确认接单
- `POST /api/purchase/ship` - 供应商发货
- `POST /api/purchase/receive` - 管理员确认收货
- `GET /api/purchase/supplier/list` - 供应商查看自己的采购订单

**PurchaseService 增强**:
- 采购订单状态流转验证
- 采购入库自动更新库存
- 发送通知（供应商/管理员）

#### 2.1.3 前端实现
- `pages/admin/PurchaseManagement.tsx` - 增强采购管理页面
- `pages/supplier/Orders.tsx` - 供应商订单管理页面
- 采购订单详情弹窗
- 状态流转可视化

### 2.2 发货流程优化

#### 2.2.1 数据库设计
```sql
-- 物流信息表增强
ALTER TABLE ship ADD COLUMN carrier VARCHAR(50) COMMENT '快递公司';
ALTER TABLE ship ADD COLUMN estimated_delivery DATE COMMENT '预计送达时间';
ALTER TABLE ship ADD COLUMN actual_delivery DATETIME COMMENT '实际送达时间';
ALTER TABLE ship ADD COLUMN shipping_fee DECIMAL(10,2) COMMENT '运费';

-- 物流轨迹表
CREATE TABLE shipping_tracks (
    track_id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL,
    location VARCHAR(100) COMMENT '当前位置',
    status VARCHAR(50) COMMENT '物流状态',
    description TEXT COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shipment_id) REFERENCES ship(shipment_id)
);
```

#### 2.2.2 后端实现

**ShipController 增强**:
- `POST /api/order/ship` - 订单发货
- `POST /api/order/track` - 添加物流轨迹
- `GET /api/order/track/{orderId}` - 查询物流信息
- `POST /api/order/confirm-receive` - 用户确认收货

**ShipService 增强**:
- 物流信息CRUD
- 自动更新订单状态
- 物流通知

#### 2.2.3 前端实现
- `pages/user/OrderTracking.tsx` - 订单物流追踪页面
- 物流轨迹时间线组件
- 发货操作弹窗

### 2.3 功能清单
- [ ] 采购订单创建（指定供应商）
- [ ] 供应商确认接单
- [ ] 供应商发货操作
- [ ] 管理员确认收货入库
- [ ] 用户订单发货
- [ ] 物流信息追踪
- [ ] 用户确认收货

---

## 阶段三：折扣计算优化

### 3.1 数据库设计

```sql
-- 用户表添加已消费金额字段
ALTER TABLE users ADD COLUMN total_spent DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计消费金额';
ALTER TABLE users ADD COLUMN discount_updated_at DATETIME COMMENT '折扣更新时间';

-- 折扣规则表
CREATE TABLE discount_rules (
    rule_id INT PRIMARY KEY AUTO_INCREMENT,
    min_spent DECIMAL(10,2) NOT NULL COMMENT '最低消费金额',
    max_spent DECIMAL(10,2) COMMENT '最高消费金额',
    discount_rate DECIMAL(3,2) NOT NULL COMMENT '折扣率（如0.95表示95折）',
    level_name VARCHAR(20) NOT NULL COMMENT '等级名称',
    description VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 初始化折扣规则
INSERT INTO discount_rules (min_spent, max_spent, discount_rate, level_name, description) VALUES
(0, 99.99, 1.00, '普通会员', '消费满0元'),
(100, 499.99, 0.98, '铜牌会员', '消费满100元，享98折'),
(500, 999.99, 0.95, '银牌会员', '消费满500元，享95折'),
(1000, 4999.99, 0.90, '金牌会员', '消费满1000元，享9折'),
(5000, NULL, 0.85, '钻石会员', '消费满5000元，享85折');
```

### 3.2 后端实现

#### 3.2.1 新增实体
- `DiscountRule.java` - 折扣规则实体

#### 3.2.2 新增Service
- `DiscountService.java` - 折扣计算服务
  - `calculateDiscount(BigDecimal totalSpent)` - 根据消费金额计算折扣
  - `updateUserDiscount(Long userId)` - 更新用户折扣
  - `getUserLevel(BigDecimal totalSpent)` - 获取用户等级
  - `getDiscountRules()` - 获取所有折扣规则

#### 3.2.3 修改订单服务
- 订单完成后累加 `total_spent`
- 根据累计消费金额更新用户折扣率
- 折扣更新可配置为定时任务（非实时更新）

#### 3.2.4 新增Controller
- `DiscountController.java`
  - `GET /api/discount/rules` - 获取折扣规则
  - `GET /api/discount/my-level` - 获取当前用户等级信息

### 3.3 前端实现
- 用户等级展示组件
- 折扣规则说明页面
- 个人中心显示累计消费和当前等级

### 3.4 功能清单
- [ ] 累计消费金额统计
- [ ] 基于消费金额的折扣计算
- [ ] 会员等级体系
- [ ] 折扣规则管理（管理员）

---

## 阶段四：图书图片加载 - 阿里云OSS集成

### 4.1 技术选型
- **主要方案**: 阿里云OSS（稳定、功能丰富）
- **备选方案**: MinIO（私有化部署、兼容S3协议）

### 4.2 数据库设计

```sql
-- 图书表添加图片字段
ALTER TABLE books ADD COLUMN cover_image VARCHAR(255) COMMENT '封面图片URL';
ALTER TABLE books ADD COLUMN images TEXT COMMENT '其他图片URL（JSON数组）';
```

### 4.3 后端实现

#### 4.3.1 添加依赖
```xml
<!-- 阿里云OSS SDK -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
```

#### 4.3.2 配置文件
```yaml
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: ${ALIYUN_ACCESS_KEY_ID}
    access-key-secret: ${ALIYUN_ACCESS_KEY_SECRET}
    bucket-name: bookstore-images
    base-url: https://bookstore-images.oss-cn-hangzhou.aliyuncs.com
```

#### 4.3.3 新增Service
- `OssService.java` - OSS文件上传服务
  - `uploadImage(MultipartFile file, String folder)` - 上传图片
  - `deleteImage(String fileUrl)` - 删除图片
  - `generatePresignedUrl(String objectKey, int expireSeconds)` - 生成签名URL

#### 4.3.4 新增Controller
- `FileController.java`
  - `POST /api/file/upload` - 上传图片
  - `DELETE /api/file/delete` - 删除图片

### 4.4 前端实现
- 图片上传组件（支持拖拽、预览）
- 图片裁剪功能
- 上传进度显示
- 图片管理界面（图书编辑时）

### 4.5 功能清单
- [ ] 阿里云OSS配置
- [ ] 图片上传接口
- [ ] 图片删除接口
- [ ] 前端图片上传组件
- [ ] 图书封面管理

---

## 阶段五：Redis深度使用 - 高并发优化与分布式锁

### 5.1 缓存策略设计

#### 5.1.1 缓存场景
1. **图书信息缓存** - 热门图书信息缓存
2. **用户Session缓存** - 登录状态缓存
3. **库存缓存** - 热门商品库存缓存
4. **购物车缓存** - 购物车数据缓存
5. **验证码缓存** - 短信/邮箱验证码

#### 5.1.2 缓存更新策略
- **Cache-Aside Pattern**: 先查缓存，miss则查数据库并回写
- **Write-Through**: 数据更新时同步更新缓存
- **Lazy Loading**: 按需加载，设置合理过期时间

### 5.2 分布式锁实现

#### 5.2.1 应用场景
1. **库存扣减** - 防止超卖
2. **订单创建** - 防止重复下单
3. **秒杀活动** - 高并发抢购

#### 5.2.2 后端实现

**添加依赖**:
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.27.0</version>
</dependency>
```

**Redisson配置**:
```java
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
              .setAddress("redis://localhost:6379")
              .setConnectionPoolSize(10)
              .setConnectionMinimumIdleSize(5);
        return Redisson.create(config);
    }
}
```

**分布式锁服务**:
```java
@Service
public class DistributedLockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 使用分布式锁执行操作
     * @param lockKey 锁的key
     * @param waitTime 等待时间（秒）
     * @param leaseTime 持有时间（秒）
     * @param task 要执行的任务
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                return task.get();
            } else {
                throw new RuntimeException("获取锁超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

#### 5.2.3 库存扣减优化
```java
@Service
public class StockService {

    @Autowired
    private DistributedLockService lockService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 扣减库存（带分布式锁）
     */
    @Transactional
    public boolean deductStock(Long bookId, int quantity) {
        String lockKey = "lock:stock:" + bookId;
        return lockService.executeWithLock(lockKey, 5, 10, () -> {
            // 1. 先查Redis缓存库存
            String cacheKey = "stock:" + bookId;
            Integer cachedStock = (Integer) redisTemplate.opsForValue().get(cacheKey);

            // 2. 缓存miss则查数据库
            if (cachedStock == null) {
                Books book = bookMapper.selectById(bookId);
                cachedStock = book.getStockQuantity();
                redisTemplate.opsForValue().set(cacheKey, cachedStock, 30, TimeUnit.MINUTES);
            }

            // 3. 检查库存
            if (cachedStock < quantity) {
                return false;
            }

            // 4. 扣减数据库库存
            int updated = bookMapper.deductStock(bookId, quantity);
            if (updated > 0) {
                // 5. 更新缓存
                redisTemplate.opsForValue().decrement(cacheKey, quantity);
                return true;
            }
            return false;
        });
    }
}
```

### 5.3 缓存穿透/击穿/雪崩防护

#### 5.3.1 缓存穿透防护
- 布隆过滤器（Bloom Filter）
- 空值缓存

#### 5.3.2 缓存击穿防护
- 热点数据永不过期
- 分布式锁重建缓存

#### 5.3.3 缓存雪崩防护
- 过期时间随机化
- 多级缓存（本地缓存 + Redis）

### 5.4 后端实现清单
- [ ] Redisson集成
- [ ] 分布式锁服务
- [ ] 图书信息缓存
- [ ] 库存缓存与扣减优化
- [ ] 购物车Redis存储
- [ ] 布隆过滤器实现

---

## 阶段六：消息中间件集成 - RabbitMQ

### 6.1 技术选型
- **消息队列**: RabbitMQ（轻量级、易用、支持延迟队列）
- **备选**: Kafka（大数据量场景）、RocketMQ（阿里生态）

### 6.2 添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 6.3 配置文件

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    listener:
      simple:
        acknowledge-mode: manual  # 手动确认
        prefetch: 1  # 每次只取一条消息
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          multiplier: 2
```

### 6.4 消息场景设计

#### 6.4.1 订单相关消息
| 队列名 | 交换机 | 路由键 | 用途 |
|--------|--------|--------|------|
| order.created | order.exchange | order.created | 订单创建通知 |
| order.paid | order.exchange | order.paid | 订单支付成功 |
| order.shipped | order.exchange | order.shipped | 订单发货通知 |
| order.cancelled | order.exchange | order.cancelled | 订单取消（库存回滚）|
| order.timeout | order.exchange | order.timeout | 订单超时取消（延迟队列）|

#### 6.4.2 采购相关消息
| 队列名 | 交换机 | 路由键 | 用途 |
|--------|--------|--------|------|
| purchase.created | purchase.exchange | purchase.created | 采购订单创建（通知供应商）|
| purchase.confirmed | purchase.exchange | purchase.confirmed | 供应商确认接单 |
| purchase.shipped | purchase.exchange | purchase.shipped | 供应商发货通知 |

#### 6.4.3 库存相关消息
| 队列名 | 交换机 | 路由键 | 用途 |
|--------|--------|--------|------|
| stock.low | stock.exchange | stock.low | 库存预警 |
| stock.updated | stock.exchange | stock.updated | 库存更新通知 |

### 6.5 后端实现

#### 6.5.1 消息配置类
```java
@Configuration
public class RabbitMQConfig {

    // 订单交换机
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CREATED_QUEUE = "order.created";
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout";

    // 延迟队列配置（订单超时取消）
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(ORDER_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "order.cancel")
                .build();
    }
}
```

#### 6.5.2 消息生产者
```java
@Service
public class OrderMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送订单创建消息
     */
    public void sendOrderCreatedMessage(Long orderId) {
        OrderMessage message = new OrderMessage(orderId, LocalDateTime.now());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            "order.created",
            message
        );
    }

    /**
     * 发送订单超时延迟消息（30分钟后执行）
     */
    public void sendOrderTimeoutMessage(Long orderId) {
        OrderMessage message = new OrderMessage(orderId, LocalDateTime.now());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            "order.timeout",
            message,
            msg -> {
                msg.getMessageProperties().setDelay(30 * 60 * 1000); // 30分钟
                return msg;
            }
        );
    }
}
```

#### 6.5.3 消息消费者
```java
@Component
@Slf4j
public class OrderMessageConsumer {

    @Autowired
    private OrderService orderService;

    /**
     * 处理订单超时取消
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_TIMEOUT_QUEUE)
    public void handleOrderTimeout(OrderMessage message, Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("处理订单超时: orderId={}", message.getOrderId());
            orderService.cancelTimeoutOrder(message.getOrderId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("订单超时处理失败", e);
            channel.basicNack(tag, false, true);
        }
    }
}
```

### 6.6 Docker配置更新

```yaml
# docker-compose.yml 添加
rabbitmq:
  image: rabbitmq:3.12-management
  container_name: bookstore-rabbitmq
  ports:
    - "5672:5672"
    - "15672:15672"  # 管理界面
  environment:
    RABBITMQ_DEFAULT_USER: admin
    RABBITMQ_DEFAULT_PASS: admin123
  volumes:
    - rabbitmq_data:/var/lib/rabbitmq
  networks:
    - bookstore-network

volumes:
  rabbitmq_data:
```

### 6.7 功能清单
- [ ] RabbitMQ配置与集成
- [ ] 订单创建消息
- [ ] 订单超时自动取消（延迟队列）
- [ ] 库存预警消息
- [ ] 采购通知消息
- [ ] 邮件/短信通知（消费消息后发送）

---

## 实施时间线

| 阶段 | 预计工作量 | 优先级 |
|------|------------|--------|
| 阶段一：供应商用户体系 | 中 | 高 |
| 阶段二：采购发货流程 | 中 | 高 |
| 阶段三：折扣计算优化 | 低 | 中 |
| 阶段四：OSS图片存储 | 低 | 中 |
| 阶段五：Redis深度优化 | 高 | 高 |
| 阶段六：RabbitMQ集成 | 高 | 高 |

---

## 技术风险与缓解措施

### 风险1：分布式锁死锁
- **缓解**: 使用Redisson的看门狗机制，自动续期
- **缓解**: 设置合理的锁超时时间

### 风险2：消息队列消息丢失
- **缓解**: 开启消息持久化
- **缓解**: 生产者确认机制
- **缓解**: 消费者手动ACK

### 风险3：缓存与数据库不一致
- **缓解**: 采用延时双删策略
- **缓解**: 使用消息队列保证最终一致性

---

## 附录：数据库变更汇总

### 新增表
1. `supplier_users` - 供应商用户表
2. `purchase_order_logs` - 采购订单日志
3. `shipping_tracks` - 物流轨迹表
4. `discount_rules` - 折扣规则表

### 修改表
1. `suppliers` - 添加用户认证字段
2. `purchase_orders` - 添加发货日期字段
3. `ship` - 添加运费、快递公司字段
4. `users` - 添加累计消费金额字段
5. `books` - 添加图片URL字段

---

*计划创建时间: 2026-03-10*
*计划版本: v1.0*

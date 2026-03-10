package com.weblearning.bookstore.servcie.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.weblearning.bookstore.DTO.OrderDetailResponse;
import com.weblearning.bookstore.DTO.OrderItemResponse;
import com.weblearning.bookstore.mapper.BookMapper;
import com.weblearning.bookstore.mapper.OrderMapper;
import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.*;
import com.weblearning.bookstore.servcie.DiscountService;
import com.weblearning.bookstore.servcie.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private DiscountService discountService;

    @Override
    public void addOrder(OrderRequest order) {
        Integer userId = order.getUserId();
        User user = userMapper.findById(userId);
        //用户相关信息
        Double discount = user.getDiscount();
        Double overBalance = user.getOverBalance();
        Double accountBalance = user.getAccountBalance();
        //订单相关信息
        Double totalPrice = 0.00;
        for(OrderDetails item: order.getItems()){
            Integer bookId = item.getBookId();
            Books book = bookMapper.findById(bookId);
            Integer stockQuantity = book.getStockQuantity();
            if(stockQuantity < item.getQuantity()){
                throw new RuntimeException("库存不足");
            }
            book.setStockQuantity(stockQuantity - item.getQuantity());
            bookMapper.updateQuantity(book.getStockQuantity(),bookId);
            totalPrice += item.getPrice() * item.getQuantity();
        }
        Double finalPrice = totalPrice * (1 - discount);
        if(finalPrice > accountBalance + overBalance){
            throw new RuntimeException("余额不足");
        }
        Double n = accountBalance - finalPrice;
        if(n< 0 && overBalance != 0){
            user.setOverBalance(finalPrice - accountBalance);
            user.setAccountBalance(0.00);
            userMapper.updateCreditLevel(userId,1,0.10);
        }
        else{
            userMapper.addBalance(userId,-finalPrice);
            if(n < 500){
                userMapper.updateCreditLevel(userId,1,0.10);
            }
            if(n >= 500 && n < 1000){
                userMapper.updateCreditLevel(userId,2,0.15);
            }
            if(n >= 1000 && n < 2000){
                userMapper.updateCreditLevel(userId,3,0.15);
                userMapper.updateOverBalance(userId,100.00);
            }
            if(n >= 2000 && n < 5000){
                userMapper.updateCreditLevel(userId,4,0.20);
                userMapper.updateOverBalance(userId,200.00);
            }
            if(n >= 5000){
                userMapper.updateCreditLevel(userId,5,0.25);
                userMapper.updateOverBalance(userId,500.00);
            }
        }
        // 订单状态使用数据库ENUM定义的值: pending, confirmed, processing, shipped, delivered, cancelled
        // "待发货" 对应 "confirmed"（已确认，待发货）
        String status = "confirmed";
        orderMapper.addOrder(userId,finalPrice,LocalDateTime.now(),status);
        Integer orderId = orderMapper.getLastInsertId();
        for(OrderDetails item: order.getItems()){
            orderMapper.addOrderDetails(orderId,item.getBookId(),item.getQuantity(),item.getPrice());
        }
    }

    @Override
    public Order getOrder(Integer orderId) {
        Order order = orderMapper.findById(orderId);
        return order;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Integer orderId) {
        OrderDetailResponse orderDetails = orderMapper.getById(orderId);
        List<OrderItemResponse> items = orderMapper.getItemsByOrderId(orderId);
        orderDetails.setItems(items);
        return orderDetails;
    }

    /**
     * 获取用户的订单列表（分页）
     */
    @Override
    public PageBean<Order> getUserOrderList(Integer userId, Integer pageNum, Integer pageSize) {
        // 创建PageBean对象
        PageBean<Order> pageBean = new PageBean<>();

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询用户的订单列表
        List<Order> orders = orderMapper.findByUserId(userId);

        // 获取分页信息
        Page<Order> page = (Page<Order>) orders;

        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());

        return pageBean;
    }

    /**
     * 获取所有订单列表（管理员用，分页）
     */
    @Override
    public PageBean<Order> getAllOrderList(Integer pageNum, Integer pageSize, String status) {
        // 创建PageBean对象
        PageBean<Order> pageBean = new PageBean<>();

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询订单列表
        List<Order> orders = orderMapper.findAllOrders(status);

        // 获取分页信息
        Page<Order> page = (Page<Order>) orders;

        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());

        return pageBean;
    }

    /**
     * 用户确认收货
     * 确认收货后，将订单状态更新为delivered，并累加用户消费金额
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     */
    @Override
    @Transactional
    public void confirmReceive(Integer orderId, Integer userId) {
        // 1. 获取订单信息
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 验证订单归属（只能确认自己的订单）
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        // 3. 验证订单状态（只有已发货的订单才能确认收货）
        String currentStatus = order.getStatus();
        if (!"shipped".equals(currentStatus)) {
            throw new RuntimeException("订单状态不允许确认收货，当前状态：" + currentStatus);
        }

        // 4. 更新订单状态为已送达
        orderMapper.updateStatus(orderId, "delivered");

        // 5. 累加用户消费金额并更新折扣率
        BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalPrice());
        discountService.addTotalSpent(userId, orderAmount);

        logger.info("用户确认收货成功：orderId={}, userId={}, amount={}", orderId, userId, orderAmount);
    }
}

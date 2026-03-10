package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Order;
import com.weblearning.bookstore.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    User findByUserName(String username);

    void add(String username, String encodePassword);

    void update(User user, Integer userId);

    void updatePwd(String encodePassword, Integer userId);

    void addBalance(Integer userId, Double balance);

    User findById(Integer userId);

    void updateCreditLevel(Integer userId, int level, double discount);

    /**
     * 只更新用户信用等级（不更新折扣）
     * 折扣率只与累计消费金额相关，由 DiscountService 管理
     * @param userId 用户ID
     * @param level 信用等级
     */
    void updateCreditLevelOnly(@Param("userId") Integer userId, @Param("level") int level);

    void updateOverBalance(Integer userId, double overBalance);

    Order findOrderByUserId(Integer userId);

    // 获取用户列表（带条件搜索）
    List<User> getUserList(@Param("username") String username, @Param("role") String role);

    // 获取所有用户
    List<User> getAllUsers();

    // ==================== 阶段三：累计消费金额相关 ====================

    /**
     * 更新用户累计消费金额
     * @param userId 用户ID
     * @param totalSpent 累计消费金额
     */
    void updateTotalSpent(@Param("userId") Integer userId, @Param("totalSpent") BigDecimal totalSpent);

    /**
     * 更新用户折扣率
     * @param userId 用户ID
     * @param discount 折扣率
     */
    void updateDiscount(@Param("userId") Integer userId, @Param("discount") Double discount);

    /**
     * 更新折扣更新时间
     * @param userId 用户ID
     * @param discountUpdatedAt 更新时间
     */
    void updateDiscountUpdatedAt(@Param("userId") Integer userId, @Param("discountUpdatedAt") LocalDateTime discountUpdatedAt);
}

package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.DTO.DiscountRuleResponse;
import com.weblearning.bookstore.DTO.UserLevelResponse;
import com.weblearning.bookstore.mapper.UserMapper;
import com.weblearning.bookstore.pojo.DiscountRule;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.User;
import com.weblearning.bookstore.servcie.DiscountService;
import com.weblearning.bookstore.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 折扣控制器
 * 提供折扣规则查询、用户等级信息查询等接口
 */
@RestController
@RequestMapping("/api/discount")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有启用的折扣规则
     * 用于前端展示会员等级体系
     * @return 折扣规则列表
     */
    @GetMapping("/rules")
    public Result<List<DiscountRuleResponse>> getDiscountRules() {
        List<DiscountRule> rules = discountService.getActiveDiscountRules();
        List<DiscountRuleResponse> responseList = rules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responseList);
    }

    /**
     * 获取当前用户的等级信息
     * 包括累计消费、当前等级、折扣率、距离下一等级还需消费等
     * @param request HTTP请求（用于获取用户信息）
     * @return 用户等级信息
     */
    @GetMapping("/my-level")
    public Result<UserLevelResponse> getMyLevel(HttpServletRequest request) {
        // 从请求中获取用户ID
        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 获取用户等级信息
        UserLevelResponse levelInfo = getUserLevelInfo(userId);
        return Result.success(levelInfo);
    }

    /**
     * 从请求中获取用户ID
     * @param request HTTP请求
     * @return 用户ID
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        // 优先从request attribute获取（由拦截器设置）
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) {
            return userId;
        }

        // 从token中获取
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Map<String, Object> claims = JwtUtil.parseToken(token);
                Object userIdObj = claims.get("userId");
                if (userIdObj != null) {
                    return ((Number) userIdObj).intValue();
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * 获取用户等级信息
     * @param userId 用户ID
     * @return 用户等级信息响应
     */
    private UserLevelResponse getUserLevelInfo(Integer userId) {
        UserLevelResponse response = new UserLevelResponse();
        response.setUserId(userId);

        // 获取用户的累计消费金额
        User user = userMapper.findById(userId);
        BigDecimal totalSpent = BigDecimal.ZERO;
        String username = "";
        if (user != null) {
            username = user.getUsername();
            if (user.getTotalSpent() != null) {
                totalSpent = user.getTotalSpent();
            }
        }
        response.setUsername(username);

        // 获取当前等级
        DiscountRule currentRule = discountService.getDiscountRule(totalSpent);
        response.setTotalSpent(totalSpent);
        response.setLevelName(currentRule.getLevelName());
        response.setDiscountRate(currentRule.getDiscountRate());

        // 计算距离下一等级还需消费金额
        List<DiscountRule> allRules = discountService.getActiveDiscountRules();
        BigDecimal nextLevelNeeded = null;
        String nextLevelName = null;

        for (DiscountRule rule : allRules) {
            // 规则按minSpent降序排列，找到第一个比当前消费高的规则
            if (rule.getMinSpent().compareTo(totalSpent) > 0) {
                nextLevelNeeded = rule.getMinSpent().subtract(totalSpent);
                nextLevelName = rule.getLevelName();
                break;
            }
        }

        response.setNextLevelNeeded(nextLevelNeeded);
        response.setNextLevelName(nextLevelName);

        return response;
    }

    /**
     * 将DiscountRule实体转换为响应DTO
     * @param rule 折扣规则实体
     * @return 响应DTO
     */
    private DiscountRuleResponse convertToResponse(DiscountRule rule) {
        DiscountRuleResponse response = new DiscountRuleResponse();
        BeanUtils.copyProperties(rule, response);
        return response;
    }
}

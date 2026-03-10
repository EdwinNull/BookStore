package com.weblearning.bookstore.interceptors;

import com.weblearning.bookstore.utils.JwtUtil;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 供应商登录拦截器
 * 用于验证供应商身份，确保只有已登录的供应商才能访问相关接口
 *
 * 与 LoginInterceptor 的区别：
 * 1. 使用 "supplier_token:" 前缀在Redis中存储token
 * 2. 验证token中的role是否为 "supplier"
 */
@Component
public class SupplierInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public SupplierInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 对于OPTIONS预检请求，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        // 检查token格式
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供认证令牌\"}");
            return false;
        }

        try {
            // 从Redis获取供应商token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get("supplier_token:" + token);

            if (redisToken == null) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"登录已过期，请重新登录\"}");
                return false;
            }

            // 解析token获取claims
            Map<String, Object> claims = JwtUtil.parseToken(token);

            // 验证是否为供应商角色
            String role = (String) claims.get("role");
            if (!"supplier".equals(role)) {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权访问供应商接口\"}");
                return false;
            }

            // 将用户信息存入ThreadLocal，供后续使用
            ThreadLocalUtil.set(claims);
            return true;

        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal，防止内存泄漏
        ThreadLocalUtil.remove();
    }
}

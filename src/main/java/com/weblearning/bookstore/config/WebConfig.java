package com.weblearning.bookstore.config;

import com.weblearning.bookstore.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置登录拦截器，用于验证用户身份
 *
 * 注意：@Profile("!test") 表示在非 test 环境下生效
 * 测试时通过 -Dspring.profiles.active=test 或 @ActiveProfiles("test") 来禁用拦截器
 */
@Configuration
@Profile("!test")  // 非 test 环境下才启用拦截器
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器配置
        // addPathPatterns("/**") - 拦截所有请求
        // excludePathPatterns - 排除不需要登录验证的路径
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/api/login",          // 用户登录
                        "/api/register",       // 用户注册
                        "/api/admin/login",    // 管理员登录
                        "/api/books",          // 图书列表（GET，允许匿名访问）
                        "/api/books/*",        // 图书详情（GET，允许匿名访问，匹配 /api/books/{id}）
                        "/api/books/findBook", // 图书搜索（GET，允许匿名访问）
                        // Swagger 相关路径 - 允许匿名访问 API 文档
                        "/swagger-ui/**",      // Swagger UI 页面及资源
                        "/swagger-ui.html",    // Swagger UI 入口
                        "/v3/api-docs/**",     // OpenAPI 3 文档接口
                        "/v3/api-docs.yaml",   // YAML 格式文档
                        "/webjars/**"          // Swagger UI 静态资源
                );
    }
}

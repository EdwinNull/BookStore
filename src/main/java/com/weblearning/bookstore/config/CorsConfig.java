package com.weblearning.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS（跨域资源共享）配置类
 *
 * 功能说明：
 * 1. 允许来自 http://localhost:5173 的前端请求
 * 2. 支持携带 Authorization Header 进行身份验证
 * 3. 支持所有 HTTP 方法（GET、POST、PUT、DELETE、OPTIONS 等）
 * 4. 正确处理 OPTIONS 预检请求
 *
 * 注意：此配置仅在开发环境使用，生产环境应通过 Nginx 处理 CORS
 */
@Configuration
public class CorsConfig {

    /**
     * 创建 CORS 过滤器 Bean
     *
     * @return CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ============================================
        // 允许的来源（Origin）
        // ============================================
        // 仅允许来自 http://localhost:5173 的请求（前端开发服务器地址）
        // 注意：allowCredentials 为 true 时不能使用 "*"
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",    // Vite 开发服务器
                "http://127.0.0.1:5173",    // 备用地址
                "http://localhost:80",      // Nginx 生产环境
                "http://127.0.0.1:80"       // Nginx 备用地址
        ));

        // ============================================
        // 允许的 HTTP 方法
        // ============================================
        // 支持所有常用的 HTTP 方法
        config.setAllowedMethods(Arrays.asList(
                "GET",      // 查询
                "POST",     // 创建
                "PUT",      // 更新
                "DELETE",   // 删除
                "PATCH",    // 部分更新
                "OPTIONS"   // 预检请求
        ));

        // ============================================
        // 允许的请求头
        // ============================================
        // 允许所有请求头，包括 Authorization
        // 也可以明确指定：config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.addAllowedHeader("*");

        // ============================================
        // 允许携带凭证（Cookies、Authorization Header）
        // ============================================
        // 必须设置为 true 才能携带 Authorization Header
        // 注意：设置为 true 时，allowedOrigins 不能使用 "*"
        config.setAllowCredentials(true);

        // ============================================
        // 暴露的响应头
        // ============================================
        // 允许前端访问的响应头
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // ============================================
        // 预检请求缓存时间
        // ============================================
        // OPTIONS 预检请求的结果缓存 3600 秒（1小时）
        // 减少预检请求的发送频率，提高性能
        config.setMaxAge(3600L);

        // ============================================
        // 注册 CORS 配置到所有路径
        // ============================================
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    /**
     * 可选：为 Spring Security 提供 CORS 配置源
     * 如果项目使用了 Spring Security，需要此 Bean
     *
     * @return CorsConfigurationSource 实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:80",
                "http://127.0.0.1:80"
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

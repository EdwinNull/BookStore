# =====================================================
# 后端 Dockerfile - Spring Boot 应用
# 多阶段构建：先编译，再运行
# =====================================================

# ============================================
# 第一阶段：构建阶段 (Build Stage)
# 使用 Maven 编译 Spring Boot 应用
# ============================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# 设置工作目录
WORKDIR /app

# 先复制 pom.xml，利用 Docker 缓存加速依赖下载
COPY pom.xml .

# 下载依赖（利用缓存，依赖不变时无需重新下载）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 编译打包，跳过测试
RUN mvn clean package -DskipTests -B

# ============================================
# 第二阶段：运行阶段 (Runtime Stage)
# 使用轻量级 JRE 镜像运行应用
# ============================================
FROM eclipse-temurin:17-jre-alpine AS production

# 安装必要工具
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 jar 文件
COPY --from=builder /app/target/*.jar app.jar

# 设置文件权限
RUN chown -R appuser:appgroup /app

# 切换到非 root 用户
USER appuser

# 暴露端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

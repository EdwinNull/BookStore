# =====================================================
# 前端 Dockerfile - 多阶段构建
# 第一阶段：使用 Node.js 编译打包前端应用
# 第二阶段：将打包产物放入 Nginx 镜像
# =====================================================

# ============================================
# 第一阶段：构建阶段 (Build Stage)
# 使用 Node.js 镜像编译前端代码
# ============================================
FROM node:20-alpine AS builder

# 设置工作目录
WORKDIR /app

# 设置 npm 镜像源（国内可加速）
# RUN npm config set registry https://registry.npmmirror.com

# 先复制 package.json 和 package-lock.json
# 利用 Docker 缓存机制，依赖不变时无需重新安装
COPY package*.json ./

# 安装依赖
RUN npm ci --legacy-peer-deps

# 复制源代码
COPY . .

# 设置构建时环境变量（可在 docker-compose 中覆盖）
ARG VITE_API_URL=/api
ENV VITE_API_URL=$VITE_API_URL

# 构建生产版本
RUN npm run build

# ============================================
# 第二阶段：生产阶段 (Production Stage)
# 使用轻量级 Nginx 镜像部署静态文件
# ============================================
FROM nginx:alpine AS production

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 删除 Nginx 默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 从构建阶段复制打包产物到 Nginx 静态资源目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 设置文件权限
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# 创建日志目录
RUN mkdir -p /var/log/nginx && \
    chown -R nginx:nginx /var/log/nginx

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# 启动 Nginx（前台运行）
CMD ["nginx", "-g", "daemon off;"]

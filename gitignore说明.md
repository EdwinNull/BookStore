# 📝 Gitignore 管理说明

## 📋 概述

本项目采用了分层的 .gitignore 管理策略，确保不同类型的文件不会被意外提交到版本控制中。

## 🏗️ Gitignore 文件结构

### 1. 根目录 .gitignore
**文件位置**: `BookStore/.gitignore`
**用途**: 管理整个项目的通用忽略规则，包括：
- Maven 构建文件 (`target/`)
- IDE 配置文件 (`.idea/`, `.vscode/`)
- 操作系统生成的文件 (`.DS_Store`, `Thumbs.db`)
- 前端构建产物 (`frontend/dist/`, `frontend/build/`)
- 日志文件 (`*.log`)
- 临时文件 (`tmp/`, `temp/`)

### 2. 前端专用 .gitignore
**文件位置**: `BookStore/frontend/.gitignore`
**用途**: 专门管理前端项目的忽略规则，包括：
- Node.js 依赖 (`node_modules/`)
- 构建输出 (`dist/`, `build/`)
- 环境变量文件 (`.env*`)
- 缓存文件 (`.cache/`, `.vite/`)
- 测试覆盖率 (`coverage/`)
- Vue 自动生成文件 (`auto-imports.d.ts`, `components.d.ts`)

### 3. 全局参考 .gitignore
**文件位置**: `BookStore/.gitignore_global`
**用途**: 提供全局忽略规则的参考，不实际生效但作为参考文档。

## 🎯 忽略规则详解

### 🔴 必须忽略的文件

#### 敏感信息
```gitignore
# 密钥和证书
*.key
*.pem
*.p12
*.keystore

# 环境配置
.env
.env.local
application-dev.yml
application-prod.yml
```

#### 构建产物
```gitignore
# Maven
target/
build/

# 前端
frontend/dist/
frontend/build/
node_modules/
```

#### IDE 文件
```gitignore
# IntelliJ IDEA
.idea/
*.iml
*.iws

# VS Code
.vscode/

# Eclipse
.settings/
.classpath
.project
```

#### 操作系统文件
```gitignore
# macOS
.DS_Store
._*

# Windows
Thumbs.db
ehthumbs.db
Desktop.ini

# Linux
*~
.nfs*
```

### 🟡 建议忽略的文件

#### 日志文件
```gitignore
*.log
logs/
log/
```

#### 临时文件
```gitignore
tmp/
temp/
*.tmp
*.temp
```

#### 缓存文件
```gitignore
.cache/
.vite/
.nyc_output/
coverage/
```

#### 测试文件
```gitignore
coverage/
test-results/
junit.xml
```

### 🟢 特殊情况处理

#### 必须保留的文件
```gitignore
# 保留 Maven wrapper
!.mvn/wrapper/maven-wrapper.jar

# 保留源码中的 target 目录
!**/src/main/**/target/
!**/src/test/**/target/
```

#### 条件忽略
```gitignore
# 忽略所有 .env 文件，但不包含 .env.example
.env*
!.env.example
```

## 🔧 使用建议

### 1. 检查当前状态
```bash
# 查看被忽略的文件状态
git status --ignored

# 检查特定文件是否被忽略
git check-ignore <filename>
```

### 2. 测试忽略规则
```bash
# 测试文件是否会被忽略
git check-ignore -v <filename>

# 查看匹配的忽略规则
git check-ignore -n <filename>
```

### 3. 强制添加文件
```bash
# 即使文件在 .gitignore 中也强制添加
git add -f <filename>
```

## 🚨 常见问题

### 1. 文件已被跟踪
**问题**: 文件在添加 .gitignore 前已被提交到版本控制
**解决**:
```bash
# 先从版本控制中移除
git rm --cached <filename>

# 然后添加到 .gitignore
echo "<filename>" >> .gitignore
```

### 2. 规则不生效
**问题**: 添加了 .gitignore 规则但文件仍被跟踪
**解决**:
```bash
# 清理缓存
git rm -r --cached .

# 重新添加文件
git add .
```

### 3. 复杂的忽略规则
**问题**: 需要复杂的忽略逻辑
**解决**: 使用更精确的模式匹配
```gitignore
# 忽略所有 log 文件，但不包括 important.log
*.log
!important.log

# 忽略所有 node_modules，但不包括特定目录
node_modules/
!node_modules/important-package/
```

## 📋 最佳实践

### 1. 按项目分层
- 项目根目录：通用规则
- 子项目目录：特定规则
- 避免重复和冲突

### 2. 定期审查
```bash
# 定期检查被忽略的文件
git ls-files --others --ignored --exclude-standard

# 检查是否有重要的文件被忽略
git ls-files --deleted --ignored --exclude-standard
```

### 3. 文档化
- 为复杂的忽略规则添加注释
- 维护忽略规则说明文档
- 记录特殊处理的文件

### 4. 安全第一
- 始终忽略敏感信息
- 使用环境变量替代配置文件
- 定期检查是否有敏感信息被提交

## 🔄 更新策略

### 添加新规则
1. 确认文件类型和用途
2. 选择合适的 .gitignore 文件
3. 添加明确的注释
4. 测试规则是否生效

### 清理历史
1. 定期检查 .gitignore 是否有效
2. 移除不再需要的规则
3. 更新文档说明

### 团队协作
1. 确保团队成员了解忽略规则
2. 在项目文档中说明重要的忽略规则
3. 使用 pre-commit hooks 防止敏感文件提交

---

**通过合理的 .gitignore 管理，可以确保代码仓库的整洁和安全！** 🎉
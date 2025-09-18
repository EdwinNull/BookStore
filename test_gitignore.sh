#!/bin/bash

# Gitignore 测试脚本
echo "🔍 测试 .gitignore 文件效果..."
echo "=================================="

cd "$(dirname "$0")"

# 测试文件列表
test_files=(
    "target/"
    "frontend/node_modules/"
    "frontend/dist/"
    ".idea/"
    ".vscode/"
    "*.log"
    ".DS_Store"
    "Thumbs.db"
    ".env"
    "frontend/.env.local"
    "coverage/"
    "tmp/"
    "temp/"
)

echo "📋 测试文件列表："
printf '%s\n' "${test_files[@]}"
echo ""

# 测试每个文件
echo "✅ 测试结果："
for file in "${test_files[@]}"; do
    if git check-ignore "$file" >/dev/null 2>&1; then
        echo "✓ $file - 已被忽略"
    else
        echo "✗ $file - 未被忽略"
    fi
done

echo ""
echo "🎯 测试完成！如果看到 '已被忽略' 说明 .gitignore 工作正常。"
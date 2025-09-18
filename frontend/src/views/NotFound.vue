<template>
  <div class="not-found">
    <div class="not-found-container">
      <div class="error-code">404</div>
      <div class="error-message">页面不存在</div>
      <div class="error-description">
        抱歉，您访问的页面不存在或已被移除
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="$router.push('/')">
          返回首页
        </el-button>
        <el-button @click="$router.back()">
          返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.not-found-container {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
  margin-bottom: 16px;
}

.error-message {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.error-description {
  font-size: 18px;
  color: #666;
  margin-bottom: 32px;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }

  .error-message {
    font-size: 24px;
  }

  .error-description {
    font-size: 16px;
  }

  .error-actions {
    flex-direction: column;
    align-items: center;

    .el-button {
      min-width: 120px;
    }
  }
}
</style>
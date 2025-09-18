<template>
  <div class="admin-login-page">
    <div class="admin-login-container">
      <div class="login-form">
        <div class="form-header">
          <el-icon class="admin-icon"><Setting /></el-icon>
          <h1>管理员登录</h1>
          <p>请使用管理员账号登录系统</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          @submit.prevent="handleAdminLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入管理员用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleAdminLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="authStore.loading"
              @click="handleAdminLogin"
              class="login-button"
            >
              管理员登录
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <router-link to="/login" class="user-login-link">
              返回用户登录
            </router-link>
          </div>
        </el-form>

        <!-- 管理员提示信息 -->
        <div class="admin-tips">
          <el-alert
            title="管理员提示"
            type="info"
            :closable="false"
          >
            <template #default>
              <ul>
                <li>默认管理员账号：admin</li>
                <li>默认密码：admin123</li>
                <li>请妥善保管登录信息</li>
              </ul>
            </template>
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import type { LoginRequest } from '@/types'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()

const form = reactive<LoginRequest>({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入管理员用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleAdminLogin = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const result = await authStore.adminLogin(form)

    if (result.success) {
      ElMessage.success('管理员登录成功')
    } else {
      ElMessage.error(result.message || '管理员登录失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped lang="scss">
.admin-login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.admin-login-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  width: 100%;
  max-width: 400px;
}

.login-form {
  padding: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;

  .admin-icon {
    font-size: 48px;
    color: #E6A23C;
    margin-bottom: 16px;
  }

  h1 {
    font-size: 28px;
    color: #333;
    margin: 0 0 8px 0;
  }

  p {
    color: #666;
    margin: 0;
    font-size: 14px;
  }
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  background: #E6A23C;
  border-color: #E6A23C;

  &:hover {
    background: #ebb563;
    border-color: #ebb563;
  }
}

.form-footer {
  text-align: center;
  margin-top: 20px;

  .user-login-link {
    color: #409EFF;
    text-decoration: none;
    font-size: 14px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.admin-tips {
  margin-top: 24px;

  ul {
    margin: 8px 0 0 0;
    padding-left: 20px;

    li {
      margin-bottom: 4px;
      color: #666;
      font-size: 13px;
    }
  }
}

@media (max-width: 768px) {
  .admin-login-page {
    padding: 16px;
  }

  .login-form {
    padding: 24px;
  }

  .form-header {
    .admin-icon {
      font-size: 40px;
    }

    h1 {
      font-size: 24px;
    }
  }
}
</style>
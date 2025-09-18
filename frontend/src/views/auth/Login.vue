<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-form">
        <div class="form-header">
          <h1>用户登录</h1>
          <p>欢迎来到书店管理系统</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
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
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="authStore.loading"
              @click="handleLogin"
              class="login-button"
            >
              登录
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <router-link to="/register" class="register-link">
              还没有账号？立即注册
            </router-link>
            <router-link to="/admin/login" class="admin-link">
              管理员登录
            </router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
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
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 16, message: '用户名长度在 5 到 16 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 16, message: '密码长度在 5 到 16 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const result = await authStore.login(form)

    if (result.success) {
      ElMessage.success('登录成功')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
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
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  font-size: 14px;

  .register-link {
    color: #409EFF;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .admin-link {
    color: #666;
    text-decoration: none;

    &:hover {
      color: #409EFF;
    }
  }
}

@media (max-width: 768px) {
  .login-page {
    padding: 16px;
  }

  .login-form {
    padding: 24px;
  }

  .form-header {
    h1 {
      font-size: 24px;
    }
  }
}
</style>
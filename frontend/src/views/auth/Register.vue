<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-form">
        <div class="form-header">
          <h1>用户注册</h1>
          <p>创建您的账号，开始购物之旅</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          @submit.prevent="handleRegister"
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
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item prop="name">
            <el-input
              v-model="form.name"
              placeholder="请输入真实姓名"
              prefix-icon="UserFilled"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="authStore.loading"
              @click="handleRegister"
              class="register-button"
            >
              注册
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="login-link">
              立即登录
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
import type { RegisterRequest } from '@/types'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: ''
})

// 自定义验证规则：确认密码
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 16, message: '用户名长度在 5 到 16 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 16, message: '密码长度在 5 到 16 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const registerData: RegisterRequest = {
      username: form.username,
      password: form.password
    }

    const result = await authStore.register(registerData)

    if (result.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(result.message || '注册失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped lang="scss">
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 100%;
  max-width: 400px;
}

.register-form {
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

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;

  .login-link {
    color: #409EFF;
    text-decoration: none;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}

@media (max-width: 768px) {
  .register-page {
    padding: 16px;
  }

  .register-form {
    padding: 24px;
  }

  .form-header {
    h1 {
      font-size: 24px;
    }
  }
}
</style>
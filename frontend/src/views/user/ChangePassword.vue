<template>
  <MainLayout>
    <div class="change-password-page">
      <div class="container">
        <div class="page-header">
          <h1>修改密码</h1>
          <el-button @click="$router.back()">返回</el-button>
        </div>

        <div class="password-form">
          <el-card>
            <template #header>
              <div class="card-header">
                <h3>修改登录密码</h3>
                <p>为了您的账户安全，请定期更换密码</p>
              </div>
            </template>

            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="100px"
              size="large"
            >
              <el-form-item label="原密码" prop="oldPassword">
                <el-input
                  v-model="form.oldPassword"
                  type="password"
                  placeholder="请输入原密码"
                  show-password
                  clearable
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="form.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                  clearable
                />
                <div class="password-strength">
                  <div class="strength-label">密码强度：</div>
                  <div class="strength-bars">
                    <div
                      v-for="i in 4"
                      :key="i"
                      class="strength-bar"
                      :class="{ active: passwordStrength >= i }"
                    />
                  </div>
                  <div class="strength-text">{{ passwordStrengthText }}</div>
                </div>
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="请确认新密码"
                  show-password
                  clearable
                />
              </el-form-item>

              <el-form-item>
                <div class="form-actions">
                  <el-button @click="$router.back()">取消</el-button>
                  <el-button
                    type="primary"
                    @click="handleChangePassword"
                    :loading="loading"
                  >
                    确认修改
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 安全提示 -->
          <el-card class="tips-card">
            <template #header>
              <div class="card-header">
                <el-icon><InfoFilled /></el-icon>
                <h3>安全提示</h3>
              </div>
            </template>
            <ul class="security-tips">
              <li>密码长度至少8位，最多16位</li>
              <li>建议包含字母、数字和特殊字符</li>
              <li>不要使用生日、姓名等容易猜测的密码</li>
              <li>定期更换密码，不要在多个网站使用相同密码</li>
              <li>修改密码后需要重新登录</li>
            </ul>
          </el-card>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = form.newPassword
  if (!password) return 0

  let strength = 0

  // 长度检查
  if (password.length >= 8) strength++
  if (password.length >= 12) strength++

  // 字符类型检查
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++

  return Math.min(strength, 4)
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  switch (strength) {
    case 0: return '很弱'
    case 1: return '弱'
    case 2: return '一般'
    case 3: return '较强'
    case 4: return '很强'
    default: return '很弱'
  }
})

// 自定义验证规则
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePasswordStrength = (rule: any, value: any, callback: any) => {
  if (value && passwordStrength.value < 2) {
    callback(new Error('密码强度太弱，请使用更复杂的密码'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 5, max: 16, message: '密码长度在 5 到 16 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 5, max: 16, message: '密码长度在 5 到 16 个字符', trigger: 'blur' },
    { validator: validatePasswordStrength, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleChangePassword = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    const result = await authStore.changePassword(
      form.oldPassword,
      form.newPassword
    )

    if (result.success) {
      ElMessage.success(result.message)
      router.push('/profile')
    } else {
      ElMessage.error(result.message || '密码修改失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.change-password-page {
  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;

    h1 {
      font-size: 28px;
      color: #333;
      margin: 0;
    }
  }

  .password-form {
    .el-card {
      margin-bottom: 24px;
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #333;
      }

      p {
        margin: 4px 0 0 0;
        color: #666;
        font-size: 14px;
      }
    }

    .password-strength {
      margin-top: 8px;
      display: flex;
      align-items: center;
      gap: 8px;

      .strength-label {
        font-size: 12px;
        color: #666;
      }

      .strength-bars {
        display: flex;
        gap: 2px;

        .strength-bar {
          width: 40px;
          height: 4px;
          background: #e4e7ed;
          border-radius: 2px;
          transition: background-color 0.3s;

          &.active {
            &.strength-1,
            &:nth-child(1) {
              background: #f56c6c;
            }

            &.strength-2,
            &:nth-child(2) {
              background: #e6a23c;
            }

            &.strength-3,
            &:nth-child(3) {
              background: #409eff;
            }

            &.strength-4,
            &:nth-child(4) {
              background: #67c23a;
            }
          }
        }
      }

      .strength-text {
        font-size: 12px;
        color: #666;
        min-width: 40px;
      }
    }

    .form-actions {
      display: flex;
      gap: 12px;
      justify-content: center;
      margin-top: 24px;
    }
  }

  .tips-card {
    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;

      .el-icon {
        color: #409EFF;
        font-size: 20px;
      }

      h3 {
        margin: 0;
        font-size: 16px;
        color: #333;
      }
    }

    .security-tips {
      margin: 0;
      padding-left: 20px;

      li {
        margin-bottom: 8px;
        color: #666;
        font-size: 14px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .change-password-page {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .password-form {
      .password-strength {
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
      }

      .form-actions {
        flex-direction: column;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
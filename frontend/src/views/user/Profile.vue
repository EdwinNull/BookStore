<template>
  <MainLayout>
    <div class="profile-page">
      <div class="container">
        <div class="page-header">
          <h1>个人信息</h1>
        </div>

        <div class="profile-content">
          <!-- 个人信息卡片 -->
          <div class="profile-card">
            <div class="profile-header">
              <div class="avatar-section">
                <el-avatar :size="80" class="user-avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="user-info">
                  <h2>{{ authStore.user?.name }}</h2>
                  <p>{{ authStore.user?.username }}</p>
                </div>
              </div>
              <div class="user-status">
                <el-tag :type="authStore.user?.role === 'admin' ? 'danger' : 'primary'">
                  {{ authStore.user?.role === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </div>
            </div>

            <div class="profile-details">
              <div class="detail-section">
                <h3>基本信息</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label>用户名：</label>
                    <span>{{ authStore.user?.username }}</span>
                  </div>
                  <div class="detail-item">
                    <label>真实姓名：</label>
                    <span>{{ authStore.user?.name }}</span>
                  </div>
                  <div class="detail-item">
                    <label>注册时间：</label>
                    <span>{{ authStore.user?.created_at }}</span>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3>账户信息</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label>账户余额：</label>
                    <span class="balance">¥{{ authStore.user?.account_balance?.toFixed(2) || '0.00' }}</span>
                  </div>
                  <div class="detail-item">
                    <label>信用等级：</label>
                    <span>等级 {{ authStore.user?.credit_level || 1 }}</span>
                  </div>
                  <div class="detail-item">
                    <label>会员折扣：</label>
                    <span>{{ ((authStore.user?.discount || 1) * 10).toFixed(1) }}折</span>
                  </div>
                  <div class="detail-item">
                    <label>透支额度：</label>
                    <span>¥{{ authStore.user?.over_balance?.toFixed(2) || '0.00' }}</span>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3>联系信息</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label>联系地址：</label>
                    <span>{{ authStore.user?.address || '未设置' }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="profile-actions">
              <el-button type="primary" @click="showEditDialog = true">
                编辑信息
              </el-button>
              <el-button @click="$router.push('/profile/password')">
                修改密码
              </el-button>
            </div>
          </div>

          <!-- 信用等级说明 -->
          <div class="credit-info-card">
            <h3>信用等级说明</h3>
            <div class="credit-levels">
              <div class="credit-level">
                <div class="level-header">
                  <span class="level-badge">一级</span>
                  <span class="level-discount">10%折扣</span>
                </div>
                <p class="level-desc">不能透支</p>
              </div>
              <div class="credit-level">
                <div class="level-header">
                  <span class="level-badge">二级</span>
                  <span class="level-discount">15%折扣</span>
                </div>
                <p class="level-desc">不能透支</p>
              </div>
              <div class="credit-level">
                <div class="level-header">
                  <span class="level-badge">三级</span>
                  <span class="level-discount">15%折扣</span>
                </div>
                <p class="level-desc">可透支100元</p>
              </div>
              <div class="credit-level">
                <div class="level-header">
                  <span class="level-badge">四级</span>
                  <span class="level-discount">20%折扣</span>
                </div>
                <p class="level-desc">可透支200元</p>
              </div>
              <div class="credit-level">
                <div class="level-header">
                  <span class="level-badge current">五级</span>
                  <span class="level-discount">25%折扣</span>
                </div>
                <p class="level-desc">无透支限制</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 编辑信息对话框 -->
      <el-dialog
        v-model="showEditDialog"
        title="编辑个人信息"
        width="500px"
        @close="resetEditForm"
      >
        <el-form
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-width="80px"
        >
          <el-form-item label="真实姓名" prop="name">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="联系地址" prop="address">
            <el-input
              v-model="editForm.address"
              type="textarea"
              :rows="3"
              placeholder="请输入您的联系地址"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showEditDialog = false">取消</el-button>
            <el-button type="primary" @click="handleUpdateProfile" :loading="updating">
              保存
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const showEditDialog = ref(false)
const updating = ref(false)

const editFormRef = ref<FormInstance>()

const editForm = reactive({
  name: '',
  address: ''
})

const editRules: FormRules = {
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  address: [
    { max: 500, message: '地址长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 重置编辑表单
const resetEditForm = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields()
  }
}

// 显示编辑对话框
const showEditDialogHandler = () => {
  editForm.name = authStore.user?.name || ''
  editForm.address = authStore.user?.address || ''
  showEditDialog.value = true
}

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    updating.value = true

    const result = await authStore.updateUser(editForm)

    if (result.success) {
      ElMessage.success('个人信息更新成功')
      showEditDialog.value = false
    } else {
      ElMessage.error(result.message || '更新失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    updating.value = false
  }
}
</script>

<style scoped lang="scss">
.profile-page {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-header {
    margin-bottom: 32px;

    h1 {
      font-size: 28px;
      color: #333;
      margin: 0;
    }
  }

  .profile-content {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 24px;
  }

  .profile-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .profile-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f0f0f0;

      .avatar-section {
        display: flex;
        align-items: center;
        gap: 16px;

        .user-avatar {
          background: #409EFF;
          color: white;
        }

        .user-info {
          h2 {
            margin: 0 0 4px 0;
            font-size: 20px;
            color: #333;
          }

          p {
            margin: 0;
            color: #666;
            font-size: 14px;
          }
        }
      }
    }

    .profile-details {
      margin-bottom: 24px;

      .detail-section {
        margin-bottom: 24px;

        &:last-child {
          margin-bottom: 0;
        }

        h3 {
          font-size: 16px;
          color: #333;
          margin: 0 0 12px 0;
        }

        .detail-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px;

          .detail-item {
            display: flex;
            align-items: center;

            label {
              font-weight: bold;
              color: #666;
              margin-right: 8px;
              min-width: 80px;
            }

            .balance {
              font-weight: bold;
              color: #F56C6C;
              font-size: 16px;
            }
          }
        }
      }
    }

    .profile-actions {
      display: flex;
      gap: 12px;
      justify-content: center;
    }
  }

  .credit-info-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    h3 {
      font-size: 16px;
      color: #333;
      margin: 0 0 16px 0;
    }

    .credit-levels {
      .credit-level {
        padding: 12px;
        border: 1px solid #f0f0f0;
        border-radius: 6px;
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .level-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .level-badge {
            background: #f0f0f0;
            color: #666;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;

            &.current {
              background: #409EFF;
              color: white;
            }
          }

          .level-discount {
            color: #F56C6C;
            font-weight: bold;
          }
        }

        .level-desc {
          margin: 0;
          color: #666;
          font-size: 12px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .profile-page {
    .profile-content {
      grid-template-columns: 1fr;
    }

    .profile-card {
      .profile-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
      }

      .profile-details {
        .detail-section {
          .detail-grid {
            grid-template-columns: 1fr;
          }
        }
      }

      .profile-actions {
        flex-direction: column;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
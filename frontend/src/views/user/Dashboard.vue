<template>
  <MainLayout>
    <div class="dashboard">
      <div class="container">
        <div class="welcome-section">
          <h1>欢迎回来，{{ authStore.username }}</h1>
          <p>这是您的个人仪表板</p>
        </div>

        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon account-balance">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <h3>账户余额</h3>
              <p class="stat-value">¥{{ authStore.user?.account_balance?.toFixed(2) || '0.00' }}</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon credit-level">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-info">
              <h3>信用等级</h3>
              <p class="stat-value">等级 {{ authStore.user?.credit_level || 1 }}</p>
              <p class="stat-desc">{{ creditLevelText }}</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon discount">
              <el-icon><Discount /></el-icon>
            </div>
            <div class="stat-info">
              <h3>会员折扣</h3>
              <p class="stat-value">{{ ((authStore.user?.discount || 1) * 10).toFixed(1) }}折</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon cart">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <h3>购物车</h3>
              <p class="stat-value">{{ cartStore.totalItems }} 件</p>
              <p class="stat-desc">¥{{ cartStore.totalPrice.toFixed(2) }}</p>
            </div>
          </div>
        </div>

        <div class="quick-actions">
          <h2>快速操作</h2>
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="$router.push('/books')">
              <el-icon><Search /></el-icon>
              浏览图书
            </el-button>
            <el-button size="large" @click="$router.push('/orders')">
              <el-icon><List /></el-icon>
              我的订单
            </el-button>
            <el-button size="large" @click="$router.push('/profile')">
              <el-icon><User /></el-icon>
              个人信息
            </el-button>
            <el-button size="large" @click="showCart = true">
              <el-icon><ShoppingCart /></el-icon>
              查看购物车
            </el-button>
          </div>
        </div>

        <div class="recent-orders">
          <h2>最近订单</h2>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="order_id" label="订单号" width="100" />
            <el-table-column prop="total_price" label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ row.total_price }}
              </template>
            </el-table-column>
            <el-table-column prop="order_date" label="下单时间" width="180" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button link type="primary" @click="$router.push(`/orders/${row.order_id}`)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 购物车抽屉 -->
    <el-drawer
      v-model="showCart"
      title="购物车"
      direction="rtl"
      size="400px"
    >
      <CartDrawer @close="showCart = false" />
    </el-drawer>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Wallet, Star, Discount, ShoppingCart, Search, List, User } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import CartDrawer from '@/components/common/CartDrawer.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import type { Order } from '@/types'

const authStore = useAuthStore()
const cartStore = useCartStore()

const showCart = ref(false)
const recentOrders = ref<Order[]>([])

// 信用等级描述
const creditLevelText = computed(() => {
  const level = authStore.user?.credit_level || 1
  switch (level) {
    case 1: return '一级：10%折扣，不能透支'
    case 2: return '二级：15%折扣，不能透支'
    case 3: return '三级：15%折扣，可透支100元'
    case 4: return '四级：20%折扣，可透支200元'
    case 5: return '五级：25%折扣，无透支限制'
    default: return '未知等级'
  }
})

// 获取订单状态样式
const getStatusType = (status: string) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'confirmed': return 'primary'
    case 'processing': return 'info'
    case 'shipped': return 'success'
    case 'delivered': return 'success'
    case 'cancelled': return 'danger'
    default: return 'info'
  }
}

// 获取订单状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'pending': return '待确认'
    case 'confirmed': return '已确认'
    case 'processing': return '处理中'
    case 'shipped': return '已发货'
    case 'delivered': return '已送达'
    case 'cancelled': return '已取消'
    default: return '未知'
  }
}

// 页面加载时获取数据
onMounted(() => {
  // 这里应该调用API获取用户最近的订单
  // 暂时使用模拟数据
  recentOrders.value = [
    {
      order_id: 1001,
      user_id: authStore.userId,
      total_price: 158.00,
      order_date: '2024-01-15 10:30:00',
      status: 'delivered',
      created_at: '2024-01-15 10:30:00',
      updated_at: '2024-01-16 14:20:00'
    },
    {
      order_id: 1002,
      user_id: authStore.userId,
      total_price: 89.50,
      order_date: '2024-01-10 15:45:00',
      status: 'shipped',
      created_at: '2024-01-10 15:45:00',
      updated_at: '2024-01-11 09:30:00'
    }
  ]
})
</script>

<style scoped lang="scss">
.dashboard {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .welcome-section {
    margin-bottom: 32px;

    h1 {
      font-size: 32px;
      color: #333;
      margin: 0 0 8px 0;
    }

    p {
      color: #666;
      margin: 0;
      font-size: 16px;
    }
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
  }

  .stat-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      .el-icon {
        font-size: 24px;
        color: white;
      }

      &.account-balance {
        background: #409EFF;
      }

      &.credit-level {
        background: #E6A23C;
      }

      &.discount {
        background: #67C23A;
      }

      &.cart {
        background: #F56C6C;
      }
    }

    .stat-info {
      flex: 1;

      h3 {
        margin: 0 0 8px 0;
        font-size: 14px;
        color: #666;
        font-weight: normal;
      }

      .stat-value {
        margin: 0 0 4px 0;
        font-size: 20px;
        font-weight: bold;
        color: #333;
      }

      .stat-desc {
        margin: 0;
        font-size: 12px;
        color: #999;
      }
    }
  }

  .quick-actions {
    margin-bottom: 32px;

    h2 {
      font-size: 24px;
      color: #333;
      margin: 0 0 16px 0;
    }

    .action-buttons {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;

      .el-button {
        height: 60px;
        font-size: 16px;
      }
    }
  }

  .recent-orders {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    h2 {
      font-size: 24px;
      color: #333;
      margin: 0 0 16px 0;
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    .stats-grid {
      grid-template-columns: 1fr;
    }

    .quick-actions {
      .action-buttons {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
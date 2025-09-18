<template>
  <div class="admin-dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h1>欢迎回来，{{ authStore.username }}</h1>
      <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon users">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            {{ stats.userGrowth }}%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon books">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalBooks }}</div>
          <div class="stat-label">图书总数</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            {{ stats.bookGrowth }}%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orders">
          <el-icon><List /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalOrders }}</div>
          <div class="stat-label">总订单数</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            {{ stats.orderGrowth }}%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon revenue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">¥{{ stats.totalRevenue.toFixed(2) }}</div>
          <div class="stat-label">总收入</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            {{ stats.revenueGrowth }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 销售趋势图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>销售趋势</h3>
          <el-radio-group v-model="salesPeriod" size="small">
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="year">本年</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-content">
          <!-- 这里应该集成图表库，暂时用占位符 -->
          <div class="chart-placeholder">
            <el-icon><TrendCharts /></el-icon>
            <p>销售趋势图表</p>
          </div>
        </div>
      </div>

      <!-- 图书分类分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>图书分类分布</h3>
        </div>
        <div class="chart-content">
          <div class="chart-placeholder">
            <el-icon><PieChart /></el-icon>
            <p>图书分类饼图</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h3>快速操作</h3>
      <div class="action-buttons">
        <el-button type="primary" @click="$router.push('/admin/books/add')">
          <el-icon><Plus /></el-icon>
          添加图书
        </el-button>
        <el-button @click="$router.push('/admin/orders')">
          <el-icon><List /></el-icon>
          管理订单
        </el-button>
        <el-button @click="$router.push('/admin/users')">
          <el-icon><User /></el-icon>
          管理用户
        </el-button>
        <el-button @click="$router.push('/admin/purchases')">
          <el-icon><ShoppingCart /></el-icon>
          采购管理
        </el-button>
        <el-button @click="$router.push('/admin/suppliers')">
          <el-icon><Office /></el-icon>
          供应商管理
        </el-button>
      </div>
    </div>

    <!-- 最新动态 -->
    <div class="recent-activities">
      <div class="section-header">
        <h3>最新动态</h3>
        <el-button text>查看全部</el-button>
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :type="activity.type"
          :timestamp="activity.timestamp"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 库存预警 -->
    <div class="stock-alerts">
      <div class="section-header">
        <h3>库存预警</h3>
        <el-tag type="warning">{{ lowStockBooks.length }} 本图书库存不足</el-tag>
      </div>
      <el-table :data="lowStockBooks" style="width: 100%">
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="stock_quantity" label="当前库存" width="120">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.stock_quantity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="出版社" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/admin/books/edit/${row.book_id}`)">
              补货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  User,
  Reading,
  List,
  Money,
  ArrowUp,
  TrendCharts,
  PieChart,
  Plus,
  ShoppingCart,
  Office
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import type { Book } from '@/types'

const authStore = useAuthStore()

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 统计数据
const stats = ref({
  totalUsers: 1234,
  userGrowth: 12.5,
  totalBooks: 5678,
  bookGrowth: 8.3,
  totalOrders: 890,
  orderGrowth: 15.2,
  totalRevenue: 125678.90,
  revenueGrowth: 18.7
})

// 销售趋势周期
const salesPeriod = ref('week')

// 最新动态
const recentActivities = ref([
  {
    id: 1,
    content: '新用户 "张三" 注册',
    timestamp: '2024-01-15 10:30:00',
    type: 'primary'
  },
  {
    id: 2,
    content: '订单 #1001 已完成',
    timestamp: '2024-01-15 09:45:00',
    type: 'success'
  },
  {
    id: 3,
    content: '图书 "算法导论" 库存不足',
    timestamp: '2024-01-15 09:30:00',
    type: 'warning'
  },
  {
    id: 4,
    content: '新增图书 "深入理解计算机系统"',
    timestamp: '2024-01-15 08:15:00',
    type: 'info'
  }
])

// 库存不足的图书
const lowStockBooks = ref<Book[]>([])

// 获取统计数据
const fetchStats = () => {
  // 这里应该调用API获取实际统计数据
  console.log('获取统计数据...')
}

// 获取库存预警
const fetchLowStockBooks = () => {
  // 这里应该调用API获取库存不足的图书
  lowStockBooks.value = [
    {
      book_id: 2,
      title: '算法导论',
      author1: 'Thomas H. Cormen',
      publisher: '机械工业出版社',
      price: 128.00,
      stock_quantity: 8,
      low_quantity_flag: 1,
      created_at: '2024-01-02',
      updated_at: '2024-01-02'
    }
  ]
}

onMounted(() => {
  fetchStats()
  fetchLowStockBooks()
})
</script>

<style scoped lang="scss">
.admin-dashboard {
  .welcome-section {
    margin-bottom: 24px;

    h1 {
      font-size: 28px;
      color: #333;
      margin: 0 0 8px 0;
    }

    p {
      color: #666;
      margin: 0;
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

      &.users {
        background: #409EFF;
      }

      &.books {
        background: #67C23A;
      }

      &.orders {
        background: #E6A23C;
      }

      &.revenue {
        background: #F56C6C;
      }
    }

    .stat-content {
      flex: 1;

      .stat-number {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        margin-bottom: 4px;
      }

      .stat-label {
        color: #666;
        font-size: 14px;
        margin-bottom: 8px;
      }

      .stat-change {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        font-weight: bold;

        &.positive {
          color: #67C23A;
        }

        &.negative {
          color: #F56C6C;
        }

        .el-icon {
          font-size: 12px;
        }
      }
    }
  }

  .charts-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 24px;
    margin-bottom: 32px;
  }

  .chart-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #333;
      }
    }

    .chart-content {
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;

      .chart-placeholder {
        text-align: center;
        color: #999;

        .el-icon {
          font-size: 48px;
          margin-bottom: 8px;
        }

        p {
          margin: 0;
        }
      }
    }
  }

  .quick-actions {
    margin-bottom: 32px;

    h3 {
      font-size: 18px;
      color: #333;
      margin: 0 0 16px 0;
    }

    .action-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
    }
  }

  .recent-activities {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #333;
      }
    }
  }

  .stock-alerts {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #333;
      }
    }
  }
}

@media (max-width: 768px) {
  .admin-dashboard {
    .stats-grid {
      grid-template-columns: 1fr;
    }

    .charts-grid {
      grid-template-columns: 1fr;
    }

    .quick-actions {
      .action-buttons {
        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
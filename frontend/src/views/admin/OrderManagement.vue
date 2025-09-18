<template>
  <div class="order-management">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>

    <div class="content-card">
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="待确认" value="pending" />
              <el-option label="已确认" value="confirmed" />
              <el-option label="处理中" value="processing" />
              <el-option label="已发货" value="shipped" />
              <el-option label="已送达" value="delivered" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="order_id" label="订单号" width="100" />
        <el-table-column prop="user_id" label="用户ID" width="80" />
        <el-table-column prop="total_price" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.total_price }}
          </template>
        </el-table-column>
        <el-table-column prop="order_date" label="下单时间" width="160" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/admin/orders/${row.order_id}`)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { Order } from '@/types'

// 搜索表单
const searchForm = reactive({
  status: ''
})

// 订单列表
const orders = ref<Order[]>([])

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

// 搜索
const handleSearch = () => {
  fetchOrders()
}

// 重置搜索
const resetSearch = () => {
  searchForm.status = ''
  handleSearch()
}

// 获取订单列表
const fetchOrders = () => {
  // 这里应该调用API获取订单数据
  orders.value = [
    {
      order_id: 1001,
      user_id: 1,
      total_price: 158.00,
      order_date: '2024-01-15 10:30:00',
      status: 'delivered',
      created_at: '2024-01-15 10:30:00',
      updated_at: '2024-01-16 14:20:00'
    },
    {
      order_id: 1002,
      user_id: 2,
      total_price: 89.50,
      order_date: '2024-01-10 15:45:00',
      status: 'shipped',
      created_at: '2024-01-10 15:45:00',
      updated_at: '2024-01-11 09:30:00'
    }
  ]
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped lang="scss">
.order-management {
  .page-header {
    margin-bottom: 24px;

    h2 {
      font-size: 20px;
      color: #333;
      margin: 0;
    }
  }

  .content-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .search-section {
    margin-bottom: 24px;
  }
}
</style>
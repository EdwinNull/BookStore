<template>
  <MainLayout>
    <div class="order-detail-page">
      <div class="container">
        <div class="page-header">
          <h1>订单详情</h1>
          <el-button @click="$router.back()">返回</el-button>
        </div>

        <div v-if="order" class="order-content">
          <!-- 订单基本信息 -->
          <div class="section-card">
            <h2>订单信息</h2>
            <div class="info-grid">
              <div class="info-item">
                <label>订单号：</label>
                <span>{{ order.order_id }}</span>
              </div>
              <div class="info-item">
                <label>下单时间：</label>
                <span>{{ order.order_date }}</span>
              </div>
              <div class="info-item">
                <label>订单状态：</label>
                <el-tag :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
              <div class="info-item">
                <label>订单总额：</label>
                <span class="total-price">¥{{ order.total_price }}</span>
              </div>
            </div>
          </div>

          <!-- 收货信息 -->
          <div class="section-card">
            <h2>收货信息</h2>
            <div class="shipping-info">
              <div class="info-item">
                <label>收货人：</label>
                <span>{{ shippingInfo.receiver }}</span>
              </div>
              <div class="info-item">
                <label>联系电话：</label>
                <span>{{ shippingInfo.phone }}</span>
              </div>
              <div class="info-item">
                <label>收货地址：</label>
                <span>{{ shippingInfo.address }}</span>
              </div>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="section-card">
            <h2>商品信息</h2>
            <div class="order-items">
              <div
                v-for="item in orderItems"
                :key="item.id"
                class="order-item"
              >
                <div class="item-image">
                  <el-image
                    :src="item.cover_image || defaultCover"
                    :alt="item.title"
                    fit="cover"
                  />
                </div>
                <div class="item-info">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.author1 }}</p>
                  <p>{{ item.publisher }}</p>
                </div>
                <div class="item-price">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="quantity">x{{ item.quantity }}</span>
                  <span class="subtotal">小计：¥{{ (item.price * item.quantity).toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单跟踪 -->
          <div class="section-card">
            <h2>订单跟踪</h2>
            <el-timeline>
              <el-timeline-item
                v-for="(step, index) in orderTracking"
                :key="index"
                :type="step.type"
                :color="step.color"
                :timestamp="step.timestamp"
              >
                {{ step.content }}
              </el-timeline-item>
            </el-timeline>
          </div>

          <!-- 操作按钮 -->
          <div class="order-actions">
            <el-button @click="$router.back()">返回列表</el-button>
            <el-button type="primary" @click="handleReorder">再次购买</el-button>
            <el-button
              v-if="order.status === 'pending'"
              type="danger"
              @click="handleCancelOrder"
            >
              取消订单
            </el-button>
          </div>
        </div>

        <div v-else class="loading">
          <el-skeleton :rows="10" animated />
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/components/layout/MainLayout.vue'
import type { Order } from '@/types'

const route = useRoute()
const router = useRouter()

const order = ref<Order>()
const orderItems = ref<any[]>([])
const shippingInfo = ref({
  receiver: '张三',
  phone: '13800138000',
  address: '北京市朝阳区某某街道123号'
})

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iODAiIHZpZXdCb3g9IjAgMCA2MCA4MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0zMCAyMEw0MCA0MEgyMEwzMCAyMFoiIGZpbGw9IiNFMTVFOUIiLz4KPHBhdGggZD0iTTIwIDUwSDQwVjcwSDIwVjUwWiIgZmlsbD0iI0UxRTVFOCIvCjwvc3ZnPg=='

// 订单跟踪时间线
const orderTracking = ref([
  {
    content: '订单已提交',
    timestamp: '2024-01-15 10:30:00',
    type: 'primary',
    color: '#409EFF'
  },
  {
    content: '订单已确认',
    timestamp: '2024-01-15 11:00:00',
    type: 'success',
    color: '#67C23A'
  },
  {
    content: '订单处理中',
    timestamp: '2024-01-15 14:00:00',
    type: 'info',
    color: '#909399'
  },
  {
    content: '订单已发货',
    timestamp: '2024-01-16 09:00:00',
    type: 'warning',
    color: '#E6A23C'
  },
  {
    content: '订单已送达',
    timestamp: '2024-01-16 15:00:00',
    type: 'success',
    color: '#67C23A'
  }
])

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

// 取消订单
const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API取消订单
    ElMessage.success('订单已取消')
    router.push('/orders')
  } catch {
    // 用户取消操作
  }
}

// 再次购买
const handleReorder = () => {
  // 这里应该将订单中的商品添加到购物车
  ElMessage.success('已将商品添加到购物车')
}

// 获取订单详情
const fetchOrderDetail = async () => {
  const orderId = parseInt(route.params.id as string)

  if (!orderId) {
    ElMessage.error('订单ID无效')
    router.push('/orders')
    return
  }

  try {
    // 这里应该调用API获取订单详情
    // 暂时使用模拟数据
    order.value = {
      order_id: orderId,
      user_id: 1,
      total_price: 158.00,
      order_date: '2024-01-15 10:30:00',
      status: 'delivered',
      created_at: '2024-01-15 10:30:00',
      updated_at: '2024-01-16 14:20:00'
    }

    orderItems.value = [
      {
        id: 1,
        book_id: 1,
        title: '深入理解计算机系统',
        author1: 'Randal E. Bryant',
        publisher: '机械工业出版社',
        price: 139.00,
        quantity: 1,
        cover_image: ''
      },
      {
        id: 2,
        book_id: 2,
        title: '算法导论',
        author1: 'Thomas H. Cormen',
        publisher: '机械工业出版社',
        price: 128.00,
        quantity: 1,
        cover_image: ''
      }
    ]
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.push('/orders')
  }
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped lang="scss">
.order-detail-page {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h1 {
      font-size: 28px;
      color: #333;
      margin: 0;
    }
  }

  .section-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    h2 {
      font-size: 18px;
      color: #333;
      margin: 0 0 16px 0;
      padding-bottom: 8px;
      border-bottom: 1px solid #f0f0f0;
    }
  }

  .info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;

    .info-item {
      display: flex;
      align-items: center;

      label {
        font-weight: bold;
        color: #666;
        margin-right: 8px;
        min-width: 80px;
      }

      .total-price {
        font-size: 18px;
        font-weight: bold;
        color: #F56C6C;
      }
    }
  }

  .shipping-info {
    .info-item {
      display: flex;
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }

      label {
        font-weight: bold;
        color: #666;
        margin-right: 8px;
        min-width: 80px;
      }
    }
  }

  .order-items {
    .order-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .item-image {
        width: 80px;
        height: 100px;
        flex-shrink: 0;

        .el-image {
          width: 100%;
          height: 100%;
          border-radius: 4px;
        }
      }

      .item-info {
        flex: 1;
        min-width: 0;

        h4 {
          margin: 0 0 8px 0;
          font-size: 16px;
          color: #333;
        }

        p {
          margin: 0 0 4px 0;
          color: #666;
          font-size: 14px;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }

      .item-price {
        text-align: right;
        flex-shrink: 0;

        .price {
          display: block;
          font-weight: bold;
          color: #F56C6C;
          font-size: 16px;
          margin-bottom: 4px;
        }

        .quantity {
          display: block;
          color: #666;
          font-size: 14px;
          margin-bottom: 4px;
        }

        .subtotal {
          display: block;
          color: #333;
          font-size: 14px;
          font-weight: bold;
        }
      }
    }
  }

  .order-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
    margin-top: 32px;
  }

  .loading {
    padding: 40px 0;
  }
}

@media (max-width: 768px) {
  .order-detail-page {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .info-grid {
      grid-template-columns: 1fr;
    }

    .order-items {
      .order-item {
        flex-direction: column;
        align-items: flex-start;

        .item-price {
          text-align: left;
          width: 100%;
          margin-top: 12px;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }
      }
    }

    .order-actions {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
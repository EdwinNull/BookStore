<template>
  <MainLayout>
    <div class="orders-page">
      <div class="container">
        <div class="page-header">
          <h1>我的订单</h1>
          <el-button type="primary" @click="$router.push('/books')">
            继续购物
          </el-button>
        </div>

        <!-- 订单统计 -->
        <div class="order-stats">
          <div class="stat-item">
            <div class="stat-number">{{ orderStats.total }}</div>
            <div class="stat-label">总订单数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ orderStats.pending }}</div>
            <div class="stat-label">待处理</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ orderStats.delivered }}</div>
            <div class="stat-label">已完成</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">¥{{ orderStats.totalAmount.toFixed(2) }}</div>
            <div class="stat-label">总消费金额</div>
          </div>
        </div>

        <!-- 筛选和搜索 -->
        <div class="filter-section">
          <el-form :model="filterForm" inline>
            <el-form-item label="订单状态">
              <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
                <el-option label="待确认" value="pending" />
                <el-option label="已确认" value="confirmed" />
                <el-option label="处理中" value="processing" />
                <el-option label="已发货" value="shipped" />
                <el-option label="已送达" value="delivered" />
                <el-option label="已取消" value="cancelled" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 订单列表 -->
        <div class="orders-list">
          <div v-if="orders.length === 0" class="empty-orders">
            <el-empty description="暂无订单">
              <el-button type="primary" @click="$router.push('/books')">
                去购物
              </el-button>
            </el-empty>
          </div>

          <div v-else>
            <div
              v-for="order in orders"
              :key="order.order_id"
              class="order-card"
            >
              <div class="order-header">
                <div class="order-info">
                  <span class="order-number">订单号：{{ order.order_id }}</span>
                  <span class="order-date">{{ order.order_date }}</span>
                </div>
                <div class="order-status">
                  <el-tag :type="getStatusType(order.status)">
                    {{ getStatusText(order.status) }}
                  </el-tag>
                </div>
              </div>

              <div class="order-content">
                <div class="order-items">
                  <div
                    v-for="item in order.items"
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
                    </div>
                    <div class="item-price">
                      <span class="price">¥{{ item.price }}</span>
                      <span class="quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>

                <div class="order-summary">
                  <div class="total-price">
                    订单总额：<span class="price">¥{{ order.total_price }}</span>
                  </div>
                  <div class="order-actions">
                    <el-button
                      link
                      type="primary"
                      @click="$router.push(`/orders/${order.order_id}`)"
                    >
                      查看详情
                    </el-button>
                    <el-button
                      v-if="order.status === 'delivered'"
                      link
                      type="success"
                      @click="handleReorder(order)"
                    >
                      再次购买
                    </el-button>
                    <el-button
                      v-if="order.status === 'pending'"
                      link
                      type="danger"
                      @click="handleCancelOrder(order.order_id)"
                    >
                      取消订单
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/components/layout/MainLayout.vue'
import type { Order } from '@/types'

// 筛选表单
const filterForm = reactive({
  status: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 订单列表
const orders = ref<any[]>([])

// 订单统计
const orderStats = ref({
  total: 0,
  pending: 0,
  delivered: 0,
  totalAmount: 0
})

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iODAiIHZpZXdCb3g9IjAgMCA2MCA4MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0zMCAyMEw0MCA0MEgyMEwzMCAyMFoiIGZpbGw9IiNFMTVFOUIiLz4KPHBhdGggZD0iTTIwIDUwSDQwVjcwSDIwVjUwWiIgZmlsbD0iI0UxRTVFOCIvCjwvc3ZnPg=='

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

// 搜索订单
const handleSearch = () => {
  pagination.page = 1
  fetchOrders()
}

// 重置筛选
const resetFilter = () => {
  filterForm.status = ''
  filterForm.dateRange = []
  handleSearch()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchOrders()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchOrders()
}

// 取消订单
const handleCancelOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API取消订单
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch {
    // 用户取消操作
  }
}

// 再次购买
const handleReorder = (order: Order) => {
  // 这里应该将订单中的商品添加到购物车
  ElMessage.success('已将商品添加到购物车')
}

// 获取订单列表
const fetchOrders = () => {
  // 这里应该调用API获取订单数据
  // 暂时使用模拟数据
  orders.value = [
    {
      order_id: 1001,
      user_id: 1,
      total_price: 158.00,
      order_date: '2024-01-15 10:30:00',
      status: 'delivered',
      created_at: '2024-01-15 10:30:00',
      updated_at: '2024-01-16 14:20:00',
      items: [
        {
          id: 1,
          book_id: 1,
          title: '深入理解计算机系统',
          author1: 'Randal E. Bryant',
          price: 139.00,
          quantity: 1,
          cover_image: ''
        },
        {
          id: 2,
          book_id: 2,
          title: '算法导论',
          author1: 'Thomas H. Cormen',
          price: 128.00,
          quantity: 1,
          cover_image: ''
        }
      ]
    }
  ]

  pagination.total = orders.value.length
}

// 获取订单统计
const fetchOrderStats = () => {
  // 这里应该调用API获取统计数据
  orderStats.value = {
    total: 15,
    pending: 2,
    delivered: 12,
    totalAmount: 2580.50
  }
}

onMounted(() => {
  fetchOrders()
  fetchOrderStats()
})
</script>

<style scoped lang="scss">
.orders-page {
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

  .order-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .stat-item {
      background: white;
      border-radius: 8px;
      padding: 20px;
      text-align: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .stat-number {
        font-size: 24px;
        font-weight: bold;
        color: #409EFF;
        margin-bottom: 8px;
      }

      .stat-label {
        color: #666;
        font-size: 14px;
      }
    }
  }

  .filter-section {
    background: white;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .orders-list {
    .empty-orders {
      text-align: center;
      padding: 60px 0;
    }

    .order-card {
      background: white;
      border-radius: 8px;
      margin-bottom: 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      overflow: hidden;

      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        background: #f8f9fa;
        border-bottom: 1px solid #e9ecef;

        .order-info {
          display: flex;
          align-items: center;
          gap: 16px;

          .order-number {
            font-weight: bold;
            color: #333;
          }

          .order-date {
            color: #666;
            font-size: 14px;
          }
        }
      }

      .order-content {
        padding: 20px;

        .order-items {
          margin-bottom: 16px;

          .order-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;

            &:last-child {
              border-bottom: none;
            }

            .item-image {
              width: 60px;
              height: 80px;
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
                margin: 0 0 4px 0;
                font-size: 14px;
                font-weight: normal;
                color: #333;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }

              p {
                margin: 0;
                color: #666;
                font-size: 12px;
              }
            }

            .item-price {
              text-align: right;
              flex-shrink: 0;

              .price {
                display: block;
                font-weight: bold;
                color: #F56C6C;
                margin-bottom: 4px;
              }

              .quantity {
                color: #666;
                font-size: 12px;
              }
            }
          }
        }

        .order-summary {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 16px;
          border-top: 1px solid #f0f0f0;

          .total-price {
            font-size: 16px;
            color: #333;

            .price {
              font-weight: bold;
              color: #F56C6C;
              font-size: 18px;
            }
          }

          .order-actions {
            display: flex;
            gap: 8px;
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 24px;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .orders-page {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .order-stats {
      grid-template-columns: repeat(2, 1fr);
    }

    .order-card {
      .order-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
      }

      .order-content {
        .order-items {
          .order-item {
            flex-direction: column;
            align-items: flex-start;

            .item-price {
              text-align: left;
              width: 100%;
              display: flex;
              justify-content: space-between;
              margin-top: 8px;
            }
          }
        }

        .order-summary {
          flex-direction: column;
          align-items: flex-start;
          gap: 12px;

          .order-actions {
            width: 100%;
            justify-content: flex-end;
          }
        }
      }
    }
  }
}
</style>
<template>
  <div class="cart-drawer">
    <div v-if="cartStore.isEmpty" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="$router.push('/books')">
          去逛逛
        </el-button>
      </el-empty>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div
          v-for="item in cartStore.items"
          :key="item.book_id"
          class="cart-item"
        >
          <div class="item-image">
            <el-image
              :src="item.cover_image || defaultCover"
              :alt="item.title"
              fit="cover"
            />
          </div>

          <div class="item-info">
            <h4 class="item-title">{{ item.title }}</h4>
            <p class="item-price">¥{{ item.price }}</p>
          </div>

          <div class="item-actions">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="99"
              size="small"
              @change="(value) => cartStore.updateQuantity(item.book_id, value as number)"
            />
            <el-button
              type="danger"
              size="small"
              circle
              @click="cartStore.removeItem(item.book_id)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <div class="cart-summary">
        <div class="summary-row">
          <span>商品总数:</span>
          <span>{{ cartStore.totalItems }} 件</span>
        </div>
        <div class="summary-row">
          <span>商品总价:</span>
          <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
      </div>

      <div class="cart-actions">
        <el-button @click="$emit('close')">关闭</el-button>
        <el-button type="primary" @click="handleCheckout">
          去结算
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'

defineEmits<{
  close: []
}>()

const router = useRouter()
const cartStore = useCartStore()

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iODAiIHZpZXdCb3g9IjAgMCA2MCA4MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0zMCAyMEw0MCA0MEgyMEwzMCAyMFoiIGZpbGw9IiNFMTVFOUIiLz4KPHBhdGggZD0iTTIwIDUwSDQwVjcwSDIwVjUwWiIgZmlsbD0iI0UxRTVFOCIvCjwvc3ZnPg=='

const handleCheckout = () => {
  router.push('/checkout')
}
</script>

<style scoped lang="scss">
.cart-drawer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.empty-cart {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.item-image {
  width: 60px;
  height: 80px;
  margin-right: 12px;
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

  .item-title {
    margin: 0 0 4px 0;
    font-size: 14px;
    font-weight: normal;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .item-price {
    margin: 0;
    color: #F56C6C;
    font-size: 14px;
    font-weight: bold;
  }
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.cart-summary {
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  margin-bottom: 16px;

  .summary-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }

    .total-price {
      font-size: 18px;
      font-weight: bold;
      color: #F56C6C;
    }
  }
}

.cart-actions {
  display: flex;
  gap: 12px;

  .el-button {
    flex: 1;
  }
}
</style>
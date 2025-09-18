<template>
  <div class="book-card" @click="handleClick">
    <div class="book-cover">
      <el-image
        :src="book.cover_image || defaultCover"
        :alt="book.title"
        fit="cover"
        loading="lazy"
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
            <span>暂无封面</span>
          </div>
        </template>
      </el-image>

      <!-- 库存状态标签 -->
      <div class="stock-badge" :class="stockStatusClass">
        {{ stockStatusText }}
      </div>
    </div>

    <div class="book-info">
      <h3 class="book-title" :title="book.title">{{ book.title }}</h3>

      <div class="book-author" v-if="book.author1">
        <el-icon><User /></el-icon>
        {{ book.author1 }}
        <span v-if="book.author2" class="author-separator">,</span>
        {{ book.author2 }}
      </div>

      <div class="book-publisher" v-if="book.publisher">
        <el-icon><Office /></el-icon>
        {{ book.publisher }}
      </div>

      <div class="book-meta">
        <span class="book-price">¥{{ book.price }}</span>
        <span class="book-stock">库存: {{ book.stock_quantity }}</span>
      </div>

      <div class="book-actions">
        <el-button
          type="primary"
          size="small"
          @click.stop="handleAddToCart"
          :disabled="book.stock_quantity === 0"
          :loading="addingToCart"
        >
          <el-icon><ShoppingCart /></el-icon>
          {{ book.stock_quantity === 0 ? '缺货' : '加入购物车' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Office, Picture, ShoppingCart } from '@element-plus/icons-vue'
import type { Book } from '@/types'
import { useCartStore } from '@/stores/cart'

interface Props {
  book: Book
}

const props = defineProps<Props>()
const router = useRouter()
const cartStore = useCartStore()

const addingToCart = ref(false)
const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjI4MCIgdmlld0JveD0iMCAwIDIwMCAyODAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMjgwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0xMDAgNzBMMTIwIDEwMEg4MEwxMDAgNzBaIiBmaWxsPSIjRTFFNUY5Ii8+CjxwYXRoIGQ9Ik04MCAxMTBIMTIwVjE4MEg4MFYxMTBaIiBmaWxsPSIjRTFFNUY5Ii8+CjxwYXRoIGQ9Ik0xMDAgMjBWMTYwIiBzdHJva2U9IiNEMUQxRDEiIHN0cm9rZS13aWR0aD0iMiIgc3Ryb2tlLWRhc2hhcnJheT0iMTAgMTAiLz4KPHRleHQgeD0iMTAwIiB5PSIyNTAiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiM5QTlBOUEiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCI+57yW56iL5bCHPC90ZXh0Pgo8L3N2Zz4='

// 库存状态计算
const stockStatus = computed(() => {
  if (props.book.stock_quantity === 0) return 'out-of-stock'
  if (props.book.stock_quantity < 10) return 'low-stock'
  return 'in-stock'
})

const stockStatusClass = computed(() => {
  return {
    'out-of-stock': stockStatus.value === 'out-of-stock',
    'low-stock': stockStatus.value === 'low-stock',
    'in-stock': stockStatus.value === 'in-stock'
  }
})

const stockStatusText = computed(() => {
  switch (stockStatus.value) {
    case 'out-of-stock': return '缺货'
    case 'low-stock': return '库存不足'
    default: return '有库存'
  }
})

const handleClick = () => {
  router.push(`/books/${props.book.book_id}`)
}

const handleAddToCart = async () => {
  if (props.book.stock_quantity === 0) {
    ElMessage.warning('该图书已售罄')
    return
  }

  addingToCart.value = true
  try {
    cartStore.addItem(props.book, 1)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  } finally {
    addingToCart.value = false
  }
}
</script>

<style scoped lang="scss">
.book-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
}

.book-cover {
  position: relative;
  height: 200px;
  overflow: hidden;

  .el-image {
    width: 100%;
    height: 100%;
  }

  .image-error {
    width: 100%;
    height: 100%;
    background: #f5f5f5;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;

    .el-icon {
      font-size: 32px;
      margin-bottom: 8px;
    }

    span {
      font-size: 14px;
    }
  }

  .stock-badge {
    position: absolute;
    top: 8px;
    right: 8px;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
    color: white;

    &.out-of-stock {
      background: #F56C6C;
    }

    &.low-stock {
      background: #E6A23C;
    }

    &.in-stock {
      background: #67C23A;
    }
  }
}

.book-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author,
.book-publisher {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
  margin-bottom: 4px;

  .el-icon {
    font-size: 14px;
    color: #999;
  }

  .author-separator {
    margin: 0 2px;
  }
}

.book-meta {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.book-price {
  font-size: 18px;
  font-weight: bold;
  color: #F56C6C;
}

.book-stock {
  font-size: 12px;
  color: #666;
}

.book-actions {
  .el-button {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .book-cover {
    height: 150px;
  }

  .book-info {
    padding: 12px;
  }

  .book-title {
    font-size: 14px;
  }

  .book-price {
    font-size: 16px;
  }
}
</style>
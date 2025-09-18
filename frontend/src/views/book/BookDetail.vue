<template>
  <MainLayout>
    <div class="book-detail-page">
      <div class="container">
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/books' }">图书浏览</el-breadcrumb-item>
            <el-breadcrumb-item>图书详情</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div v-if="book" class="book-content">
          <div class="book-main">
            <!-- 图书封面 -->
            <div class="book-cover">
              <el-image
                :src="book.cover_image || defaultCover"
                :alt="book.title"
                fit="cover"
                style="width: 100%; height: 100%"
              />
            </div>

            <!-- 图书信息 -->
            <div class="book-info">
              <h1 class="book-title">{{ book.title }}</h1>

              <div class="book-meta">
                <div class="meta-item">
                  <span class="label">作者：</span>
                  <span class="value">{{ book.author1 }}</span>
                  <span v-if="book.author2" class="co-author">, {{ book.author2 }}</span>
                  <span v-if="book.author3" class="co-author">, {{ book.author3 }}</span>
                  <span v-if="book.author4" class="co-author">, {{ book.author4 }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">出版社：</span>
                  <span class="value">{{ book.publisher }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">价格：</span>
                  <span class="price">¥{{ book.price }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">库存：</span>
                  <el-tag :type="book.stock_quantity > 10 ? 'success' : 'danger'">
                    {{ book.stock_quantity }} 本
                  </el-tag>
                </div>
              </div>

              <!-- 购买按钮 -->
              <div class="purchase-section">
                <div class="price-section">
                  <div class="current-price">¥{{ book.price }}</div>
                  <div class="original-price" v-if="authStore.user?.discount < 1">
                    原价：¥{{ (book.price / (authStore.user?.discount || 1)).toFixed(2) }}
                  </div>
                  <div class="discount" v-if="authStore.user?.discount < 1">
                    会员{{ ((authStore.user?.discount || 1) * 10).toFixed(1) }}折
                  </div>
                </div>

                <div class="action-buttons">
                  <el-button
                    type="primary"
                    size="large"
                    :disabled="book.stock_quantity === 0"
                    @click="handleAddToCart"
                  >
                    <el-icon><ShoppingCart /></el-icon>
                    {{ book.stock_quantity === 0 ? '缺货' : '加入购物车' }}
                  </el-button>
                  <el-button size="large" @click="handleBuyNow">
                    立即购买
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 图书详细信息 -->
          <div class="book-details">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="图书简介" name="description">
                <div class="tab-content">
                  <p>这是一本优秀的图书，详细介绍了相关知识和技能。本书内容丰富，结构清晰，适合各类读者学习参考。</p>
                  <p>本书特色：</p>
                  <ul>
                    <li>内容全面，覆盖核心知识点</li>
                    <li>实例丰富，理论与实践结合</li>
                    <li>语言通俗易懂，适合自学</li>
                    <li>配套资源完整，便于学习</li>
                  </ul>
                </div>
              </el-tab-pane>

              <el-tab-pane label="目录" name="contents">
                <div class="tab-content">
                  <div v-if="book.table_of_contents" class="table-of-contents">
                    <pre>{{ book.table_of_contents }}</pre>
                  </div>
                  <div v-else class="empty-contents">
                    <el-empty description="暂无目录信息" />
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="作者介绍" name="author">
                <div class="tab-content">
                  <p>本书作者是该领域的专家，拥有丰富的教学和实践经验。作者通过多年的积累，将复杂的知识体系化、系统化，为读者提供了优质的学习资源。</p>
                </div>
              </el-tab-pane>
            </el-tabs>
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
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import type { Book } from '@/types'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const book = ref<Book>()
const activeTab = ref('description')
const loading = ref(false)

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjQwMCIgdmlld0JveD0iMCAwIDMwMCA0MDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iNDAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0xNTAgMTAwTDE4MCAxNjBIMTIwTDE1MCAxMDBaIiBmaWxsPSIjRTFFNUY5Ii8+CjxwYXRoIGQ9Ik0xMjAgMjAwSDE4MFYzMDBIMTIwVjIwMFoiIGZpbGw9IiNFMTVFOUIiLz4KPHRleHQgeD0iMTUwIiB5PSIzNTAiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiM5QTlBOUEiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNiI+57yW56iL5bCHPC90ZXh0Pgo8L3N2Zz4='

// 添加到购物车
const handleAddToCart = () => {
  if (!book.value) return

  if (book.value.stock_quantity === 0) {
    ElMessage.warning('该图书已售罄')
    return
  }

  cartStore.addItem(book.value, 1)
  ElMessage.success('已添加到购物车')
}

// 立即购买
const handleBuyNow = () => {
  if (!book.value) return

  if (book.value.stock_quantity === 0) {
    ElMessage.warning('该图书已售罄')
    return
  }

  // 添加到购物车并跳转到结算页面
  cartStore.addItem(book.value, 1)
  router.push('/checkout')
}

// 获取图书详情
const fetchBookDetail = async () => {
  const bookId = parseInt(route.params.id as string)

  if (!bookId) {
    ElMessage.error('图书ID无效')
    router.push('/books')
    return
  }

  loading.value = true

  try {
    // 这里应该调用API获取图书详情
    // 暂时使用模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))

    book.value = {
      book_id: bookId,
      title: '深入理解计算机系统',
      author1: 'Randal E. Bryant',
      author2: 'David R. O\'Hallaron',
      publisher: '机械工业出版社',
      price: 139.00,
      stock_quantity: 25,
      low_quantity_flag: 0,
      keywords: '计算机系统,操作系统,程序设计',
      table_of_contents: `第1章 计算机系统漫游
第2章 信息的表示和处理
第3章 程序的机器级表示
第4章 处理器体系结构
第5章 优化程序性能
第6章 存储器层次结构
第7章 链接
第8章 异常控制流
第9章 虚拟内存
第10章 系统级I/O
第11章 网络编程
第12章 并发编程`,
      created_at: '2024-01-15 10:30:00',
      updated_at: '2024-01-15 10:30:00'
    }
  } catch (error) {
    ElMessage.error('获取图书详情失败')
    router.push('/books')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped lang="scss">
.book-detail-page {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .breadcrumb {
    margin-bottom: 24px;
  }

  .book-content {
    .book-main {
      display: grid;
      grid-template-columns: 300px 1fr;
      gap: 40px;
      margin-bottom: 40px;

      .book-cover {
        background: white;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        height: 400px;

        .el-image {
          border-radius: 8px;
        }
      }

      .book-info {
        .book-title {
          font-size: 28px;
          font-weight: bold;
          color: #333;
          margin: 0 0 20px 0;
          line-height: 1.4;
        }

        .book-meta {
          margin-bottom: 32px;

          .meta-item {
            display: flex;
            align-items: center;
            margin-bottom: 12px;

            .label {
              font-weight: bold;
              color: #666;
              min-width: 60px;
            }

            .value {
              color: #333;
            }

            .co-author {
              color: #666;
            }

            .price {
              font-size: 24px;
              font-weight: bold;
              color: #F56C6C;
            }
          }
        }

        .purchase-section {
          background: #f8f9fa;
          border-radius: 8px;
          padding: 24px;

          .price-section {
            margin-bottom: 20px;

            .current-price {
              font-size: 32px;
              font-weight: bold;
              color: #F56C6C;
              margin-bottom: 8px;
            }

            .original-price {
              color: #999;
              text-decoration: line-through;
              font-size: 14px;
              margin-bottom: 4px;
            }

            .discount {
              color: #E6A23C;
              font-size: 14px;
              font-weight: bold;
            }
          }

          .action-buttons {
            display: flex;
            gap: 12px;
          }
        }
      }
    }

    .book-details {
      background: white;
      border-radius: 8px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .tab-content {
        padding: 20px 0;
        line-height: 1.8;
        color: #333;

        ul {
          padding-left: 20px;
        }

        .table-of-contents {
          white-space: pre-wrap;
          font-family: monospace;
          line-height: 1.6;
        }

        .empty-contents {
          text-align: center;
          padding: 40px 0;
        }
      }
    }
  }

  .loading {
    padding: 40px 0;
  }
}

@media (max-width: 768px) {
  .book-detail-page {
    .book-content {
      .book-main {
        grid-template-columns: 1fr;
        gap: 24px;

        .book-cover {
          height: 300px;
          margin: 0 auto;
          max-width: 300px;
        }

        .book-info {
          .book-title {
            font-size: 24px;
          }

          .purchase-section {
            .action-buttons {
              flex-direction: column;

              .el-button {
                width: 100%;
              }
            }
          }
        }
      }
    }
  }
}
</style>
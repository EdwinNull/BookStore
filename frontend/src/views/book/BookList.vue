<template>
  <MainLayout>
    <div class="book-list-page">
      <div class="container">
        <div class="page-header">
          <h1>图书浏览</h1>
        </div>

        <!-- 搜索和筛选 -->
        <div class="search-section">
          <el-card>
            <el-form :model="searchForm" inline>
              <el-form-item label="书名">
                <el-input
                  v-model="searchForm.title"
                  placeholder="请输入书名"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="作者">
                <el-input
                  v-model="searchForm.author"
                  placeholder="请输入作者"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="出版社">
                <el-input
                  v-model="searchForm.publisher"
                  placeholder="请输入出版社"
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <!-- 排序和视图切换 -->
        <div class="view-controls">
          <div class="sort-controls">
            <span>排序：</span>
            <el-radio-group v-model="sortBy" @change="handleSort">
              <el-radio-button label="default">默认</el-radio-button>
              <el-radio-button label="priceAsc">价格从低到高</el-radio-button>
              <el-radio-button label="priceDesc">价格从高到低</el-radio-button>
              <el-radio-button label="newest">最新上架</el-radio-button>
            </el-radio-group>
          </div>
          <div class="view-toggle">
            <el-button-group>
              <el-button
                :type="viewMode === 'grid' ? 'primary' : 'default'"
                @click="viewMode = 'grid'"
              >
                <el-icon><Grid /></el-icon>
              </el-button>
              <el-button
                :type="viewMode === 'list' ? 'primary' : 'default'"
                @click="viewMode = 'list'"
              >
                <el-icon><List /></el-icon>
              </el-button>
            </el-button-group>
          </div>
        </div>

        <!-- 图书列表 -->
        <div class="books-container">
          <div v-if="loading" class="loading">
            <el-skeleton :rows="6" animated />
          </div>

          <div v-else-if="books.length === 0" class="empty">
            <el-empty description="暂无图书">
              <el-button type="primary" @click="$router.push('/')">
                返回首页
              </el-button>
            </el-empty>
          </div>

          <div v-else :class="['books-wrapper', viewMode]">
            <BookCard
              v-for="book in books"
              :key="book.book_id"
              :book="book"
            />
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[12, 24, 48]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Grid, List } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import BookCard from '@/components/common/BookCard.vue'
import type { Book } from '@/types'

const route = useRoute()
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
  publisher: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

// 排序
const sortBy = ref('default')

// 视图模式
const viewMode = ref<'grid' | 'list'>('grid')

// 加载状态
const loading = ref(false)

// 图书列表
const books = ref<Book[]>([])

// 处理URL参数
const handleQueryParams = () => {
  const query = route.query
  if (query.title) searchForm.title = query.title as string
  if (query.author) searchForm.author = query.author as string
  if (query.publisher) searchForm.publisher = query.publisher as string
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchBooks()

  // 更新URL参数
  router.push({
    path: route.path,
    query: {
      ...searchForm,
      page: 1
    }
  })
}

// 重置搜索
const resetSearch = () => {
  searchForm.title = ''
  searchForm.author = ''
  searchForm.publisher = ''
  handleSearch()
}

// 排序
const handleSort = () => {
  fetchBooks()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchBooks()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchBooks()

  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取图书列表
const fetchBooks = async () => {
  loading.value = true

  try {
    // 这里应该调用API获取图书数据
    // 暂时使用模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))

    books.value = [
      {
        book_id: 1,
        title: '深入理解计算机系统',
        author1: 'Randal E. Bryant',
        author2: 'David R. O\'Hallaron',
        publisher: '机械工业出版社',
        price: 139.00,
        stock_quantity: 25,
        low_quantity_flag: 0,
        created_at: '2024-01-15 10:30:00',
        updated_at: '2024-01-15 10:30:00'
      },
      {
        book_id: 2,
        title: '算法导论',
        author1: 'Thomas H. Cormen',
        author2: 'Charles E. Leiserson',
        author3: 'Ronald L. Rivest',
        author4: 'Clifford Stein',
        publisher: '机械工业出版社',
        price: 128.00,
        stock_quantity: 8,
        low_quantity_flag: 1,
        created_at: '2024-01-10 15:45:00',
        updated_at: '2024-01-10 15:45:00'
      },
      {
        book_id: 3,
        title: 'Vue.js设计与实现',
        author1: '霍春阳',
        publisher: '人民邮电出版社',
        price: 89.00,
        stock_quantity: 15,
        low_quantity_flag: 0,
        created_at: '2024-01-05 09:20:00',
        updated_at: '2024-01-05 09:20:00'
      }
    ]

    pagination.total = books.value.length

    // 排序
    if (sortBy.value === 'priceAsc') {
      books.value.sort((a, b) => a.price - b.price)
    } else if (sortBy.value === 'priceDesc') {
      books.value.sort((a, b) => b.price - a.price)
    } else if (sortBy.value === 'newest') {
      books.value.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  handleQueryParams()
  fetchBooks()
})
</script>

<style scoped lang="scss">
.book-list-page {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-header {
    margin-bottom: 24px;

    h1 {
      font-size: 28px;
      color: #333;
      margin: 0;
    }
  }

  .search-section {
    margin-bottom: 24px;
  }

  .view-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .sort-controls {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .books-container {
    margin-bottom: 32px;

    .loading {
      padding: 40px 0;
    }

    .empty {
      padding: 60px 0;
      text-align: center;
    }

    .books-wrapper {
      &.grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 24px;
      }

      &.list {
        display: flex;
        flex-direction: column;
        gap: 16px;
      }
    }
  }

  .pagination {
    text-align: center;
  }
}

@media (max-width: 768px) {
  .book-list-page {
    .search-section {
      .el-form {
        .el-form-item {
          margin-bottom: 12px;
        }
      }
    }

    .view-controls {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;

      .sort-controls {
        flex-wrap: wrap;
      }
    }

    .books-container {
      .books-wrapper {
        &.grid {
          grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
          gap: 16px;
        }
      }
    }
  }
}
</style>
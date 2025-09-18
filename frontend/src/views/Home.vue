<template>
  <MainLayout>
    <div class="home-page">
      <!-- 轮播图 -->
      <div class="banner">
        <el-carousel height="400px" indicator-position="outside">
          <el-carousel-item v-for="banner in banners" :key="banner.id">
            <div class="banner-content" :style="{ backgroundImage: `url(${banner.image})` }">
              <div class="banner-text">
                <h1>{{ banner.title }}</h1>
                <p>{{ banner.description }}</p>
                <el-button type="primary" size="large" @click="banner.action">
                  {{ banner.buttonText }}
                </el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 分类导航 -->
      <div class="categories">
        <div class="container">
          <h2 class="section-title">图书分类</h2>
          <div class="category-grid">
            <div
              v-for="category in categories"
              :key="category.id"
              class="category-card"
              @click="browseByCategory(category)"
            >
              <el-icon :size="32">
                <component :is="category.icon" />
              </el-icon>
              <h3>{{ category.name }}</h3>
              <p>{{ category.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门图书 -->
      <div class="popular-books">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">热门图书</h2>
            <el-button text @click="$router.push('/books')">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>

          <div class="books-grid">
            <BookCard
              v-for="book in popularBooks"
              :key="book.book_id"
              :book="book"
            />
          </div>
        </div>
      </div>

      <!-- 新书上架 -->
      <div class="new-books">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">新书上架</h2>
            <el-button text @click="$router.push('/books')">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>

          <div class="books-grid">
            <BookCard
              v-for="book in newBooks"
              :key="book.book_id"
              :book="book"
            />
          </div>
        </div>
      </div>

      <!-- 库存告急 -->
      <div class="low-stock">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">库存告急</h2>
            <el-button text @click="$router.push('/books')">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>

          <div class="books-grid">
            <BookCard
              v-for="book in lowStockBooks"
              :key="book.book_id"
              :book="book"
            />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import BookCard from '@/components/common/BookCard.vue'
import type { Book } from '@/types'

const router = useRouter()

// 轮播图数据
const banners = ref([
  {
    id: 1,
    title: '精选好书',
    description: '发现知识的海洋，探索无限可能',
    buttonText: '立即浏览',
    image: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=1200&h=400&fit=crop',
    action: () => router.push('/books')
  },
  {
    id: 2,
    title: '新书上架',
    description: '最新出版的优质图书，抢先阅读',
    buttonText: '查看新书',
    image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=1200&h=400&fit=crop',
    action: () => router.push('/books?sort=new')
  },
  {
    id: 3,
    title: '特价优惠',
    description: '精选图书特价优惠，限时抢购',
    buttonText: '查看优惠',
    image: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=1200&h=400&fit=crop',
    action: () => router.push('/books?sort=discount')
  }
])

// 分类数据
const categories = ref([
  {
    id: 1,
    name: '计算机科学',
    description: '编程、算法、人工智能',
    icon: 'Monitor'
  },
  {
    id: 2,
    name: '文学经典',
    description: '小说、散文、诗歌',
    icon: 'Reading'
  },
  {
    id: 3,
    name: '历史人文',
    description: '历史、哲学、文化',
    icon: 'Collection'
  },
  {
    id: 4,
    name: '科学技术',
    description: '科学、技术、工程',
    icon: 'DataAnalysis'
  }
])

// 图书数据（模拟数据，实际应该从API获取）
const popularBooks = ref<Book[]>([])
const newBooks = ref<Book[]>([])
const lowStockBooks = ref<Book[]>([])

// 页面加载时获取数据
onMounted(async () => {
  // 这里应该调用API获取实际数据
  // 暂时使用模拟数据
  popularBooks.value = [
    {
      book_id: 1,
      title: '深入理解计算机系统',
      author1: 'Randal E. Bryant',
      publisher: '机械工业出版社',
      price: 139.00,
      stock_quantity: 25,
      low_quantity_flag: 0,
      created_at: '2024-01-01',
      updated_at: '2024-01-01'
    },
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

  newBooks.value = [
    {
      book_id: 3,
      title: 'Vue.js设计与实现',
      author1: '霍春阳',
      publisher: '人民邮电出版社',
      price: 89.00,
      stock_quantity: 15,
      low_quantity_flag: 0,
      created_at: '2024-01-15',
      updated_at: '2024-01-15'
    }
  ]

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
})

const browseByCategory = (category: any) => {
  router.push({
    path: '/books',
    query: { category: category.name }
  })
}
</script>

<style scoped lang="scss">
.home-page {
  .banner {
    margin-bottom: 40px;

    .banner-content {
      height: 400px;
      background-size: cover;
      background-position: center;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.4);
      }
    }

    .banner-text {
      position: relative;
      z-index: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: flex-start;
      padding: 0 100px;
      color: white;

      h1 {
        font-size: 48px;
        margin-bottom: 16px;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
      }

      p {
        font-size: 24px;
        margin-bottom: 32px;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
      }
    }
  }

  .categories {
    margin-bottom: 40px;

    .category-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 24px;
    }

    .category-card {
      background: white;
      border-radius: 8px;
      padding: 24px;
      text-align: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      }

      .el-icon {
        color: #409EFF;
        margin-bottom: 16px;
      }

      h3 {
        margin: 0 0 8px 0;
        font-size: 18px;
        color: #333;
      }

      p {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
    }
  }

  .popular-books,
  .new-books,
  .low-stock {
    margin-bottom: 40px;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .section-title {
    font-size: 28px;
    font-weight: bold;
    color: #333;
    margin: 0;
  }

  .books-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .home-page {
    .banner {
      .banner-text {
        padding: 0 20px;

        h1 {
          font-size: 32px;
        }

        p {
          font-size: 18px;
        }
      }
    }

    .categories {
      .category-grid {
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 16px;
      }
    }

    .books-grid {
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 16px;
    }

    .section-title {
      font-size: 24px;
    }
  }
}
</style>
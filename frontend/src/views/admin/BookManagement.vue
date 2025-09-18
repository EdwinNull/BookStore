<template>
  <div class="book-management">
    <div class="page-header">
      <h2>图书管理</h2>
      <el-button type="primary" @click="$router.push('/admin/books/add')">
        <el-icon><Plus /></el-icon>
        添加图书
      </el-button>
    </div>

    <div class="content-card">
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="书名">
            <el-input v-model="searchForm.title" placeholder="请输入书名" clearable />
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
          </el-form-item>
          <el-form-item label="出版社">
            <el-input v-model="searchForm.publisher" placeholder="请输入出版社" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="books" style="width: 100%">
        <el-table-column prop="book_id" label="ID" width="80" />
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author1" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock_quantity" label="库存" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stock_quantity < 10 ? 'danger' : 'success'">
              {{ row.stock_quantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/books/${row.book_id}`)">
              查看
            </el-button>
            <el-button link type="primary" @click="$router.push(`/admin/books/edit/${row.book_id}`)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { Book } from '@/types'

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
  publisher: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 图书列表
const books = ref<Book[]>([])

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchBooks()
}

// 重置搜索
const resetSearch = () => {
  searchForm.title = ''
  searchForm.author = ''
  searchForm.publisher = ''
  handleSearch()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchBooks()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchBooks()
}

// 删除图书
const handleDelete = async (book: Book) => {
  try {
    await ElMessageBox.confirm(`确定要删除图书"${book.title}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除图书
    ElMessage.success('删除成功')
    fetchBooks()
  } catch {
    // 用户取消操作
  }
}

// 获取图书列表
const fetchBooks = () => {
  // 这里应该调用API获取图书数据
  // 暂时使用模拟数据
  books.value = [
    {
      book_id: 1,
      title: '深入理解计算机系统',
      author1: 'Randal E. Bryant',
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
      publisher: '机械工业出版社',
      price: 128.00,
      stock_quantity: 8,
      low_quantity_flag: 1,
      created_at: '2024-01-10 15:45:00',
      updated_at: '2024-01-10 15:45:00'
    }
  ]

  pagination.total = books.value.length
}

onMounted(() => {
  fetchBooks()
})
</script>

<style scoped lang="scss">
.book-management {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

  .pagination {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
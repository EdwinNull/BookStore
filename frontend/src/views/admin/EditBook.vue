<template>
  <div class="edit-book">
    <div class="page-header">
      <h2>编辑图书</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div class="content-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        size="large"
      >
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>

        <el-form-item label="作者" prop="author1">
          <el-input v-model="form.author1" placeholder="第一作者" />
        </el-form-item>

        <el-form-item label="其他作者">
          <el-input v-model="form.author2" placeholder="第二作者" />
          <el-input v-model="form.author3" placeholder="第三作者" />
          <el-input v-model="form.author4" placeholder="第四作者" />
        </el-form-item>

        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="库存数量" prop="stock_quantity">
          <el-input-number
            v-model="form.stock_quantity"
            :min="0"
            :max="9999"
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            保存
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { Book } from '@/types'

const route = useRoute()
const router = useRouter()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<Partial<Book>>({
  title: '',
  author1: '',
  author2: '',
  author3: '',
  author4: '',
  publisher: '',
  price: 0,
  stock_quantity: 0
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' }
  ],
  author1: [
    { required: true, message: '请输入第一作者', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  stock_quantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // 这里应该调用API更新图书
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('图书更新成功')
    router.push('/admin/books')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取图书信息
const fetchBook = async () => {
  const bookId = parseInt(route.params.id as string)

  if (!bookId) {
    ElMessage.error('图书ID无效')
    router.push('/admin/books')
    return
  }

  try {
    // 这里应该调用API获取图书信息
    // 暂时使用模拟数据
    form.title = '深入理解计算机系统'
    form.author1 = 'Randal E. Bryant'
    form.publisher = '机械工业出版社'
    form.price = 139.00
    form.stock_quantity = 25
  } catch (error) {
    ElMessage.error('获取图书信息失败')
    router.push('/admin/books')
  }
}

onMounted(() => {
  fetchBook()
})
</script>

<style scoped lang="scss">
.edit-book {
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
    max-width: 800px;
  }
}
</style>
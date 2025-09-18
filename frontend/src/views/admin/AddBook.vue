<template>
  <div class="add-book">
    <div class="page-header">
      <h2>添加图书</h2>
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

        <el-form-item label="关键字">
          <el-input
            v-model="form.keywords"
            type="textarea"
            :rows="2"
            placeholder="请输入关键字，用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="目录">
          <el-input
            v-model="form.table_of_contents"
            type="textarea"
            :rows="4"
            placeholder="请输入图书目录"
          />
        </el-form-item>

        <el-form-item label="存储位置">
          <el-input v-model="form.storage_location" placeholder="请输入存储位置" />
        </el-form-item>

        <el-form-item label="供应商">
          <el-select v-model="form.supplier_id" placeholder="请选择供应商">
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.supplier_id"
              :label="supplier.name"
              :value="supplier.supplier_id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="系列">
          <el-select v-model="form.series_id" placeholder="请选择系列">
            <el-option
              v-for="series in seriesList"
              :key="series.series_id"
              :label="series.series_name"
              :value="series.series_id"
            />
          </el-select>
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
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { Book } from '@/types'

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
  stock_quantity: 0,
  keywords: '',
  table_of_contents: '',
  storage_location: '',
  supplier_id: undefined,
  series_id: undefined
})

const suppliers = ref([])
const seriesList = ref([])

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

    // 这里应该调用API添加图书
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('图书添加成功')
    router.push('/admin/books')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取供应商列表
const fetchSuppliers = () => {
  // 这里应该调用API获取供应商数据
  suppliers.value = [
    { supplier_id: 1, name: '清华大学出版社' },
    { supplier_id: 2, name: '人民邮电出版社' },
    { supplier_id: 3, name: '机械工业出版社' }
  ]
}

// 获取系列列表
const fetchSeries = () => {
  // 这里应该调用API获取系列数据
  seriesList.value = [
    { series_id: 1, series_name: '计算机科学与技术' },
    { series_id: 2, series_name: '文学经典' },
    { series_id: 3, series_name: '历史与文化' }
  ]
}

onMounted(() => {
  fetchSuppliers()
  fetchSeries()
})
</script>

<style scoped lang="scss">
.add-book {
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
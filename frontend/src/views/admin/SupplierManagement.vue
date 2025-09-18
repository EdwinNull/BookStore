<template>
  <div class="supplier-management">
    <div class="page-header">
      <h2>›”F¡</h2>
    </div>

    <div class="content-card">
      <el-table :data="suppliers" style="width: 100%">
        <el-table-column prop="supplier_id" label="ID" width="80" />
        <el-table-column prop="name" label="›”Fð" />
        <el-table-column prop="address" label="0@" />
        <el-table-column prop="contact_info" label="Tû¹" />
        <el-table-column prop="created_at" label="úöô" width="160" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Supplier } from '@/types'

const suppliers = ref<Supplier[]>([])

const fetchSuppliers = () => {
  suppliers.value = [
    {
      supplier_id: 1,
      name: 'N'fúH>',
      address: '¬wÀ:N'f',
      contact_info: '5Ý010-62770175',
      created_at: '2024-01-01 10:00:00',
      updated_at: '2024-01-01 10:00:00'
    },
    {
      supplier_id: 2,
      name: 'º®5úH>',
      address: '¬Î:¹W5÷',
      contact_info: '5Ý010-81055000',
      created_at: '2024-01-01 10:00:00',
      updated_at: '2024-01-01 10:00:00'
    }
  ]
}

onMounted(() => {
  fetchSuppliers()
})
</script>

<style scoped lang="scss">
.supplier-management {
  .page-header {
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
}
</style>
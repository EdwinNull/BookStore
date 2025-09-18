<template>
  <div class="user-management">
    <div class="page-header">
      <h2>(7¡</h2>
    </div>

    <div class="content-card">
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="user_id" label="ID" width="80" />
        <el-table-column prop="username" label="(7" width="120" />
        <el-table-column prop="name" label="Ó" width="120" />
        <el-table-column prop="account_balance" label="Y" width="120">
          <template #default="{ row }">
            ¥{{ row.account_balance?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="credit_level" label="á(I§" width="100" />
        <el-table-column prop="role" label="Òr" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">
              {{ row.role === 'admin' ? '¡X' : '(7' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="èŒöô" width="160" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { User } from '@/types'

const users = ref<User[]>([])

const fetchUsers = () => {
  users.value = [
    {
      user_id: 1,
      username: 'admin',
      password: '',
      name: 'ûß¡X',
      account_balance: 1000.00,
      credit_level: 5,
      role: 'admin',
      discount: 0.75,
      over_balance: 999999.99,
      created_at: '2024-01-01 10:00:00',
      updated_at: '2024-01-01 10:00:00'
    },
    {
      user_id: 2,
      username: 'user001',
      password: '',
      name: ' 	',
      account_balance: 200.50,
      credit_level: 2,
      role: 'user',
      discount: 0.85,
      over_balance: 0.00,
      created_at: '2024-01-05 14:30:00',
      updated_at: '2024-01-05 14:30:00'
    }
  ]
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped lang="scss">
.user-management {
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
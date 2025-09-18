<template>
  <header class="header">
    <div class="header-container">
      <div class="logo">
        <router-link to="/" class="logo-link">
          <el-icon><Shop /></el-icon>
          <span>书店管理系统</span>
        </router-link>
      </div>

      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索图书..."
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <div class="nav-menu">
        <el-menu
          mode="horizontal"
          :router="true"
          class="nav-menu-desktop"
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/books">图书浏览</el-menu-item>
        </el-menu>

        <div class="user-actions">
          <!-- 购物车 -->
          <el-badge
            :value="cartStore.totalItems"
            :hidden="cartStore.totalItems === 0"
            class="cart-badge"
          >
            <el-button
              type="primary"
              circle
              @click="showCart = true"
            >
              <el-icon><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>

          <!-- 用户菜单 -->
          <template v-if="authStore.isAuthenticated">
            <el-dropdown @command="handleUserCommand">
              <el-avatar :size="40" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><List /></el-icon>
                    我的订单
                  </el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isAdmin" command="admin">
                    <el-icon><Setting /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </div>

    <!-- 购物车抽屉 -->
    <el-drawer
      v-model="showCart"
      title="购物车"
      direction="rtl"
      size="400px"
    >
      <CartDrawer @close="showCart = false" />
    </el-drawer>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Shop,
  Search,
  ShoppingCart,
  User,
  List,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import CartDrawer from '@/components/common/CartDrawer.vue'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const searchQuery = ref('')
const showCart = ref(false)

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/books',
      query: { title: searchQuery.value.trim() }
    })
  }
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    case 'logout':
      authStore.logout()
      break
  }
}
</script>

<style scoped lang="scss">
.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  .logo-link {
    display: flex;
    align-items: center;
    text-decoration: none;
    color: #409EFF;
    font-size: 20px;
    font-weight: bold;

    .el-icon {
      margin-right: 8px;
      font-size: 24px;
    }
  }
}

.search-bar {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
}

.nav-menu {
  display: flex;
  align-items: center;

  .nav-menu-desktop {
    border-bottom: none;
    margin-right: 20px;
  }
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;

  .cart-badge {
    margin-right: 12px;
  }

  .user-avatar {
    cursor: pointer;
    background: #409EFF;
    color: white;
  }
}

@media (max-width: 768px) {
  .header-container {
    flex-wrap: wrap;
    height: auto;
    padding: 10px;
  }

  .search-bar {
    order: 3;
    flex-basis: 100%;
    margin: 10px 0 0 0;
    max-width: none;
  }

  .nav-menu {
    order: 2;
  }

  .logo {
    order: 1;
  }
}
</style>
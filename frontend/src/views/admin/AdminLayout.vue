<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <el-icon><Setting /></el-icon>
          <span v-show="!isCollapsed">管理后台</span>
        </div>
        <div class="collapse-btn" @click="toggleSidebar">
          <el-icon>
            <component :is="isCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapsed"
        :router="true"
        background-color="#2c3e50"
        text-color="#ecf0f1"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>仪表板</template>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon><Management /></el-icon>
            <span>图书管理</span>
          </template>
          <el-menu-item index="/admin/books">图书列表</el-menu-item>
          <el-menu-item index="/admin/books/add">添加图书</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="2">
          <template #title>
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/admin/orders">订单列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/users">用户列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="4">
          <template #title>
            <el-icon><Office /></el-icon>
            <span>供应商管理</span>
          </template>
          <el-menu-item index="/admin/suppliers">供应商列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="5">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/admin/purchases">采购订单</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="6">
          <template #title>
            <el-icon><Collection /></el-icon>
            <span>系列管理</span>
          </template>
          <el-menu-item index="/admin/series">系列列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="navbar-right">
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ authStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="page-content">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Setting,
  DataBoard,
  Management,
  List,
  User,
  Office,
  ShoppingCart,
  Collection,
  Expand,
  Fold,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 当前页面标题
const currentPageTitle = computed(() => {
  return route.meta.title || '未知页面'
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 处理用户命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      authStore.logout()
      break
  }
}

// 监听路由变化
watch(
  () => route.path,
  () => {
    // 可以在这里添加路由变化时的逻辑
  }
)
</script>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

.sidebar {
  width: 260px;
  background: #2c3e50;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  &.collapsed {
    width: 64px;

    .sidebar-header {
      padding: 0 16px;
    }

    .logo {
      span {
        display: none;
      }
    }
  }

  .sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    background: #34495e;
    border-bottom: 1px solid #4a5f7a;

    .logo {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #409EFF;
      font-size: 16px;
      font-weight: bold;

      .el-icon {
        font-size: 20px;
      }

      span {
        white-space: nowrap;
      }
    }

    .collapse-btn {
      cursor: pointer;
      color: #ecf0f1;
      padding: 4px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }

  .sidebar-menu {
    flex: 1;
    border: none;

    :deep(.el-menu) {
      border: none;
    }

    :deep(.el-menu-item) {
      &:hover {
        background: #34495e;
      }

      &.is-active {
        background: #34495e;
      }
    }

    :deep(.el-sub-menu) {
      .el-sub-menu__title {
        &:hover {
          background: #34495e;
        }
      }
    }
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .navbar-left {
    flex: 1;
  }

  .navbar-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background: #f5f5f5;
      }

      .user-avatar {
        background: #409EFF;
        color: white;
      }

      .username {
        color: #333;
        font-size: 14px;
      }
    }
  }
}

.page-content {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

@media (max-width: 768px) {
  .admin-layout {
    .sidebar {
      position: fixed;
      left: 0;
      top: 0;
      height: 100vh;
      z-index: 1000;
      transform: translateX(-100%);
      transition: transform 0.3s;

      &.collapsed {
        transform: translateX(0);
      }
    }

    .main-content {
      margin-left: 0;
    }
  }
}
</style>
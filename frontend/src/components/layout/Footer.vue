<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-section">
        <h3>关于我们</h3>
        <p>书店管理系统致力于为用户提供优质的图书购买体验，拥有丰富的图书资源和完善的售后服务。</p>
      </div>

      <div class="footer-section">
        <h3>快速链接</h3>
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/books">图书浏览</router-link></li>
          <li><router-link to="/orders">我的订单</router-link></li>
          <li><router-link to="/profile">个人信息</router-link></li>
        </ul>
      </div>

      <div class="footer-section">
        <h3>客户服务</h3>
        <ul>
          <li><a href="#">帮助中心</a></li>
          <li><a href="#">联系我们</a></li>
          <li><a href="#">退换货政策</a></li>
          <li><a href="#">配送说明</a></li>
        </ul>
      </div>

      <div class="footer-section">
        <h3>联系信息</h3>
        <div class="contact-info">
          <p><el-icon><Phone /></el-icon> 400-123-4567</p>
          <p><el-icon><Message /></el-icon> service@bookstore.com</p>
          <p><el-icon><Location /></el-icon> 北京市朝阳区某某街道123号</p>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <div class="footer-bottom-content">
        <p>&copy; 2024 书店管理系统. All rights reserved.</p>
        <div class="footer-links">
          <a href="#">隐私政策</a>
          <a href="#">服务条款</a>
          <a href="#">网站地图</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { Phone, Message, Location } from '@element-plus/icons-vue'
</script>

<style scoped lang="scss">
.footer {
  background: #2c3e50;
  color: white;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
}

.footer-section {
  h3 {
    color: #409EFF;
    margin-bottom: 16px;
    font-size: 18px;
  }

  p {
    line-height: 1.6;
    color: #bdc3c7;
    margin-bottom: 12px;
  }

  ul {
    list-style: none;
    padding: 0;

    li {
      margin-bottom: 8px;

      a {
        color: #bdc3c7;
        text-decoration: none;
        transition: color 0.3s;

        &:hover {
          color: #409EFF;
        }
      }
    }
  }
}

.contact-info {
  p {
    display: flex;
    align-items: center;
    gap: 8px;

    .el-icon {
      color: #409EFF;
    }
  }
}

.footer-bottom {
  background: #1a252f;
  padding: 20px 0;
  margin-top: 40px;
}

.footer-bottom-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.footer-links {
  display: flex;
  gap: 20px;

  a {
    color: #bdc3c7;
    text-decoration: none;
    font-size: 14px;
    transition: color 0.3s;

    &:hover {
      color: #409EFF;
    }
  }
}

@media (max-width: 768px) {
  .footer-container {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .footer-bottom-content {
    flex-direction: column;
    text-align: center;
  }

  .contact-info {
    p {
      justify-content: center;
    }
  }
}
</style>
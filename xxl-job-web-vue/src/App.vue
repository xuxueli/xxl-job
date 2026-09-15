<template>
  <div id="app-container">
    <!-- 登录页面：无 Header 全屏显示 -->
    <template v-if="$route.name === 'Login'">
      <router-view />
    </template>

    <!-- 其他页面：Header + 内容布局 -->
    <template v-else>
      <div class="app-header">
        <span class="app-logo">XXL-JOB 调度中心</span>
        <div class="app-nav">
          <router-link to="/jobinfo">任务管理</router-link>
          <router-link to="/jobquery">定时任务查询</router-link>
          <router-link to="/joblog">执行记录查询</router-link>
          <router-link to="/dashboard">运行报表</router-link>
          <router-link to="/jobgroup">执行器管理</router-link>
          <router-link to="/user">用户管理</router-link>
        </div>
        <div class="app-user">
          <el-button type="text" style="color: #fff" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
      <div class="app-body">
        <router-view />
      </div>
    </template>
  </div>
</template>

<script>
import { logout } from '@/api/login'

export default {
  name: 'App',

  methods: {
    handleLogout: function () {
      var self = this
      logout().finally(function () {
        sessionStorage.removeItem('xxl-job-login')
        self.$router.replace({ path: '/login' })
      })
    }
  }
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  background-color: #f5f7fa;
}

#app-container {
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  background: #304156;
  color: #fff;
}

.app-logo {
  font-size: 18px;
  font-weight: 600;
  margin-right: 40px;
}

.app-nav {
  flex: 1;
}

.app-nav a {
  color: #bfcbd9;
  text-decoration: none;
  margin-right: 24px;
  font-size: 14px;
}

.app-nav a.router-link-active {
  color: #409eff;
}

.app-user {
  margin-left: auto;
}
</style>

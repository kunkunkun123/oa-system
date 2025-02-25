<template>
  <el-container class="app-wrapper">
    <el-aside width="200px" class="sidebar-container">
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router>
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/tasks">
          <i class="el-icon-s-order"></i>
          <span>任务管理</span>
        </el-menu-item>
        
        <el-menu-item index="/leave">
          <i class="el-icon-date"></i>
          <span>请假管理</span>
        </el-menu-item>
        
        <el-menu-item index="/messages">
          <i class="el-icon-message"></i>
          <span>消息中心</span>
        </el-menu-item>
        
        <el-menu-item v-if="isAdmin" index="/departments">
          <i class="el-icon-office-building"></i>
          <span>部门管理</span>
        </el-menu-item>
        
        <el-menu-item v-if="isAdmin" index="/users">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/announcements">
          <i class="el-icon-bell"></i>
          <span>公告管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header height="60px" class="navbar">
        <div class="right-menu">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              {{ currentUser.name }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',
  
  computed: {
    ...mapGetters([
      'currentUser',
      'isAdmin'
    ])
  },
  
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  height: 100vh;
  
  .sidebar-container {
    background-color: #304156;
    
    .el-menu {
      border: none;
    }
  }
  
  .navbar {
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0,21,41,.08);
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 0 20px;
    
    .right-menu {
      .el-dropdown-link {
        cursor: pointer;
        color: #303133;
        
        &:hover {
          color: #409EFF;
        }
      }
    }
  }
  
  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style> 
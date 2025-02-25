<template>
  <div class="dashboard">
    <div v-loading="loading" element-loading-text="加载中...">
      <!-- 欢迎信息 -->
      <div class="welcome-section">
        <h2>
          <i class="el-icon-sunny"></i>
          {{ greeting }}，{{ currentUser.name }}
        </h2>
        <p class="welcome-text">欢迎使用办公自动化系统</p>
      </div>

      <el-row :gutter="20">
        <el-col :span="4">
          <el-card class="box-card message-card">
            <div class="card-content">
              <div class="stat-title">未读消息</div>
              <div class="stat-value">{{ statistics.unreadMessages }}</div>
              <div class="stat-action">
                <el-button type="text" class="message-btn" @click="goToMessages">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card class="box-card monthly-card">
            <div class="card-content">
              <div class="stat-title">本月任务</div>
              <div class="stat-value">{{ statistics.monthlyTasks }}</div>
              <div class="stat-action">
                <el-button type="text" class="monthly-btn" @click="goToTasks('monthly')">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card class="box-card pending-card">
            <div class="card-content">
              <div class="stat-title">待处理任务</div>
              <div class="stat-value">{{ statistics.pendingTasks }}</div>
              <div class="stat-action">
                <el-button type="text" class="pending-btn" @click="goToTasks('pending')">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card class="box-card completed-card">
            <div class="card-content">
              <div class="stat-title">已完成任务</div>
              <div class="stat-value">{{ statistics.completedTasks }}</div>
              <div class="stat-action">
                <el-button type="text" class="completed-btn" @click="goToTasks('completed')">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="box-card dept-card">
            <div class="dept-content" v-if="department">
              <div class="dept-header">
                <i class="el-icon-office-building"></i>
                <span class="dept-name">{{ department.deptName }}</span>
              </div>
              <div class="dept-info-row">
                <div class="info-item">
                  <i class="el-icon-user"></i>
                  <span>{{ department.manager ? department.manager.name : '暂无' }}</span>
                  <div class="info-label">部门经理</div>
                </div>
                <div class="info-item">
                  <i class="el-icon-user-solid"></i>
                  <span>{{ departmentUsers.length }}</span>
                  <div class="info-label">部门人数</div>
                </div>
              </div>
            </div>
            <div v-else class="empty-text">暂无部门信息</div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 任务和工作区域 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="16">
          <el-card class="box-card">
            <div slot="header">
              <span><i class="el-icon-data-analysis"></i> 工作概览</span>
            </div>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="chart-container">
                  <el-progress type="circle" :percentage="completionRate" :width="120" :stroke-width="10" :color="customColors">
                    <template #default="{ percentage }">
                      <span class="progress-label">任务完成率<br/>{{ percentage }}%</span>
                    </template>
                  </el-progress>
                </div>
              </el-col>
              <el-col :span="16">
                <div class="task-trends">
                  <h4><i class="el-icon-time"></i> 近期任务</h4>
                  <div class="task-list">
                    <div v-for="task in recentTasks" :key="task.taskId" class="task-item">
                      <span class="task-title">{{ task.title }}</span>
                      <el-tag size="mini" :type="getStatusType(task.status)">
                        {{ getStatusText(task.status) }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="box-card">
            <div slot="header">
              <span><i class="el-icon-bell"></i> 系统公告</span>
            </div>
            <div class="notice-list">
              <div v-for="notice in announcements" :key="notice.announcementId" class="notice-item">
                <el-tooltip effect="dark" :content="formatDate(notice.createTime)" placement="right">
                  <div class="notice-title-row" @click="showNoticeDetail(notice)">
                    <i class="el-icon-info"></i>
                    <span class="notice-title">{{ notice.title }}</span>
                  </div>
                </el-tooltip>
              </div>
            </div>
            <div v-if="!announcements.length" class="empty-text">暂无公告</div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 工作日历区域 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card class="box-card">
            <div slot="header">
              <span><i class="el-icon-date"></i> 工作日历</span>
            </div>
            <div class="calendar-container">
              <div class="upcoming-tasks">
                <h4>待办事项</h4>
                <el-timeline>
                  <el-timeline-item
                    v-for="task in upcomingTasks"
                    :key="task.taskId"
                    :type="getTimelineItemType(task.dueDate)"
                    :timestamp="task.dueDate">
                    {{ task.title }}
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="box-card" v-loading="loading">
            <div slot="header">
              <span>本月任务统计</span>
            </div>
            <div class="chart-container" v-if="monthlyTaskStats">
              <div class="task-stats">
                <div class="total-tasks">
                  总任务数: {{ monthlyTaskStats.total || 0 }}
                </div>
                <div class="status-tasks">
                  <div>待处理: {{ monthlyTaskStats.pending || 0 }}</div>
                  <div>进行中: {{ monthlyTaskStats.inProgress || 0 }}</div>
                  <div>已完成: {{ monthlyTaskStats.completed || 0 }}</div>
                </div>
                <div class="completion-rate">
                  完成率: {{ ((monthlyTaskStats.completionRate || 0) * 100).toFixed(1) }}%
                </div>
              </div>
              <div class="progress-circle">
                <el-progress type="circle" 
                  :percentage="(monthlyTaskStats.completionRate || 0) * 100"
                  :width="150">
                </el-progress>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      :title="currentNotice.title"
      :visible.sync="noticeDialogVisible"
      width="500px"
      custom-class="notice-dialog">
      <div class="notice-detail">
        <div class="notice-meta">
          <span>发布时间：{{ formatDate(currentNotice.createTime) }}</span>
          <span v-if="currentNotice.creator">发布人：{{ currentNotice.creator.name }}</span>
        </div>
        <div class="notice-content">{{ currentNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
import { formatDate } from '../utils/date'
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      tasks: [],
      messages: [],
      department: null,
      departmentUsers: [],
      loading: true,
      statistics: {
        unreadMessages: 0,
        monthlyTasks: 0,
        pendingTasks: 0,
        completedTasks: 0,
        totalTasks: 0
      },
      announcements: [],
      noticeDialogVisible: false,
      currentNotice: {
        title: '',
        content: '',
        createTime: null,
        creator: null
      },
      monthlyTaskStats: null
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    completionRate() {
      if (this.statistics.totalTasks === 0) return 0
      return Math.round((this.statistics.completedTasks / this.statistics.totalTasks) * 100)
    },
    customColors() {
      return [
        {color: '#f56c6c', percentage: 20},
        {color: '#e6a23c', percentage: 40},
        {color: '#5cb87a', percentage: 60},
        {color: '#1989fa', percentage: 80},
        {color: '#67c23a', percentage: 100}
      ]
    },
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 17) return '下午好'
      if (hour < 19) return '傍晚好'
      return '晚上好'
    },
    recentTasks() {
      return this.tasks
        .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        .slice(0, 5)
    },
    upcomingTasks() {
      return this.tasks
        .filter(task => task.status !== 2)
        .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
        .slice(0, 5)
    }
  },
  async created() {
    await this.fetchData()
  },
  methods: {
    formatDate,
    goToMessages() {
      this.$router.push('/messages')
    },
    goToTasks(filter) {
      this.$router.push({
        path: '/tasks',
        query: { filter }
      })
    },
    async fetchData() {
      this.loading = true
      try {
        console.log('开始获取数据...')
        const [tasks, messages] = await Promise.all([
          api.getMyTasks(),
          api.getMessages()
        ])
        
        console.log('获取到的任务:', tasks)
        console.log('获取到的消息:', messages)
        
        this.tasks = tasks || []
        this.messages = messages || []
        
        // 获取当前月份
        const now = new Date()
        const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
        
        // 统计数据
        this.statistics = {
          totalTasks: tasks.length,
          monthlyTasks: tasks.filter(task => task.dueDate && task.dueDate.startsWith(currentMonth)).length,
          pendingTasks: tasks.filter(task => task && task.status === 0).length,
          completedTasks: tasks.filter(task => task && task.status === 2).length,
          unreadMessages: messages.filter(msg => msg && !msg.isRead).length
        }
        
        console.log('当前用户:', this.currentUser)
        if (this.currentUser?.deptId) {
          const [dept, users] = await Promise.all([
            api.getDepartments(),
            api.getUsers()
          ])
          
          console.log('获取到的部门:', dept)
          console.log('获取到的用户:', users)
          
          this.department = dept.find(d => d.deptId === this.currentUser.deptId)
          this.departmentUsers = users.filter(u => u.deptId === this.currentUser.deptId)
          
          console.log('当前部门:', this.department)
          console.log('部门用户:', this.departmentUsers)
        }
        
        const announcements = await api.getAnnouncements()
        this.announcements = announcements

        // 获取本月任务统计
        const response = await api.getMonthlyTaskStats()
        this.monthlyTaskStats = response.data
      } catch (error) {
        console.error('获取数据失败:', error)
        console.error('错误详情:', error.response?.data || error.message)
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    getTimelineItemType(dueDate) {
      const today = new Date()
      const dueDay = new Date(dueDate)
      const diffDays = Math.ceil((dueDay - today) / (1000 * 60 * 60 * 24))
      
      if (diffDays < 0) return 'danger'
      if (diffDays < 3) return 'warning'
      return 'primary'
    },
    getStatusText(status) {
      if (status === null || status === undefined) return '未知'
      const texts = {
        0: '待处理',
        1: '进行中',
        2: '已完成'
      }
      return texts[status] ?? '未知'
    },
    getStatusType(status) {
      if (status === null || status === undefined) return ''
      const types = {
        0: 'info',
        1: 'warning',
        2: 'success'
      }
      return types[status] ?? ''
    },
    showNoticeDetail(notice) {
      this.currentNotice = notice
      this.noticeDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  background-color: #f5f7fa;
}

.box-card {
  margin-bottom: 20px;
  height: 100%;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  }
}

.message-card {
  background: linear-gradient(135deg, #e0f2f1 0%, #b2dfdb 100%);
  .stat-value { color: #009688; }
  .message-btn { color: #00796b; }
}

.monthly-card {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  .stat-value { color: #1976d2; }
  .monthly-btn { color: #1565c0; }
}

.pending-card {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  .stat-value { color: #f57c00; }
  .pending-btn { color: #ef6c00; }
}

.completed-card {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  .stat-value { color: #43a047; }
  .completed-btn { color: #2e7d32; }
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

.stat-title {
  font-size: 16px;
  color: #37474f;
  margin-bottom: 15px;
  font-weight: 500;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 15px;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.stat-action {
  margin-top: 10px;
  
  .el-button {
    font-weight: 500;
    &:hover {
      opacity: 0.8;
    }
  }
}

.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  min-height: 200px;
}

.progress-label {
  font-size: 14px;
  text-align: center;
  color: #606266;
  line-height: 1.5;
  font-weight: 500;
}

.dept-card {
  background: linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%);
  
  .dept-content {
    padding: 10px;
  }
  
  .dept-header {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    
    i {
      font-size: 24px;
      color: #7b1fa2;
      margin-right: 10px;
    }
    
    .dept-name {
      font-size: 18px;
      font-weight: 500;
      color: #4a148c;
    }
  }
  
  .dept-info-row {
    display: flex;
    justify-content: space-around;
    
    .info-item {
      text-align: center;
      padding: 10px;
      
      i {
        font-size: 24px;
        color: #7b1fa2;
        margin-bottom: 5px;
      }
      
      span {
        display: block;
        font-size: 16px;
        font-weight: 500;
        color: #4a148c;
        margin: 5px 0;
      }
      
      .info-label {
        font-size: 12px;
        color: #6a1b9a;
      }
    }
  }
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.welcome-section {
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  border-radius: 8px;
  color: white;
  
  h2 {
    margin: 0;
    font-size: 24px;
    display: flex;
    align-items: center;
    
    i {
      margin-right: 10px;
      font-size: 28px;
    }
  }
  
  .welcome-text {
    margin: 10px 0 0;
    opacity: 0.8;
  }
}

.task-trends {
  padding: 0 20px;
  
  h4 {
    margin: 0 0 15px;
    color: #606266;
    
    i {
      margin-right: 5px;
    }
  }
  
  .task-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .task-title {
      flex: 1;
      margin-right: 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.notice-list {
  .notice-item {
    padding: 8px 0;
    border-bottom: 1px solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .notice-title-row {
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        color: #409EFF;
      }
      
      i {
        color: #409EFF;
        margin-right: 8px;
        font-size: 14px;
      }
      
      .notice-title {
        font-size: 13px;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

.calendar-container {
  padding: 20px;
  
  .upcoming-tasks {
    h4 {
      margin: 0 0 15px;
      color: #606266;
    }
  }
}

.notice-dialog {
  .notice-meta {
    color: #909399;
    font-size: 12px;
    margin-bottom: 15px;
    display: flex;
    justify-content: space-between;
  }
  
  .notice-content {
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

.chart-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 200px;
  
  .task-stats {
    flex: 1;
    
    .status-tasks {
      margin: 10px 0;
      
      > div {
        margin: 5px 0;
        font-size: 14px;
      }
    }
  }
  
  .progress-circle {
    margin-left: 20px;
  }
}
</style> 
<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title"><i class="el-icon-s-order"></i> 任务管理</div>
      <div class="header-actions">
        <el-radio-group v-model="taskFilter" size="small">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="pending">待处理</el-radio-button>
          <el-radio-button label="inProgress">进行中</el-radio-button>
          <el-radio-button label="completed">已完成</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="showCreateDialog" v-if="canCreateTask">
          <i class="el-icon-plus"></i> 创建任务
        </el-button>
      </div>
    </div>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="我的任务" name="my">
        <el-table :data="filteredMyTasks" v-loading="loading">
          <el-table-column label="标题" prop="title">
            <template slot-scope="scope">
              <i class="el-icon-document"></i>
              {{ scope.row.title }}
            </template>
          </el-table-column>
          <el-table-column label="创建人" width="120">
            <template slot-scope="scope">
              <el-tooltip :content="scope.row.creator.name" placement="top">
                <span>{{ scope.row.creator.name }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="截止日期" width="120">
            <template slot-scope="scope">
              {{ formatDate(scope.row.dueDate) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                <i :class="getStatusIcon(scope.row.status)" style="margin-right: 2px"></i>
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button type="text" @click="viewTask(scope.row)">
                <i class="el-icon-view"></i> 查看
              </el-button>
              <el-button 
                type="text" 
                @click="updateStatus(scope.row)"
                v-if="scope.row.status !== 2">
                <i class="el-icon-refresh"></i> {{ scope.row.status === 0 ? '开始' : '完成' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="已创建" name="created" v-if="canCreateTask">
        <el-table :data="filteredCreatedTasks" v-loading="loading">
          <el-table-column label="标题">
            <template slot-scope="scope">
              <i class="el-icon-document"></i>
              {{ scope.row.title }}
            </template>
          </el-table-column>
          <el-table-column label="执行人" width="120">
            <template slot-scope="scope">
              <el-tooltip :content="scope.row.assignee.name" placement="top">
                <span>{{ scope.row.assignee.name }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="截止日期" width="120">
            <template slot-scope="scope">
              {{ formatDate(scope.row.dueDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button type="text" @click="viewTask(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 创建任务对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="taskForm" :rules="rules" ref="taskForm" label-width="80px">
        <el-form-item label="执行人" prop="assigneeId">
          <el-select v-model="taskForm.assigneeId" placeholder="请选择执行人">
            <el-option
              v-for="user in availableUsers"
              :key="user.userId"
              :label="user.name"
              :value="user.userId">
              <span>{{ user.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ getDepartmentName(user.deptId) }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="taskForm.title"></el-input>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="taskForm.content" rows="4"></el-input>
        </el-form-item>
        
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="taskForm.dueDate"
            type="date"
            placeholder="选择日期"
            :picker-options="{ disabledDate: disabledDate }">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTask" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 查看任务对话框 -->
    <el-dialog title="任务详情" :visible.sync="viewDialogVisible" width="500px">
      <div v-if="currentTask">
        <h3>{{ currentTask.title }}</h3>
        <p class="task-meta">
          <span>创建人：{{ currentTask.creator.name }}</span>
          <span>执行人：{{ currentTask.assignee.name }}</span>
        </p>
        <p class="task-meta">
          <span>截止日期：{{ currentTask.dueDate }}</span>
          <span>状态：
            <el-tag :type="getStatusType(currentTask.status)">
              {{ getStatusText(currentTask.status) }}
            </el-tag>
          </span>
        </p>
        <div class="task-content">{{ currentTask.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
import { mapGetters } from 'vuex'
import { formatDate } from '../utils/date'

export default {
  name: 'Tasks',
  data() {
    return {
      taskFilter: this.$route.query.filter || 'all',
      activeTab: 'my',
      loading: false,
      submitting: false,
      dialogVisible: false,
      viewDialogVisible: false,
      myTasks: [],
      createdTasks: [],
      currentTask: null,
      users: [],
      departments: [],
      taskForm: {
        assigneeId: '',
        title: '',
        content: '',
        dueDate: ''
      },
      rules: {
        assigneeId: [
          { required: true, message: '请选择执行人', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ],
        dueDate: [
          { required: true, message: '请选择截止日期', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    canCreateTask() {
      return this.currentUser.roleId <= 3 // 总经理、部门经理可以创建任务
    },
    availableUsers() {
      if (this.currentUser.roleId === 1 || this.currentUser.roleId === 2) {
        // 总经理可以给所有人分配任务
        return this.users.filter(u => u.userId !== this.currentUser.userId)
      } else if (this.currentUser.roleId === 3) {
        // 部门经理只能给本部门员工分配任务
        return this.users.filter(u => 
          u.userId !== this.currentUser.userId && 
          u.deptId === this.currentUser.deptId
        )
      }
      return []
    },
    dialogTitle() {
      return '创建任务'
    },
    filteredMyTasks() {
      return this.filterTasks(this.myTasks, this.taskFilter)
    },
    filteredCreatedTasks() {
      return this.filterTasks(this.createdTasks, this.taskFilter)
    }
  },
  created() {
    this.fetchData()
  },
  watch: {
    '$route.query.filter'(newFilter) {
      this.taskFilter = newFilter || 'all'
    }
  },
  methods: {
    formatDate,
    async fetchData() {
      this.loading = true
      try {
        // 先获取部门信息
        const departments = await api.getDepartments()
        this.departments = departments
        
        // 获取用户信息
        const users = await api.getUsers()
        this.users = users
        
        // 获取任务信息
        const [myTasks, createdTasks] = await Promise.all([
          api.getMyTasks(),
          api.getTasks()
        ])
        
        console.log('我的任务:', myTasks)
        console.log('创建的任务:', createdTasks)
        
        // 处理任务数据，添加关联信息
        const processTask = task => {
          return {
            ...task,
            creator: users.find(u => u.userId === task.creatorId) || { name: '未知' },
            assignee: users.find(u => u.userId === task.assigneeId) || { name: '未知' },
            department: departments.find(d => d.deptId === task.deptId) || { deptName: '未知' }
          }
        }
        
        this.myTasks = Array.isArray(myTasks) 
          ? myTasks.map(processTask)
          : []
        
        this.createdTasks = Array.isArray(createdTasks)
          ? createdTasks
              .filter(t => t.creatorId === this.currentUser.userId)
              .map(processTask)
          : []
      } catch (error) {
        console.error('获取数据失败:', error)
        console.error('错误详情:', error.response?.data || error.message)
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    getStatusType(status) {
      const types = {
        0: 'info',
        1: 'warning',
        2: 'success'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '待处理',
        1: '进行中',
        2: '已完成'
      }
      return texts[status] || '未知'
    },
    getStatusIcon(status) {
      const icons = {
        0: 'el-icon-time',
        1: 'el-icon-loading',
        2: 'el-icon-success'
      }
      return icons[status] || 'el-icon-warning'
    },
    showCreateDialog() {
      this.dialogVisible = true
      this.taskForm = {
        assigneeId: '',
        title: '',
        content: '',
        dueDate: ''
      }
    },
    disabledDate(time) {
      return time.getTime() < Date.now() - 8.64e7 // 不能选择今天之前的日期
    },
    async submitTask() {
      this.$refs.taskForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await api.createTask({
              ...this.taskForm,
              dueDate: this.taskForm.dueDate.toISOString().split('T')[0],
              deptId: this.currentUser.roleId === 3 ? this.currentUser.deptId : null
            })
            this.$message.success('创建成功')
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            this.$message.error('创建失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    viewTask(task) {
      this.currentTask = task
      this.viewDialogVisible = true
    },
    async updateStatus(task) {
      try {
        const newStatus = task.status === 0 ? 1 : 2;
        await api.updateTaskStatus(task.taskId, { status: newStatus });
        task.status = newStatus;
        this.$message.success('更新成功');
      } catch (error) {
        console.error('更新失败:', error);
        this.$message.error('更新失败');
      }
    },
    getDepartmentName(deptId) {
      const dept = this.departments.find(d => d.deptId === deptId)
      return dept ? dept.deptName : '未分配'
    },
    filterTasks(tasks, filter) {
      const now = new Date()
      const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
      
      switch (filter) {
        case 'pending':
          return tasks.filter(task => task.status === 0)
        case 'inProgress':
          return tasks.filter(task => task.status === 1)
        case 'completed':
          return tasks.filter(task => task.status === 2)
        case 'monthly':
          return tasks.filter(task => task.dueDate && task.dueDate.startsWith(currentMonth))
        default:
          return tasks
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.task-meta {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-content {
  margin-top: 20px;
  white-space: pre-wrap;
}

.el-tag {
  display: flex;
  align-items: center;
  justify-content: center;
  
  i {
    margin-right: 4px;
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;

  .el-radio-group {
    margin-right: 16px;
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .title {
    font-size: 18px;
    font-weight: 500;
    display: flex;
    align-items: center;

    i {
      margin-right: 8px;
    }
  }
}
</style> 
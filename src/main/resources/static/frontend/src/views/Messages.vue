<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title"><i class="el-icon-message"></i> 消息管理</div>
      <el-button type="primary" @click="showSendDialog">
        <i class="el-icon-plus"></i> 发送消息
      </el-button>
    </div>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="收件箱" name="inbox">
        <el-table :data="receivedMessages" v-loading="loading">
          <el-table-column prop="title" label="标题">
            <template slot-scope="scope">
              <i class="el-icon-message-solid" v-if="!scope.row.isRead"></i>
              <i class="el-icon-message" v-else></i>
              {{ scope.row.title }}
            </template>
          </el-table-column>
          <el-table-column prop="sender.name" label="发送人" width="120" />
          <el-table-column prop="createTime" label="发送时间" width="160">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="isRead" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isRead ? 'success' : 'warning'">
                {{ scope.row.isRead ? '已读' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button 
                type="text" 
                @click="viewMessage(scope.row)"
                :disabled="scope.row.isRead">
                {{ scope.row.isRead ? '已读' : '查看' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="已发送" name="sent">
        <el-table :data="sentMessages" v-loading="loading">
          <el-table-column prop="title" label="标题">
            <template slot-scope="scope">
              <i class="el-icon-message-solid" v-if="!scope.row.isRead"></i>
              <i class="el-icon-message" v-else></i>
              {{ scope.row.title }}
            </template>
          </el-table-column>
          <el-table-column prop="receiver.name" label="接收人" width="120" />
          <el-table-column prop="createTime" label="发送时间" width="160">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="isRead" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isRead ? 'success' : 'warning'">
                {{ scope.row.isRead ? '已读' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 发送消息对话框 -->
    <el-dialog title="发送消息" :visible.sync="dialogVisible" width="500px">
      <el-form :model="messageForm" :rules="rules" ref="messageForm" label-width="80px">
        <el-form-item label="发送类型">
          <el-radio-group v-model="messageForm.type">
            <el-radio label="single">个人</el-radio>
            <el-radio label="department" v-if="canSendToDepartment">部门</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="接收者" prop="receiverId" v-if="messageForm.type === 'single'">
          <el-select 
            v-model="messageForm.receiverId" 
            placeholder="请选择接收者"
            filterable>
            <el-option
              v-for="user in users"
              :key="user.userId"
              :label="user.name"
              :value="user.userId">
              <span>{{ user.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ user.department?.deptName || '无部门' }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="部门" prop="deptId" v-if="messageForm.type === 'department'">
          <el-select 
            v-model="messageForm.deptId" 
            placeholder="请选择部门"
            filterable>
            <el-option
              v-for="dept in departments"
              :key="dept.deptId"
              :label="dept.deptName"
              :value="dept.deptId">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="messageForm.title"></el-input>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input 
            type="textarea" 
            v-model="messageForm.content" 
            :rows="4">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
      </div>
    </el-dialog>
    
    <!-- 查看消息对话框 -->
    <el-dialog title="消息详情" :visible.sync="viewDialogVisible" width="500px">
      <div v-if="currentMessage">
        <h3>{{ currentMessage.title }}</h3>
        <div class="message-meta">
          <span>发送人：{{ currentMessage.sender.name }}</span>
          <span>{{ formatDate(currentMessage.createTime) }}</span>
        </div>
        <div class="message-content">{{ currentMessage.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '../utils/date'
import { mapGetters } from 'vuex'
import api from '../api'

export default {
  name: 'Messages',
  data() {
    return {
      activeTab: 'inbox',
      loading: false,
      receivedMessages: [],
      sentMessages: [],
      dialogVisible: false,
      viewDialogVisible: false,
      currentMessage: null,
      sending: false,
      users: [],
      departments: [],
      messageForm: {
        type: 'single',
        receiverId: '',
        deptId: '',
        title: '',
        content: ''
      },
      rules: {
        receiverId: [
          { required: true, message: '请选择接收者', trigger: 'change' }
        ],
        deptId: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    canSendToDepartment() {
      return this.currentUser && (this.currentUser.roleId === 1 || this.currentUser.roleId === 3)
    }
  },
  created() {
    this.fetchMessages()
    this.fetchUsers()
    this.fetchDepartments()
  },
  methods: {
    formatDate,
    async fetchMessages() {
      this.loading = true
      try {
        const [received, sent] = await Promise.all([
          api.getMessages(),
          api.getSentMessages()
        ])
        this.receivedMessages = received
        this.sentMessages = sent
      } catch (error) {
        console.error('获取消息失败:', error)
        this.$message.error('获取消息失败')
      } finally {
        this.loading = false
      }
    },
    async fetchUsers() {
      try {
        const users = await api.getUsers()
        this.users = users.filter(u => u.userId !== this.currentUser.userId)
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败')
      }
    },
    async fetchDepartments() {
      try {
        this.departments = await api.getDepartments()
      } catch (error) {
        console.error('获取部门列表失败:', error)
        this.$message.error('获取部门列表失败')
      }
    },
    showSendDialog() {
      this.dialogVisible = true
      this.messageForm = {
        type: 'single',
        receiverId: '',
        deptId: '',
        title: '',
        content: ''
      }
    },
    async sendMessage() {
      this.$refs.messageForm.validate(async valid => {
        if (valid) {
          this.sending = true
          try {
            if (this.messageForm.type === 'single') {
              await api.sendMessage({
                receiverId: this.messageForm.receiverId,
                title: this.messageForm.title,
                content: this.messageForm.content
              })
            } else {
              await api.sendDepartmentMessage(this.messageForm.deptId, {
                title: this.messageForm.title,
                content: this.messageForm.content
              })
            }
            this.$message.success('发送成功')
            this.dialogVisible = false
            this.fetchMessages()
          } catch (error) {
            this.$message.error('发送失败')
          } finally {
            this.sending = false
          }
        }
      })
    },
    viewMessage(message) {
      this.currentMessage = message
      this.viewDialogVisible = true
      if (!message.isRead) {
        this.markAsRead(message)
      }
    },
    async markAsRead(message) {
      if (!message.messageId) {
        console.error('消息ID不存在:', message)
        return
      }
      
      try {
        await api.markMessageAsRead(message.messageId)
        message.isRead = true
      } catch (error) {
        console.error('标记已读失败:', error)
        this.$message.error('标记已读失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.message-meta {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
}

.message-content {
  margin-top: 20px;
  white-space: pre-wrap;
}

.el-select-dropdown__item {
  .department {
    float: right;
    color: #8492a6;
    font-size: 13px;
  }
}
</style> 
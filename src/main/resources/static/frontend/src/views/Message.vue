<template>
  <div class="message-container">
    <el-card class="message-form">
      <h2>发送消息</h2>
      <el-form :model="messageForm" label-width="80px">
        <el-form-item label="接收者">
          <el-select 
            v-model="selectedUser" 
            placeholder="请选择接收者" 
            filterable
            @change="handleReceiverChange"
          >
            <el-option
              v-for="user in users"
              :key="user.userId"
              :label="user.name"
              :value="user"
            >
              <span>{{ user.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ user.department?.deptName || '无部门' }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="标题">
          <el-input v-model="messageForm.title" placeholder="请输入标题"></el-input>
        </el-form-item>

        <el-form-item label="内容">
          <el-input
            type="textarea"
            v-model="messageForm.content"
            :rows="4"
            placeholder="请输入消息内容"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      users: [],
      selectedUser: null,
      messageForm: {
        title: '',
        content: ''
      }
    }
  },
  created() {
    this.fetchUsers()
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await this.$http.get('/api/users')
        this.users = response.data
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败')
      }
    },
    handleReceiverChange(user) {
      this.selectedUser = user
    },
    async sendMessage() {
      try {
        if (!this.selectedUser) {
          this.$message.warning('请选择接收者')
          return
        }
        if (!this.messageForm.title.trim()) {
          this.$message.warning('请输入标题')
          return
        }
        if (!this.messageForm.content.trim()) {
          this.$message.warning('请输入内容')
          return
        }

        const messageData = {
          receiverId: this.selectedUser.userId,
          title: this.messageForm.title.trim(),
          content: this.messageForm.content.trim()
        }

        await this.$http.post('/api/messages', messageData)
        this.$message.success('消息发送成功')
        
        // 重置表单
        this.selectedUser = null
        this.messageForm.title = ''
        this.messageForm.content = ''
      } catch (error) {
        console.error('发送消息失败:', error)
        this.$message.error('发送消息失败')
      }
    }
  }
}
</script>

<style scoped>
.message-container {
  padding: 20px;
}
.message-form {
  max-width: 600px;
  margin: 0 auto;
}
</style> 
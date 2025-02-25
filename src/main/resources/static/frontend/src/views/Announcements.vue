<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title">
        <i class="el-icon-bell"></i> 公告管理
      </div>
      <el-button type="primary" @click="showCreateDialog">
        <i class="el-icon-plus"></i> 发布公告
      </el-button>
    </div>

    <el-table :data="announcements" v-loading="loading">
      <el-table-column label="标题" prop="title" />
      <el-table-column label="内容" prop="content" show-overflow-tooltip />
      <el-table-column label="发布时间" width="160">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已发布' : '已下线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button 
            type="text" 
            @click="updateStatus(scope.row)"
            :disabled="loading">
            {{ scope.row.status === 1 ? '下线' : '发布' }}
          </el-button>
          <el-button 
            type="text" 
            class="delete-btn" 
            @click="deleteAnnouncement(scope.row)"
            :disabled="loading">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑公告对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            type="textarea" 
            v-model="form.content" 
            placeholder="请输入公告内容"
            :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
import { formatDate } from '../utils/date'

export default {
  name: 'Announcements',
  data() {
    return {
      loading: false,
      announcements: [],
      dialogVisible: false,
      submitting: false,
      form: {
        title: '',
        content: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { max: 100, message: '标题最多100个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return '发布公告'
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    formatDate,
    async fetchData() {
      this.loading = true
      try {
        const data = await api.getAnnouncements(true)
        this.announcements = data
      } catch (error) {
        console.error('获取公告失败:', error)
        this.$message.error('获取公告失败')
      } finally {
        this.loading = false
      }
    },
    showCreateDialog() {
      this.form = {
        title: '',
        content: ''
      }
      this.dialogVisible = true
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        
        this.submitting = true
        try {
          await api.createAnnouncement(this.form)
          this.$message.success('发布成功')
          this.dialogVisible = false
          this.fetchData()
        } catch (error) {
          console.error('发布失败:', error)
          this.$message.error('发布失败')
        } finally {
          this.submitting = false
        }
      })
    },
    async updateStatus(announcement) {
      try {
        const newStatus = announcement.status === 1 ? 0 : 1
        await api.updateAnnouncementStatus(announcement.announcementId, newStatus)
        announcement.status = newStatus
        this.$message.success(newStatus === 1 ? '已发布' : '已下线')
      } catch (error) {
        console.error('更新状态失败:', error)
        this.$message.error('操作失败')
      }
    },
    async deleteAnnouncement(announcement) {
      try {
        await this.$confirm('确定要删除该公告吗？', '提示', {
          type: 'warning'
        })
        
        await api.updateAnnouncementStatus(announcement.announcementId, -1)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
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

.delete-btn {
  color: #f56c6c;
  margin-left: 10px;
  
  &:hover {
    color: #f78989;
  }
}
</style> 
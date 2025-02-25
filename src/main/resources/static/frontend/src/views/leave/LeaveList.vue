<template>
  <div class="leave-list">
    <div class="header">
      <h2>请假管理</h2>
      <el-button type="primary" @click="showCreateDialog">申请请假</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="我的请假" name="my">
        <el-table :data="myLeaves" v-loading="loading">
          <el-table-column prop="type" label="请假类型">
            <template slot-scope="scope">
              {{ getLeaveTypeName(scope.row.type) }}
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusName(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button type="text" @click="viewDetail(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="待审批" name="pending" v-if="isManager">
        <el-table :data="pendingLeaves" v-loading="loading">
          <el-table-column prop="applicant.name" label="申请人"/>
          <el-table-column prop="type" label="请假类型">
            <template slot-scope="scope">
              {{ getLeaveTypeName(scope.row.type) }}
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button type="text" @click="viewDetail(scope.row)">查看</el-button>
              <el-button type="text" @click="approve(scope.row, 1)">同意</el-button>
              <el-button type="text" @click="approve(scope.row, 2)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 创建请假对话框 -->
    <el-dialog title="申请请假" :visible.sync="createDialogVisible" width="500px">
      <el-form :model="leaveForm" :rules="rules" ref="leaveForm" label-width="100px">
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="leaveForm.type" placeholder="请选择请假类型">
            <el-option label="事假" :value="1"/>
            <el-option label="病假" :value="2"/>
            <el-option label="年假" :value="3"/>
            <el-option label="调休" :value="4"/>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="leaveForm.startTime"
            type="datetime"
            placeholder="选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="leaveForm.endTime"
            type="datetime"
            placeholder="选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input
            type="textarea"
            v-model="leaveForm.reason"
            :rows="3"
            placeholder="请输入请假原因">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLeave" :loading="submitting">提交</el-button>
      </div>
    </el-dialog>

    <!-- 请假详情对话框 -->
    <el-dialog title="请假详情" :visible.sync="detailDialogVisible" width="600px">
      <div v-if="currentLeave" class="leave-detail">
        <div class="detail-item">
          <span class="label">申请人：</span>
          <span>{{ currentLeave.applicant?.name }}</span>
        </div>
        <div class="detail-item">
          <span class="label">请假类型：</span>
          <span>{{ getLeaveTypeName(currentLeave.type) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">开始时间：</span>
          <span>{{ formatDateTime(currentLeave.startTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">结束时间：</span>
          <span>{{ formatDateTime(currentLeave.endTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">请假原因：</span>
          <span>{{ currentLeave.reason }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(currentLeave.status)">
            {{ getStatusName(currentLeave.status) }}
          </el-tag>
        </div>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog :title="approvalAction === 1 ? '同意请假' : '拒绝请假'"
               :visible.sync="approvalDialogVisible"
               width="500px">
      <el-form :model="approvalForm" ref="approvalForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input
            type="textarea"
            v-model="approvalForm.comment"
            :rows="3"
            placeholder="请输入审批意见">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApproval" :loading="submitting">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import leaveApi from '@/api/leave'
import { formatDate } from '@/utils/date'

export default {
  name: 'LeaveList',
  
  data() {
    return {
      activeTab: 'my',
      loading: false,
      myLeaves: [],
      pendingLeaves: [],
      createDialogVisible: false,
      detailDialogVisible: false,
      approvalDialogVisible: false,
      submitting: false,
      currentLeave: null,
      approvalAction: 1,  // 1: 同意, 2: 拒绝
      
      leaveForm: {
        type: null,
        startTime: null,
        endTime: null,
        reason: ''
      },
      
      approvalForm: {
        comment: ''
      },
      
      rules: {
        type: [{ required: true, message: '请选择请假类型' }],
        startTime: [{ required: true, message: '请选择开始时间' }],
        endTime: [{ required: true, message: '请选择结束时间' }],
        reason: [{ required: true, message: '请输入请假原因' }]
      }
    }
  },
  
  computed: {
    ...mapGetters(['currentUser']),
    isManager() {
      // 修改为同时允许管理员和部门经理查看
      return this.currentUser.roleId === 1 || this.currentUser.roleId === 3
    }
  },
  
  created() {
    // 如果是管理员或部门经理，默认显示待审批标签页
    if (this.currentUser.roleId === 1 || this.currentUser.roleId === 3) {
      this.activeTab = 'pending'
    }
    this.fetchData()
  },
  
  methods: {
    formatDateTime(time) {
      if (!time) return '---'
      return formatDate(time)
    },
    
    async fetchData() {
      this.loading = true
      try {
        if (this.activeTab === 'my') {
          console.log('获取我的请假记录');
          const response = await leaveApi.getMyLeaves();
          console.log('我的请假记录:', response);
          this.myLeaves = response;
        } else if (this.activeTab === 'pending') {
          console.log('获取待审批请假');
          console.log('当前用户:', this.currentUser);
          const response = await leaveApi.getPendingApprovals();
          console.log('待审批请假:', response);
          this.pendingLeaves = response || [];
        }
      } catch (error) {
        console.error('获取请假记录失败:', error);
        this.$message.error('获取请假记录失败');
      } finally {
        this.loading = false;
      }
    },
    
    handleTabClick() {
      console.log('切换标签页:', this.activeTab)
      console.log('当前用户:', this.currentUser)
      this.fetchData()
    },
    
    showCreateDialog() {
      this.createDialogVisible = true
      this.$nextTick(() => {
        this.$refs.leaveForm.resetFields()
      })
    },
    
    async submitLeave() {
      try {
        if (!this.leaveForm.type) {
          this.$message.warning('请选择请假类型');
          return;
        }
        await this.$refs.leaveForm.validate()
        this.submitting = true
        await leaveApi.createLeave(this.leaveForm)
        this.$message.success('申请提交成功')
        this.createDialogVisible = false
        this.fetchData()
      } catch (error) {
        console.error('提交请假申请失败:', error)
        this.$message.error(error.response?.data?.message || '提交失败')
      } finally {
        this.submitting = false
      }
    },
    
    async viewDetail(leave) {
      try {
        console.log('查看请假详情, ID:', leave.id);
        const response = await leaveApi.getLeaveById(leave.id);
        console.log('请假详情响应:', response);
        this.currentLeave = response;
        this.detailDialogVisible = true;
      } catch (error) {
        console.error('获取请假详情失败:', error);
        this.$message.error('获取详情失败');
      }
    },
    
    approve(leave, action) {
      this.currentLeave = leave
      this.approvalAction = action
      this.approvalDialogVisible = true
      this.approvalForm.comment = ''
    },
    
    async submitApproval() {
      try {
        this.submitting = true
        await leaveApi.approveLeave(this.currentLeave.id, {
          status: this.approvalAction,  // 1: 同意, 2: 拒绝
          comment: this.approvalForm.comment
        })
        this.$message.success(this.approvalAction === 1 ? '已同意' : '已拒绝')
        this.approvalDialogVisible = false
        this.fetchData()
      } catch (error) {
        console.error('审批失败:', error)
        this.$message.error(error.response?.data?.message || '审批失败')
      } finally {
        this.submitting = false
      }
    },
    
    getLeaveTypeName(type) {
      const types = {
        1: '事假',
        2: '病假',
        3: '年假',
        4: '调休'
      }
      return types[type] || '未知'
    },
    
    getStatusName(status) {
      const statuses = {
        0: '待审批',
        1: '审批中',
        2: '已通过',
        3: '已拒绝'
      }
      return statuses[status] || '未知'
    },
    
    getStatusType(status) {
      const types = {
        0: 'warning',
        1: 'primary',
        2: 'success',
        3: 'danger'
      }
      return types[status] || 'info'
    }
  }
}
</script>

<style lang="scss" scoped>
.leave-list {
  padding: 20px;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
    }
  }
}

.leave-detail {
  .detail-item {
    margin-bottom: 15px;
    
    .label {
      display: inline-block;
      width: 80px;
      color: #606266;
    }
  }
}
</style> 
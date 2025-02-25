<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title"><i class="el-icon-office-building"></i> 部门管理</div>
      <el-button type="primary" @click="showCreateDialog">
        <i class="el-icon-plus"></i> 创建部门
      </el-button>
    </div>
    
    <el-table :data="departments" v-loading="loading">
      <el-table-column label="部门名称" prop="deptName">
        <template slot-scope="scope">
          <i class="el-icon-office-building"></i>
          {{ scope.row.deptName }}
        </template>
      </el-table-column>
      <el-table-column label="部门经理" prop="manager.name">
        <template slot-scope="scope">
          <i class="el-icon-user"></i>
          {{ scope.row.manager ? scope.row.manager.name : '暂无' }}
        </template>
      </el-table-column>
      <el-table-column label="部门人数" width="120">
        <template slot-scope="scope">
          {{ getDepartmentUserCount(scope.row.deptId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button type="text" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button type="text" @click="showAssignManagerDialog(scope.row)">指定经理</el-button>
          <el-button type="text" @click="showMembersDialog(scope.row)">查看成员</el-button>
          <el-button type="text" class="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 创建/编辑部门对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="departmentForm" :rules="rules" ref="departmentForm" label-width="80px">
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="departmentForm.deptName"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDepartment" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 指定部门经理对话框 -->
    <el-dialog title="指定部门经理" :visible.sync="managerDialogVisible" width="500px">
      <el-form :model="managerForm" :rules="managerRules" ref="managerForm" label-width="80px">
        <el-form-item label="部门经理" prop="managerId">
          <el-select v-model="managerForm.managerId" placeholder="请选择部门经理">
            <el-option
              v-for="user in availableManagers"
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
      </el-form>
      <div slot="footer">
        <el-button @click="managerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignManager" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 部门成员对话框 -->
    <el-dialog :title="`${currentDepartment?.deptName || ''} - 部门成员`" 
               :visible.sync="membersDialogVisible" 
               width="800px">
      <el-table :data="departmentUsers" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column label="角色" width="120">
          <template slot-scope="scope">
            <el-tag :type="getRoleType(scope.row.roleId)">
              {{ getRoleText(scope.row.roleId) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" width="150" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Departments',
  data() {
    return {
      loading: false,
      submitting: false,
      departments: [],
      users: [],
      dialogVisible: false,
      managerDialogVisible: false,
      membersDialogVisible: false,
      currentDepartment: null,
      departmentUsers: [],
      departmentForm: {
        deptName: ''
      },
      managerForm: {
        managerId: ''
      },
      rules: {
        deptName: [
          { required: true, message: '请输入部门名称', trigger: 'blur' }
        ]
      },
      managerRules: {
        managerId: [
          { required: true, message: '请选择部门经理', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.currentDepartment ? '编辑部门' : '创建部门'
    },
    availableManagers() {
      return this.users.filter(u => u.roleId === 3)
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const [departments, users] = await Promise.all([
          api.getDepartments(),
          api.getUsers()
        ])
        this.departments = departments
        this.users = users
      } catch (error) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    getDepartmentName(deptId) {
      const dept = this.departments.find(d => d.deptId === deptId)
      return dept ? dept.deptName : '未分配'
    },
    getDepartmentUserCount(deptId) {
      return this.users.filter(u => u.deptId === deptId).length
    },
    getRoleType(roleId) {
      const types = {
        1: 'danger',
        2: 'warning',
        3: 'success',
        4: 'info'
      }
      return types[roleId] || 'info'
    },
    getRoleText(roleId) {
      const texts = {
        1: '系统管理员',
        2: '总经理',
        3: '部门经理',
        4: '普通员工'
      }
      return texts[roleId] || '未知'
    },
    showCreateDialog() {
      this.currentDepartment = null
      this.dialogVisible = true
      this.departmentForm = { deptName: '' }
    },
    showEditDialog(department) {
      this.currentDepartment = department
      this.dialogVisible = true
      this.departmentForm = {
        deptName: department.deptName
      }
    },
    showAssignManagerDialog(department) {
      this.currentDepartment = department
      this.managerDialogVisible = true
      this.managerForm = {
        managerId: department.managerId || ''
      }
    },
    showMembersDialog(department) {
      this.currentDepartment = department
      this.membersDialogVisible = true
      this.departmentUsers = this.users.filter(u => u.deptId === department.deptId)
    },
    async submitDepartment() {
      this.$refs.departmentForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            if (this.currentDepartment) {
              await api.updateDepartment(this.currentDepartment.deptId, this.departmentForm)
              this.$message.success('更新成功')
            } else {
              await api.createDepartment(this.departmentForm)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            this.$message.error(this.currentDepartment ? '更新失败' : '创建失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    async handleAssignManager() {
      try {
        await this.$refs.managerForm.validate()
        this.submitting = true
        
        await api.assignManager(
          this.currentDepartment.deptId,
          this.managerForm.managerId
        )
        
        this.$message.success('指定成功')
        this.managerDialogVisible = false
        
        // 重新获取部门列表
        await this.fetchData()
        
        // 重新获取用户列表
        if (this.fetchUsers) {
          await this.fetchUsers()
        }
      } catch (error) {
        console.error('指定失败:', error)
        this.$message.error(error.response?.data?.message || '指定失败')
      } finally {
        this.submitting = false
      }
    },
    async handleDelete(department) {
      try {
        await this.$confirm('确认删除该部门吗？', '提示', {
          type: 'warning'
        })
        await api.deleteDepartment(department.deptId)
        this.$message.success('删除成功')
        await this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          this.$message.error('删除失败: ' + (error.response?.data?.message || '未知错误'))
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.danger {
  color: #F56C6C;
}
</style> 
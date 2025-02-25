<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title"><i class="el-icon-user"></i> 用户管理</div>
      <el-button type="primary" @click="showCreateDialog">
        <i class="el-icon-plus"></i> 创建用户
      </el-button>
    </div>
    
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="搜索用户名/姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.roleId" placeholder="角色" clearable>
            <el-option
              v-for="role in roles"
              :key="role.roleId"
              :label="role.roleName"
              :value="role.roleId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.deptId" placeholder="部门" clearable>
            <el-option
              v-for="dept in departments"
              :key="dept.deptId"
              :label="dept.deptName"
              :value="dept.deptId">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="filteredUsers" v-loading="loading">
      <el-table-column prop="username" label="用户名" width="150">
        <template slot-scope="scope">
          <i class="el-icon-user"></i>
          {{ scope.row.username }}
        </template>
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column label="角色" width="120">
        <template slot-scope="scope">
          <el-tag :type="getRoleType(scope.row.roleId)">
            {{ getRoleText(scope.row.roleId) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="部门" width="150">
        <template slot-scope="scope">
          {{ getDepartmentName(scope.row.deptId) }}
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status ? 'success' : 'danger'">
            <i :class="scope.row.status ? 'el-icon-success' : 'el-icon-error'"></i>
            {{ scope.row.status ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button type="text" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button type="text" @click="resetPassword(scope.row)">重置密码</el-button>
          <el-button 
            type="text" 
            :class="{ danger: scope.row.status }"
            @click="toggleStatus(scope.row)">
            {{ scope.row.status ? '禁用' : '启用' }}
          </el-button>
          <el-button type="text" class="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 创建/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="userForm" :rules="rules" ref="userForm" label-width="80px">
        <el-form-item label="用户名" prop="username" v-if="!currentUser">
          <el-input v-model="userForm.username"></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName"></el-input>
        </el-form-item>
        
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色">
            <el-option
              v-for="role in roles"
              :key="role.roleId"
              :label="role.roleName"
              :value="role.roleId">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="userForm.deptId" placeholder="请选择部门">
            <el-option
              v-for="dept in departments"
              :key="dept.deptId"
              :label="dept.deptName"
              :value="dept.deptId">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!currentUser">
          <el-input v-model="userForm.password" type="password"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Users',
  data() {
    return {
      loading: false,
      submitting: false,
      users: [],
      departments: [],
      roles: [
        { roleId: 2, roleName: '总经理' },
        { roleId: 3, roleName: '部门经理' },
        { roleId: 4, roleName: '普通员工' }
      ],
      searchForm: {
        keyword: '',
        roleId: '',
        deptId: ''
      },
      dialogVisible: false,
      currentUser: null,
      userForm: {
        username: '',
        password: '',
        realName: '',
        roleId: '',
        deptId: '',
        email: '',
        phone: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码不能少于6个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        roleId: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        deptId: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.currentUser ? '编辑用户' : '创建用户'
    },
    filteredUsers() {
      return this.users.filter(user => {
        const matchKeyword = !this.searchForm.keyword || 
          user.username.toLowerCase().includes(this.searchForm.keyword.toLowerCase()) ||
          user.name.toLowerCase().includes(this.searchForm.keyword.toLowerCase())
        
        const matchRole = !this.searchForm.roleId || 
          user.roleId === this.searchForm.roleId
          
        const matchDept = !this.searchForm.deptId || 
          user.deptId === this.searchForm.deptId
          
        return matchKeyword && matchRole && matchDept
      })
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const [users, departments] = await Promise.all([
          api.getUsers(),
          api.getDepartments()
        ])
        this.users = users
        this.departments = departments
      } catch (error) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
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
    getDepartmentName(deptId) {
      const dept = this.departments.find(d => d.deptId === deptId)
      return dept ? dept.deptName : '未分配'
    },
    showCreateDialog() {
      this.currentUser = null
      this.dialogVisible = true
      this.userForm = {
        username: '',
        password: '',
        realName: '',
        roleId: '',
        deptId: '',
        email: '',
        phone: ''
      }
    },
    showEditDialog(user) {
      this.currentUser = user
      this.dialogVisible = true
      this.userForm = {
        realName: user.name,
        roleId: user.roleId,
        deptId: user.deptId,
        email: user.email,
        phone: user.phone
      }
    },
    async submitUser() {
      this.$refs.userForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            // 检查是否设置为部门经理角色
            if (this.userForm.roleId === 3) {
              this.$message.warning('请注意：部门经理需要在部门管理中指定具体管理的部门')
            }
            
            if (this.currentUser) {
              const userData = {
                ...this.userForm,
                name: this.userForm.realName,
              };
              delete userData.realName;
              
              await api.updateUser(this.currentUser.userId, userData)
              this.$message.success('更新成功')
            } else {
              await api.register(this.userForm)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            console.error('提交失败:', error);
            this.$message.error(this.currentUser ? '更新失败' : '创建失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    async resetPassword(user) {
      try {
        await this.$confirm('确认重置该用户的密码吗？', '提示', {
          type: 'warning'
        })
        await api.resetPassword(user.userId)
        this.$message.success('密码重置成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('密码重置失败')
        }
      }
    },
    async toggleStatus(user) {
      try {
        await this.$confirm(
          `确认${user.status ? '禁用' : '启用'}该用户吗？`, 
          '提示',
          { type: 'warning' }
        )
        await api.updateUserStatus(user.userId, !user.status)
        user.status = !user.status
        this.$message.success(`${user.status ? '启用' : '禁用'}成功`)
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`${user.status ? '禁用' : '启用'}失败`)
        }
      }
    },
    async handleDelete(user) {
      try {
        await this.$confirm('确认删除该用户吗？', '提示', {
          type: 'warning'
        })
        await api.deleteUser(user.userId)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
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
<template>
  <div class="register-container">
    <el-card class="register-card">
      <div slot="header">
        <span>用户注册</span>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username"></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password"></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email"></el-input>
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input v-model="registerForm.phone"></el-input>
        </el-form-item>

        <el-form-item label="部门" prop="deptId">
          <el-select v-model="registerForm.deptId" placeholder="请选择部门">
            <el-option
              v-for="dept in departments"
              :key="dept.deptId"
              :label="dept.deptName"
              :value="dept.deptId">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="角色" prop="roleId">
          <el-select v-model="registerForm.roleId" placeholder="请选择角色">
            <el-option label="普通用户" :value="2"></el-option>
            <el-option label="部门经理" :value="3"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '../api'

export default {
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      departments: [],
      registerForm: {
        username: '',
        realName: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: '',
        deptId: null,
        roleId: null
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        deptId: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        roleId: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.fetchDepartments()
  },
  methods: {
    async fetchDepartments() {
      try {
        this.departments = await api.getDepartments()
      } catch (error) {
        console.error('获取部门列表失败:', error)
        this.$message.error('获取部门列表失败')
      }
    },
    async handleRegister() {
      try {
        await this.$refs.registerForm.validate()
        this.loading = true
        
        const { confirmPassword, ...userData } = this.registerForm
        console.log('发送注册数据:', userData)
        
        await api.register(userData)
        this.$message.success('注册成功，请登录')
        this.$router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
        if (error.response?.data?.message) {
          this.$message.error(error.response.data.message)
        } else if (error.message) {
          this.$message.error(error.message)
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.register-card {
  width: 400px;
}
</style> 
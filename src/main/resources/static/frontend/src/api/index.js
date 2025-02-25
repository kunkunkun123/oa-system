import axios from 'axios'
import store from '../store'
import router from '../router'
import { Message } from 'element-ui'

// 创建 axios 实例
const api = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 5000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = store.state.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    // 添加调试日志
    console.log('Request config:', config)
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    if (error.code === 'ECONNABORTED') {
      console.error('请求超时')
      Message.error('请求超时，请重试')
    } else if (error.response) {
      switch (error.response.status) {
        case 401:
          store.dispatch('logout')
          router.push('/login')
          Message.error('登录已过期，请重新登录')
          break
        case 403:
          Message.error('没有权限执行此操作')
          break
        default:
          Message.error(error.response.data?.message || '服务器错误')
      }
    } else if (error.request) {
      console.error('请求未收到响应')
      Message.error('网络连接失败，请检查网络')
    } else {
      Message.error('请求发送失败')
    }
    return Promise.reject(error)
  }
)

export default {
  // 用户相关
  login: data => {
    console.log('发送登录请求:', data)
    const startTime = Date.now()
    return api.post('/users/login', data).then(response => {
      console.log('登录请求耗时:', Date.now() - startTime, 'ms')
      console.log('登录响应:', response)
      return response
    })
  },
  register: data => {
    // 转换 realName 为 name
    const convertedData = {
      ...data,
      name: data.realName
    };
    delete convertedData.realName;
    
    console.log('API register 方法接收到的数据:', convertedData)
    return api.post('/users/register', convertedData)
  },
  updatePassword: data => api.put('/users/password', data),
  getUsers: () => api.get('/users'),
  updateUser: (userId, data) => {
    console.log('更新用户数据:', { userId, data })
    return api.put(`/users/${userId}`, data)
  },
  resetPassword: userId => api.post(`/users/${userId}/reset-password`),
  updateUserStatus: (userId, status) => api.put(`/users/${userId}/status`, { status }),
  deleteUser: userId => api.delete(`/users/${userId}`),
  
  // 消息相关
  getMessages: () => api.get('/messages/my'),
  getSentMessages: () => api.get('/messages/sent'),
  sendMessage: data => api.post('/messages', data),
  sendDepartmentMessage: (deptId, data) => api.post(`/messages/department/${deptId}`, data),
  markMessageAsRead: messageId => {
    if (!messageId) {
      return Promise.reject(new Error('消息ID不能为空'))
    }
    return api.put(`/messages/${messageId}/read`)
  },
  
  // 任务相关
  getTasks: () => {
    console.log('开始获取任务列表')
    return api.get('/tasks').then(response => {
      console.log('获取任务列表响应:', response)
      return response
    })
  },
  getMyTasks: () => {
    console.log('获取我的任务列表')
    return api.get('/tasks/my')
  },
  getDepartmentTasks: deptId => api.get(`/tasks/department/${deptId}`),
  createTask: data => {
    console.log('创建任务请求数据:', data)
    return api.post('/tasks', data).then(response => {
      console.log('创建任务响应:', response)
      return response
    })
  },
  updateTaskStatus: (taskId, status) => 
    api.put(`/tasks/${taskId}/status`, status, {
      headers: {
        'Content-Type': 'application/json'
      }
    }),
  
  // 部门相关
  getDepartments: () => api.get('/departments'),
  createDepartment: data => api.post('/departments', data),
  updateDepartment: (deptId, data) => api.put(`/departments/${deptId}`, data),
  deleteDepartment: deptId => api.delete(`/departments/${deptId}`),
  assignManager: (deptId, managerId) => api.put(`/departments/${deptId}/manager/${managerId}`),
  
  // 公告相关
  getAnnouncements: (all = false) => api.get(`/announcements${all ? '?all=true' : ''}`),
  createAnnouncement: (data) => api.post('/announcements', data),
  updateAnnouncementStatus: (id, status) => api.put(`/announcements/${id}/status`, status),
  
  // 获取月度任务统计
  getMonthlyTaskStats() {
    return api.get('/tasks/monthly-stats')
  }
} 
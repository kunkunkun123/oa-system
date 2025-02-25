import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // API 的基础URL
  timeout: 15000  // 增加超时时间到 15 秒
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = store.getters.token || localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data  // 直接返回响应数据
  },
  error => {
    console.log('API Error:', error)
    
    if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
      Message.error('请求超时，请检查网络连接')
    } else if (error.response) {
      switch (error.response.status) {
        case 401:
          store.dispatch('logout')
          Message.error('登录已过期，请重新登录')
          break
        case 403:
          Message.error('没有权限执行此操作')
          break
        case 404:
          Message.error('请求的资源不存在')
          break
        case 500:
          Message.error('服务器内部错误')
          break
        default:
          Message.error(error.response.data?.message || '请求失败')
      }
    } else {
      Message.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default service 
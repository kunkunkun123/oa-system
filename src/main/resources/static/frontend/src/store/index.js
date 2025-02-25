import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import router from '../router'
import api from '../api'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user')) || null
  },
  getters: {
    isLoggedIn: state => !!state.token,
    currentUser: state => state.user,
    isAdmin: state => state.user && state.user.roleId === 1,
    isManager: state => state.user && (state.user.roleId === 1 || state.user.roleId === 3)
  },
  mutations: {
    setToken(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    setUser(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    clearAuth(state) {
      state.token = ''
      state.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    resetState(state) {
      // 重置所有状态到初始值
      Object.assign(state, {
        token: '',
        user: null,
        // 添加其他需要重置的状态...
      })
    }
  },
  actions: {
    async login({ commit }, loginData) {
      try {
        const response = await api.login(loginData)
        
        // 保存 token 和用户信息
        const { token, user } = response
        commit('setToken', token)
        commit('setUser', user)
        
        // 设置全局默认 header
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
        
        return user
      } catch (error) {
        throw error
      }
    },
    
    logout({ commit }) {
      // 清除认证信息
      commit('clearAuth')
      
      // 清除 axios 默认请求头
      delete axios.defaults.headers.common['Authorization']
      
      // 清除本地存储
      localStorage.clear()  // 清除所有本地存储
      sessionStorage.clear()  // 清除所有会话存储
      
      // 清除 vuex store
      commit('resetState')
      
      // 清除路由缓存
      if (router.app.$route.meta.keepAlive) {
        const key = router.app.$route.name
        const cache = router.app.$refs.layout && router.app.$refs.layout.$refs.content
        if (cache && cache.cache) {
          delete cache.cache[key]
        }
      }
      
      // 跳转到登录页
      if (router.currentRoute.path !== '/login') {
        router.push('/login')
      }
      
      // 刷新页面以确保完全清除
      window.location.reload()
    }
  }
}) 
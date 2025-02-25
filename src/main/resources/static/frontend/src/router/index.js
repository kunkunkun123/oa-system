import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'
import Users from '../views/Users.vue'
import Departments from '../views/Departments.vue'
import Announcements from '../views/Announcements.vue'
import Layout from '../layout/index.vue'

Vue.use(VueRouter)

// 修复路由重复导航错误
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('../views/Messages.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'tasks',
        name: 'Tasks',
        component: () => import('../views/Tasks.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'departments',
        name: 'Departments',
        component: Departments,
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: Users,
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: Announcements,
        meta: { requiresAuth: true }
      },
      {
        path: 'leave',
        name: 'LeaveList',
        component: () => import('../views/leave/LeaveList.vue'),
        meta: { title: '请假管理', icon: 'el-icon-date', requiresAuth: true }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  console.log('路由守卫：', {
    to: to.fullPath,
    from: from.fullPath,
    isLoggedIn: store.getters.isLoggedIn,
    user: store.getters.currentUser
  })
  
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    console.log('当前路由需要认证')
    // 检查用户是否已登录
    if (!store.getters.isLoggedIn) {
      console.log('用户未登录，重定向到登录页')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    // 检查是否需要管理员权限
    if (to.matched.some(record => record.meta.requiresAdmin)) {
      console.log('当前路由需要管理员权限')
      if (!store.getters.isAdmin) {
        console.log('用户不是管理员，重定向到首页')
        next('/dashboard')
        return
      }
    }
  }
  
  // 如果已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && store.getters.isLoggedIn) {
    console.log('已登录用户访问登录页，重定向到首页')
    next('/dashboard')
    return
  }
  
  console.log('允许导航到：', to.fullPath)
  next()
})

export default router 
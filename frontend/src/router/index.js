import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/Index.vue'),
        meta: { title: '学生管理', icon: 'User', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'growth',
        name: 'Growth',
        component: () => import('@/views/growth/Index.vue'),
        meta: { title: '成长档案', icon: 'Document' }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/message/Index.vue'),
        meta: { title: '家校互动', icon: 'ChatDotRound' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/Index.vue'),
        meta: { title: '数据统计', icon: 'DataAnalysis' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice/Index.vue'),
        meta: { title: '通知公告', icon: 'Bell' }
      },
      {
        path: 'user/profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人信息', icon: 'User', hidden: true }
      },
      {
        path: 'teacher',
        name: 'Teacher',
        component: () => import('@/views/teacher/Index.vue'),
        meta: { title: '教师管理', icon: 'UserFilled', roles: ['ADMIN'] }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/Index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', roles: ['ADMIN'] }
      },
      {
        path: 'class',
        name: 'Class',
        component: () => import('@/views/class/Index.vue'),
        meta: { title: '班级管理', icon: 'School', roles: ['ADMIN'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

    if (!token) {
      if (to.path === '/login' || to.path === '/register') {
        next()
      } else {
        ElMessage.error('请先登录')
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('userInfo')
        next('/login')
      }
    } else {
      if (to.path === '/login' || to.path === '/register') {
        next('/')
      } else {
        if (to.meta.roles && !to.meta.roles.includes(userStore.userInfo?.role)) {
          ElMessage.error('无权访问')
          next('/')
        } else {
          next()
        }
      }
    }
})

export default router

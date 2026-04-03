<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: item.color }">
              <el-icon :size="30" color="white">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>快速入口</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="router.push('/student')">
              <el-icon :size="40" color="#667eea"><User /></el-icon>
              <span>学生管理</span>
            </div>
            <div class="action-item" @click="router.push('/growth')">
              <el-icon :size="40" color="#764ba2"><Document /></el-icon>
              <span>成长档案</span>
            </div>
            <div class="action-item" @click="router.push('/message')">
              <el-icon :size="40" color="#f093fb"><ChatDotRound /></el-icon>
              <span>家校互动</span>
            </div>
            <div class="action-item" @click="router.push('/statistics')">
              <el-icon :size="40" color="#4facfe"><DataAnalysis /></el-icon>
              <span>数据统计</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最新通知</span>
              <el-link type="primary" @click="router.push('/notice')">查看更多</el-link>
            </div>
          </template>
          <el-empty v-if="notices.length === 0" description="暂无通知" />
          <div v-else class="notice-list">
            <div v-for="notice in notices" :key="notice.id" class="notice-item" @click="router.push(`/notice?id=${notice.id}`)">
              <div class="notice-header">
                <el-tag :type="getPriorityType(notice.priority)" size="small" class="notice-priority">{{ getPriorityText(notice.priority) }}</el-tag>
                <el-tag :type="notice.type === 'school' ? 'primary' : 'success'" size="small" class="notice-type">{{ notice.targetName || (notice.type === 'school' ? '学校通知' : '班级通知') }}</el-tag>
              </div>
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-time">{{ notice.publishTime }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticePage } from '@/api/notice'
import { getDashboardStats } from '@/api/statistics'

const router = useRouter()

const stats = ref([
  { title: '学生总数', value: '0', icon: 'User', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '教师总数', value: '0', icon: 'UserFilled', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '班级总数', value: '0', icon: 'School', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: '消息总数', value: '0', icon: 'ChatDotRound', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const notices = ref([])

const fetchDashboardStats = async () => {
  try {
    const res = await getDashboardStats()
    const data = res.data
    stats.value[0].value = data.studentCount
    stats.value[1].value = data.teacherCount
    stats.value[2].value = data.classCount
    stats.value[3].value = data.messageCount
  } catch (error) {
    console.error(error)
  }
}

const getPriorityType = (priority) => {
  if (priority === 1) return 'info'
  if (priority === 2) return 'warning'
  if (priority === 3) return 'danger'
  return 'info'
}

const getPriorityText = (priority) => {
  if (priority === 1) return '普通'
  if (priority === 2) return '重要'
  if (priority === 3) return '紧急'
  return '普通'
}

const fetchNotices = async () => {
  try {
    const res = await getNoticePage({ current: 1, size: 5 })
    notices.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchDashboardStats()
  fetchNotices()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-title {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.chart-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  background: #f5f7fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #e6f7ff;
  transform: translateY(-3px);
}

.action-item span {
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

.notice-list {
  max-height: 300px;
  overflow-y: auto;
}

.notice-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notice-item:hover {
  background-color: #f9f9f9;
}

.notice-header {
  display: flex;
  gap: 10px;
  margin-bottom: 5px;
}

.notice-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-time {
  font-size: 12px;
  color: #999;
}
</style>

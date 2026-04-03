<template>
  <div class="statistics-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>成绩统计</span>
              <el-select v-model="currentStudentId" placeholder="请选择学生" style="width: 200px" @change="handleStudentChange">
                <el-option v-for="student in studentList" :key="student.id" :label="`${student.name} (${student.className || '未分班'})`" :value="student.id" />
              </el-select>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div ref="scoreChartRef" style="height: 400px"></div>
            </el-col>
            <el-col :span="12">
              <div style="height: 40px; display: flex; justify-content: flex-end; align-items: center; margin-bottom: 10px;">
                <el-select v-model="currentSubject" placeholder="全部科目" clearable style="width: 150px" @change="handleSubjectChange">
                  <el-option v-for="subject in subjects" :key="subject" :label="subject" :value="subject" />
                </el-select>
              </div>
              <div ref="trendChartRef" style="height: 350px"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>各科成绩详情</span>
          </template>
          <el-table :data="scoreStatistics" border>
            <el-table-column prop="subject" label="科目" width="120" />
            <el-table-column prop="average" label="平均分" width="100" />
            <el-table-column prop="max" label="最高分" width="100" />
            <el-table-column prop="min" label="最低分" width="100" />
            <el-table-column prop="count" label="考试次数" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/stores/user'
import { getStudentPage, getMyStudents, getTeacherStudents } from '@/api/student'
import { getScoreStatistics, getScoreTrend } from '@/api/statistics'

const userStore = useUserStore()
const isParent = computed(() => userStore.userInfo?.role === 'PARENT')
const isTeacher = computed(() => userStore.userInfo?.role === 'TEACHER')

const currentStudentId = ref(null)
const studentList = ref([])
const scoreStatistics = ref([])
const subjects = ref([])
const currentSubject = ref('')

const scoreChartRef = ref(null)
const trendChartRef = ref(null)

let scoreChart = null
let trendChart = null

const fetchStudents = async () => {
  try {
    let res
    if (isParent.value) {
      res = await getMyStudents()
      studentList.value = res.data
    } else if (isTeacher.value) {
      res = await getTeacherStudents()
      studentList.value = res.data
    } else {
      res = await getStudentPage({ current: 1, size: 100 })
      studentList.value = res.data.records
    }
    
    if (studentList.value.length > 0) {
      currentStudentId.value = studentList.value[0].id
      fetchStatistics()
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchStatistics = async () => {
  if (!currentStudentId.value) return

  try {
    const [statRes, trendRes] = await Promise.all([
      getScoreStatistics(currentStudentId.value),
      getScoreTrend(currentStudentId.value)
    ])

    scoreStatistics.value = Object.entries(statRes.data).map(([subject, data]) => ({
      subject,
      ...data
    }))
    
    subjects.value = scoreStatistics.value.map(item => item.subject)

    await nextTick()
    initScoreChart(statRes.data)
    initTrendChart(trendRes.data)
  } catch (error) {
    console.error(error)
  }
}

const fetchTrend = async () => {
  if (!currentStudentId.value) return
  try {
    const params = {}
    if (currentSubject.value) {
      params.subject = currentSubject.value
    }
    const res = await getScoreTrend(currentStudentId.value, params)
    initTrendChart(res.data)
  } catch (error) {
    console.error(error)
  }
}

const handleSubjectChange = () => {
  fetchTrend()
}

const initScoreChart = (data) => {
  if (!scoreChartRef.value) return

  if (scoreChart) {
    scoreChart.dispose()
  }

  scoreChart = echarts.init(scoreChartRef.value)

  const subjects = Object.keys(data)
  const avgScores = subjects.map(s => data[s].average.toFixed(2))
  const maxScores = subjects.map(s => data[s].max.toFixed(2))
  const minScores = subjects.map(s => data[s].min.toFixed(2))

  const option = {
    title: {
      text: '各科成绩统计'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['平均分', '最高分', '最低分']
    },
    xAxis: {
      type: 'category',
      data: subjects
    },
    yAxis: {
      type: 'value',
      max: currentSubject.value ? 100 : undefined
    },
    series: [
      {
        name: '平均分',
        type: 'bar',
        data: avgScores,
        itemStyle: {
          color: '#5470c6'
        }
      },
      {
        name: '最高分',
        type: 'bar',
        data: maxScores,
        itemStyle: {
          color: '#91cc75'
        }
      },
      {
        name: '最低分',
        type: 'bar',
        data: minScores,
        itemStyle: {
          color: '#fac858'
        }
      }
    ]
  }

  scoreChart.setOption(option)
}

const initTrendChart = (data) => {
  if (!trendChartRef.value) return

  if (trendChart) {
    trendChart.dispose()
  }

  trendChart = echarts.init(trendChartRef.value)

  const option = {
    title: {
      text: currentSubject.value ? `${currentSubject.value}成绩趋势` : '总成绩趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: {
      type: 'value',
      max: currentSubject.value ? 100 : undefined
    },
    series: [
      {
        name: '成绩',
        type: 'line',
        data: data.scores,
        smooth: true,
        itemStyle: {
          color: '#667eea'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
            ]
          }
        }
      }
    ]
  }

  trendChart.setOption(option)
}

const handleStudentChange = () => {
  currentSubject.value = ''
  fetchStatistics()
}

onMounted(() => {
  fetchStudents()

  window.addEventListener('resize', () => {
    scoreChart?.resize()
    trendChart?.resize()
  })
})
</script>

<style scoped>
.statistics-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

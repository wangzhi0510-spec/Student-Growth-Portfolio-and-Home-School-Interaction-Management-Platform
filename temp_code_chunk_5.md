
### frontend/src/views/Login.vue
```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>学生成长档案与家校互动管理平台</h1>
        <p>Student Growth & Home-School Interaction Platform</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" style="width: 100%" @click="handleLogin">登录</el-button>
        </el-form-item>
        <el-form-item>
          <div class="login-footer">
            <span>还没有账号？</span>
            <router-link to="/register">立即注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 450px;
  padding: 50px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-top: 30px;
}

.login-footer {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>
```

### frontend/src/views/Register.vue
```vue
<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1>用户注册</h1>
        <p>Student Growth & Home-School Interaction Platform</p>
      </div>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="学生学号" prop="studentNo">
          <el-input v-model="registerForm.studentNo" placeholder="请输入关联学生的学号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" style="width: 100%" @click="handleRegister">注册</el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-footer">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'PARENT',
  studentNo: ''
})

const validateStudentNo = (rule, value, callback) => {
  if (registerForm.role === 'PARENT' && !value) {
    callback(new Error('家长注册必须填写学生学号'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  studentNo: [{ validator: validateStudentNo, trigger: 'blur' }]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-y: auto;
}

.register-box {
  width: 500px;
  padding: 50px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  margin: 20px 0;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.register-header p {
  font-size: 14px;
  color: #999;
}

.register-form {
  margin-top: 30px;
}

.register-footer {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-footer a {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.register-footer a:hover {
  text-decoration: underline;
}
</style>
```

### frontend/src/views/Dashboard.vue
```vue
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
```

### frontend/src/views/student/Index.vue
```vue
<template>
  <div class="student-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-button type="primary" @click="handleAdd">新增学生</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="请输入学号或姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="birthday" label="生日" width="120" />
        <el-table-column prop="enrollmentDate" label="入学日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="formData.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="formData.classId" placeholder="请选择班级" :disabled="classList.length === 0">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
          <div v-if="classList.length === 0" class="no-class-tip">
            暂无班级，请先添加班级
          </div>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker v-model="formData.birthday" type="date" placeholder="请选择生日" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="入学日期" prop="enrollmentDate">
          <el-date-picker v-model="formData.enrollmentDate" type="date" placeholder="请选择入学日期" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentPage, addStudent, updateStudent, deleteStudent } from '@/api/student'
import { getClassList } from '@/api/class'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const classList = ref([])

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const formData = reactive({
  id: null,
  userId: null,
  name: '',
  studentNo: '',
  classId: null,
  gender: '',
  birthday: '',
  enrollmentDate: ''
})

const formRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  classId: [{ required: true, message: '请输入班级ID', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage({
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  pagination.current = 1
  fetchData()
}

const handleAdd = () => {
    if (classList.value.length === 0) {
      ElMessage.warning('暂无班级，请先在班级管理中添加班级')
      return
    }
    dialogTitle.value = '新增学生'
    Object.assign(formData, {
      id: null,
      userId: null,
      name: '',
      studentNo: '',
      classId: null,
      gender: '',
      birthday: '',
      enrollmentDate: ''
    })
    dialogVisible.value = true
  }

const handleEdit = (row) => {
  dialogTitle.value = '编辑学生'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (formData.id) {
      await updateStudent(formData)
      ElMessage.success('更新成功')
    } else {
      await addStudent(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const fetchClassList = async () => {
  try {
    const res = await getClassList()
    classList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
  fetchClassList()
})
</script>

<style scoped>
.student-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.no-class-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}
</style>
```

### frontend/src/views/statistics/Index.vue
```vue
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
```

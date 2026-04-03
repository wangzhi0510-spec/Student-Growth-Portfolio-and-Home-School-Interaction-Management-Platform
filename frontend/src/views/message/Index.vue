<template>
  <div class="message-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="message-list-card">
          <template #header>
            <div class="card-header">
              <span>消息列表</span>
            </div>
          </template>
          <el-tabs v-model="messageType" @tab-change="fetchMessages">
            <el-tab-pane label="收到的消息" name="received">
              <div class="message-list">
                <div v-for="msg in messageList" :key="msg.id" class="message-item" :class="{ active: currentMessage?.id === msg.id, unread: msg.isRead === 0 }" @click="handleSelectMessage(msg)">
                  <div class="message-title">{{ msg.title }}</div>
                  <div class="message-time">{{ msg.createTime }}</div>
                </div>
                <el-empty v-if="messageList.length === 0" description="暂无消息" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="发送的消息" name="sent">
              <div class="message-list">
                <div v-for="msg in messageList" :key="msg.id" class="message-item" :class="{ active: currentMessage?.id === msg.id }" @click="handleSelectMessage(msg)">
                  <div class="message-title">{{ msg.title }}</div>
                  <div class="message-time">{{ msg.createTime }}</div>
                </div>
                <el-empty v-if="messageList.length === 0" description="暂无消息" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="message-detail-card">
          <template #header>
            <div class="card-header">
              <span>消息详情</span>
              <el-button type="primary" @click="handleSendMessage">发送消息</el-button>
            </div>
          </template>
          <div v-if="currentMessage" class="message-detail">
            <div class="detail-header">
              <h3>{{ currentMessage.title }}</h3>
              <div class="detail-meta">
                <span>{{ currentMessage.createTime }}</span>
                <el-tag v-if="currentMessage.isRead === 0" type="danger">未读</el-tag>
                <el-tag v-else type="success">已读</el-tag>
              </div>
            </div>
            <div class="detail-content">
              {{ currentMessage.content }}
            </div>
          </div>
          <el-empty v-else description="请选择一条消息查看详情" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="sendDialogVisible" title="发送消息" width="600px">
      <el-form ref="sendFormRef" :model="sendForm" :rules="sendRules" label-width="100px">
        <el-form-item label="关联学生" v-if="isParent">
          <el-select v-model="selectedStudentId" placeholder="请选择学生" @change="handleStudentChange" style="width: 100%">
            <el-option v-for="student in studentList" :key="student.id" :label="`${student.name} (${student.className || '未分班'})`" :value="student.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收者" prop="receiverId">
          <template v-if="isParent">
            <el-input v-model="teacherName" disabled placeholder="班主任" />
          </template>
          <template v-else-if="isTeacher">
            <el-select v-model="currentSelectedStudentId" placeholder="请选择学生家长" filterable style="width: 100%" @change="handleTeacherStudentChange">
              <el-option v-for="student in studentList" :key="student.id" :label="`${student.name} (${student.studentNo}) - ${student.parentId ? '已绑定' : '未绑定'}`" :value="student.id" :disabled="!student.parentId" />
            </el-select>
          </template>
          <el-input v-else v-model="sendForm.receiverId" placeholder="请输入接收者ID" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="sendForm.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { getReceivedMessages, getSentMessages, sendMessage, markAsRead, deleteMessage } from '@/api/message'
import { getMyStudents, getTeacherStudents } from '@/api/student'
import { getClassById } from '@/api/class'
import { getUserById } from '@/api/user'

const userStore = useUserStore()
const isParent = computed(() => userStore.userInfo?.role === 'PARENT')
const isTeacher = computed(() => userStore.userInfo?.role === 'TEACHER')
const teacherName = ref('')
const studentList = ref([])
const selectedStudentId = ref(null)

const messageType = ref('received')
const messageList = ref([])
const currentMessage = ref(null)
const sendDialogVisible = ref(false)
const sendFormRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 50
})

const sendForm = reactive({
  receiverId: null,
  title: '',
  content: ''
})

const sendRules = {
  receiverId: [{ required: true, message: '请输入接收者ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const fetchMessages = async () => {
  try {
    let res
    if (messageType.value === 'received') {
      res = await getReceivedMessages({ current: pagination.current, size: pagination.size })
    } else {
      res = await getSentMessages({ current: pagination.current, size: pagination.size })
    }
    messageList.value = res.data.records
    if (messageList.value.length > 0) {
      currentMessage.value = messageList.value[0]
    } else {
      currentMessage.value = null
    }
  } catch (error) {
    console.error(error)
  }
}

const handleSelectMessage = async (msg) => {
  currentMessage.value = msg
  if (messageType.value === 'received' && msg.isRead === 0) {
    try {
      await markAsRead(msg.id)
      msg.isRead = 1
      msg.readTime = new Date().toISOString()
    } catch (error) {
      console.error(error)
    }
  }
}

const handleStudentChange = async (studentId) => {
  if (!studentId) return
  const student = studentList.value.find(s => s.id === studentId)
  if (student) {
    if (student.classId) {
      try {
        const classRes = await getClassById(student.classId)
        if (classRes.data && classRes.data.teacherId) {
          sendForm.receiverId = classRes.data.teacherId
          const userRes = await getUserById(classRes.data.teacherId)
          if (userRes.data) {
            teacherName.value = userRes.data.realName || userRes.data.username
          }
        } else {
          sendForm.receiverId = null
          teacherName.value = '该班级暂未设置班主任'
        }
      } catch (error) {
        console.error(error)
      }
    } else {
      sendForm.receiverId = null
      teacherName.value = '该学生暂未分班'
    }
  }
}

const currentSelectedStudentId = ref(null)

const handleTeacherStudentChange = (studentId) => {
  if (!studentId) return
  const student = studentList.value.find(s => s.id === studentId)
  if (student) {
    sendForm.receiverId = student.parentId
  }
}

const handleSendMessage = async () => {
  Object.assign(sendForm, {
    receiverId: null,
    title: '',
    content: ''
  })
  teacherName.value = ''
  selectedStudentId.value = null
  currentSelectedStudentId.value = null
  
  if (isParent.value) {
    try {
      const studentRes = await getMyStudents()
      studentList.value = studentRes.data
      if (studentList.value.length > 0) {
        selectedStudentId.value = studentList.value[0].id
        handleStudentChange(selectedStudentId.value)
      } else {
        ElMessage.warning('您尚未绑定学生')
      }
    } catch (error) {
      console.error(error)
    }
  } else if (isTeacher.value) {
    try {
      const studentRes = await getTeacherStudents()
      studentList.value = studentRes.data
    } catch (error) {
      console.error(error)
    }
  }
  
  sendDialogVisible.value = true
}

const handleSubmitSend = async () => {
  const valid = await sendFormRef.value.validate()
  if (!valid) return

  try {
    await sendMessage(sendForm)
    ElMessage.success('发送成功')
    sendDialogVisible.value = false
    fetchMessages()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.message-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  max-height: 600px;
  overflow-y: auto;
}

.message-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.message-item:hover {
  background-color: #f5f7fa;
}

.message-item.active {
  background-color: #e6f7ff;
  border-left: 3px solid #1890ff;
}

.message-item.unread {
  font-weight: bold;
}

.message-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-detail {
  padding: 20px 0;
}

.detail-header h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.detail-meta span {
  font-size: 12px;
  color: #999;
}

.detail-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>

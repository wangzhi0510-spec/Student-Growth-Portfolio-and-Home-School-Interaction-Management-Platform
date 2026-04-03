<template>
  <div class="notice-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知公告</span>
          <el-button type="primary" @click="handleAdd" v-if="canPublish">发布公告</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="title" label="标题" width="300" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.priority === 3" type="danger">紧急</el-tag>
            <el-tag v-else-if="row.priority === 2" type="warning">重要</el-tag>
            <el-tag v-else>普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="canPublish">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="formData.priority">
            <el-radio :label="1">普通</el-radio>
            <el-radio :label="2">重要</el-radio>
            <el-radio :label="3">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="通知详情" width="700px">
      <div v-if="currentNotice" class="notice-detail">
        <h3>{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <el-tag v-if="currentNotice.type === 'school'" type="primary">学校通知</el-tag>
          <el-tag v-else-if="currentNotice.type === 'class'" type="success">班级通知</el-tag>
          <el-tag v-else type="info">个人通知</el-tag>
          <el-tag v-if="currentNotice.priority === 3" type="danger">紧急</el-tag>
          <el-tag v-else-if="currentNotice.priority === 2" type="warning">重要</el-tag>
          <el-tag v-else>普通</el-tag>
          <span>{{ currentNotice.publishTime }}</span>
        </div>
        <div class="notice-content">
          {{ currentNotice.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticePage, addNotice, deleteNotice } from '@/api/notice'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isParent = computed(() => userStore.userInfo?.role === 'PARENT')
const isStudent = computed(() => userStore.userInfo?.role === 'STUDENT')
const canPublish = computed(() => !isParent.value && !isStudent.value)

const loading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const currentNotice = ref(null)

const formData = reactive({
  id: null,
  title: '',
  type: 'school',
  targetType: 'all',
  targetId: null,
  priority: 1,
  content: ''
})

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getNoticePage({
      current: pagination.current,
      size: pagination.size
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
    
    // 如果路由中有id参数，自动打开对应的通知详情
    if (route.query.id) {
      const noticeId = parseInt(route.query.id)
      const notice = tableData.value.find(item => item.id === noticeId)
      if (notice) {
        handleView(notice)
      }
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '发布公告'
  Object.assign(formData, {
    id: null,
    title: '',
    type: 'school', // 默认类型，虽然界面不显示，但后端可能需要
    targetType: 'all', // 默认全校通知
    targetId: null,
    priority: 1,
    content: ''
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  currentNotice.value = row
  viewDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteNotice(row.id)
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
    await addNotice(formData)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.notice-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-detail h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.notice-meta span {
  font-size: 12px;
  color: #999;
}

.notice-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>

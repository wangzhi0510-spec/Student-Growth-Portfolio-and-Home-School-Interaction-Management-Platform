<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag v-if="form.role === 'ADMIN'" type="danger">管理员</el-tag>
          <el-tag v-else-if="form.role === 'TEACHER'" type="primary">教师</el-tag>
          <el-tag v-else-if="form.role === 'STUDENT'" type="success">学生</el-tag>
          <el-tag v-else type="warning">家长</el-tag>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="profile-card" v-if="form.role === 'PARENT'">
      <template #header>
        <div class="card-header">
          <span>我的孩子</span>
          <el-button type="primary" link @click="bindDialogVisible = true">绑定孩子</el-button>
        </div>
      </template>
      <el-table :data="myStudents" border style="margin-top: 10px">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="className" label="班级" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleUnbind(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="bindDialogVisible" title="绑定孩子" width="400px">
      <el-form :model="bindForm" label-width="80px">
        <el-form-item label="学号">
          <el-input v-model="bindForm.studentNo" placeholder="请输入孩子学号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBind">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentUser } from '@/api/auth'
import { updateUserProfile } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { getMyStudents, bindStudent, unbindStudent } from '@/api/student'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const myStudents = ref([])
const bindDialogVisible = ref(false)
const bindForm = reactive({
  studentNo: ''
})

const form = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: ''
})

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    Object.assign(form, res.data)
    if (form.role === 'PARENT') {
      fetchMyStudents()
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchMyStudents = async () => {
  try {
    const res = await getMyStudents()
    myStudents.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleBind = async () => {
  if (!bindForm.studentNo) {
    ElMessage.warning('请输入学号')
    return
  }
  try {
    await bindStudent(bindForm.studentNo)
    ElMessage.success('绑定成功')
    bindDialogVisible.value = false
    bindForm.studentNo = ''
    fetchMyStudents()
  } catch (error) {
    console.error(error)
  }
}

const handleUnbind = async (row) => {
  try {
    await ElMessageBox.confirm('确定要解绑该学生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unbindStudent(row.id)
    ElMessage.success('解绑成功')
    fetchMyStudents()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await updateUserProfile(form)
        ElMessage.success('修改成功')
        // 更新 store 中的用户信息
        userStore.userInfo = { ...userStore.userInfo, ...form }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.profile-card {
  width: 600px;
}
</style>

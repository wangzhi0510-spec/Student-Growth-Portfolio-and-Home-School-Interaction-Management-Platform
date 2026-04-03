<template>
  <div class="teacher-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
          <!-- 教师管理主要用于分配班级，新增教师建议在用户管理中进行 -->
        </div>
      </template>
      <el-table :data="teacherList" v-loading="loading" border>
        <el-table-column prop="username" label="工号/用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column label="执教班级" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="cls in getTeacherClasses(row.id)" :key="cls.id" class="class-tag" type="success">
              {{ cls.className }}
            </el-tag>
            <span v-if="getTeacherClasses(row.id).length === 0" class="no-class">暂无班级</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAssign(row)">设置班主任</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="设置班主任" width="500px">
      <el-form label-width="100px">
        <el-form-item label="教师姓名">
          <el-input v-model="currentTeacher.realName" disabled />
        </el-form-item>
        <el-form-item label="分配班级">
          <el-select v-model="selectedClassId" placeholder="请选择班级">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
          <div class="tip">注意：选择班级后，该教师将成为该班级的班主任。如果该班级已有班主任，将被覆盖。</div>
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
import { ElMessage } from 'element-plus'
import { getTeacherList } from '@/api/user'
import { getClassList, updateClass } from '@/api/class'

const loading = ref(false)
const dialogVisible = ref(false)
const teacherList = ref([])
const classList = ref([])

const currentTeacher = reactive({
  id: null,
  realName: ''
})
const selectedClassId = ref(null)

// 获取教师列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTeacherList()
    teacherList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取班级列表
const fetchClasses = async () => {
  try {
    const res = await getClassList()
    classList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 获取教师管理的班级
const getTeacherClasses = (teacherId) => {
  return classList.value.filter(c => c.teacherId === teacherId)
}

const handleAssign = (row) => {
  currentTeacher.id = row.id
  currentTeacher.realName = row.realName
  selectedClassId.value = null
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!selectedClassId.value) {
    ElMessage.warning('请选择班级')
    return
  }
  
  try {
    const targetClass = classList.value.find(c => c.id === selectedClassId.value)
    if (!targetClass) return

    // 构造更新数据
    const updateData = {
      id: targetClass.id,
      className: targetClass.className,
      grade: targetClass.grade,
      teacherId: currentTeacher.id
    }

    await updateClass(updateData)
    ElMessage.success('设置成功')
    dialogVisible.value = false
    
    // 刷新数据
    await fetchClasses()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
  fetchClasses()
})
</script>

<style scoped>
.teacher-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.class-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.no-class {
  color: #999;
  font-size: 12px;
}

.tip {
  font-size: 12px;
  color: #e6a23c;
  margin-top: 5px;
  line-height: 1.5;
}
</style>

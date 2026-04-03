<template>
  <div class="growth-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成长档案</span>
          <el-select v-model="currentStudentId" placeholder="请选择学生" style="width: 200px" @change="handleStudentChange">
            <el-option v-for="student in studentList" :key="student.id" :label="`${student.name} (${student.className || '未分班'})`" :value="student.id" />
          </el-select>
        </div>
      </template>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="成绩记录" name="score">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddScore" v-if="!isParent">新增成绩</el-button>
          </div>
          <el-table :data="scoreList" border>
            <el-table-column prop="subject" label="科目" width="120" />
            <el-table-column prop="score" label="分数" width="100" />
            <el-table-column prop="examType" label="考试类型" width="120" />
            <el-table-column prop="examDate" label="考试日期" width="120" />
            <el-table-column prop="semester" label="学期" width="100" />
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="操作" width="150" fixed="right" v-if="!isParent">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleDeleteScore(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="综合素质评价" name="evaluation">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddEvaluation" v-if="!isParent">新增评价</el-button>
          </div>
          <el-table :data="evaluationList" border>
            <el-table-column prop="semester" label="学期" width="100" />
            <el-table-column prop="moralScore" label="德育" width="80" />
            <el-table-column prop="intellectualScore" label="智育" width="80" />
            <el-table-column prop="physicalScore" label="体育" width="80" />
            <el-table-column prop="aestheticScore" label="美育" width="80" />
            <el-table-column prop="laborScore" label="劳育" width="80" />
            <el-table-column prop="totalScore" label="总分" width="80" />
            <el-table-column prop="level" label="等级" width="100" />
            <el-table-column prop="remark" label="评语" />
            <el-table-column label="操作" width="150" fixed="right" v-if="!isParent">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleDeleteEvaluation(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="教师评语" name="comment">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddComment" v-if="!isParent">新增评语</el-button>
          </div>
          <el-table :data="commentList" border>
            <el-table-column prop="commentType" label="评语类型" width="120" />
            <el-table-column prop="content" label="评语内容" />
            <el-table-column prop="commentDate" label="评语日期" width="120" />
            <el-table-column label="操作" width="150" fixed="right" v-if="!isParent">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleDeleteComment(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="获奖记录" name="award">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddAward" v-if="!isParent">新增获奖</el-button>
          </div>
          <el-table :data="awardList" border>
            <el-table-column prop="awardName" label="奖项名称" width="200" />
            <el-table-column prop="awardLevel" label="奖项级别" width="120" />
            <el-table-column prop="awardDate" label="获奖日期" width="120" />
            <el-table-column prop="issuer" label="颁发机构" width="150" />
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="操作" width="150" fixed="right" v-if="!isParent">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleDeleteAward(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="scoreDialogVisible" title="新增成绩" width="500px">
      <el-form ref="scoreFormRef" :model="scoreForm" :rules="scoreRules" label-width="100px">
        <el-form-item label="科目" prop="subject">
          <el-input v-model="scoreForm.subject" placeholder="请输入科目" />
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-input-number v-model="scoreForm.score" :min="0" :max="100" :precision="2" />
        </el-form-item>
        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="scoreForm.examType" placeholder="请选择考试类型">
            <el-option label="周考" value="周考" />
            <el-option label="月考" value="月考" />
            <el-option label="期中" value="期中" />
            <el-option label="期末" value="期末" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker v-model="scoreForm.examDate" type="date" placeholder="请选择考试日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="scoreForm.semester" placeholder="请选择学期">
            <el-option label="第一学期" value="第一学期" />
            <el-option label="第二学期" value="第二学期" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitScore">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="evaluationDialogVisible" title="新增综合素质评价" width="600px">
      <el-form ref="evaluationFormRef" :model="evaluationForm" :rules="evaluationRules" label-width="100px">
        <el-form-item label="学期" prop="semester">
          <el-select v-model="evaluationForm.semester" placeholder="请选择学期">
            <el-option label="第一学期" value="第一学期" />
            <el-option label="第二学期" value="第二学期" />
          </el-select>
        </el-form-item>
        <el-form-item label="德育得分" prop="moralScore">
          <el-input-number v-model="evaluationForm.moralScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="智育得分" prop="intellectualScore">
          <el-input-number v-model="evaluationForm.intellectualScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="体育得分" prop="physicalScore">
          <el-input-number v-model="evaluationForm.physicalScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="美育得分" prop="aestheticScore">
          <el-input-number v-model="evaluationForm.aestheticScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="劳育得分" prop="laborScore">
          <el-input-number v-model="evaluationForm.laborScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="综合评语" prop="remark">
          <el-input v-model="evaluationForm.remark" type="textarea" :rows="3" placeholder="请输入综合评语" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEvaluation">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="commentDialogVisible" title="新增教师评语" width="500px">
      <el-form ref="commentFormRef" :model="commentForm" :rules="commentRules" label-width="100px">
        <el-form-item label="评语类型" prop="commentType">
          <el-select v-model="commentForm.commentType" placeholder="请选择评语类型">
            <el-option label="日常评语" value="daily" />
            <el-option label="月度评语" value="monthly" />
            <el-option label="学期评语" value="term" />
          </el-select>
        </el-form-item>
        <el-form-item label="评语内容" prop="content">
          <el-input v-model="commentForm.content" type="textarea" :rows="4" placeholder="请输入评语内容" />
        </el-form-item>
        <el-form-item label="评语日期" prop="commentDate">
          <el-date-picker v-model="commentForm.commentDate" type="date" placeholder="请选择评语日期" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="commentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitComment">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="awardDialogVisible" title="新增获奖记录" width="500px">
      <el-form ref="awardFormRef" :model="awardForm" :rules="awardRules" label-width="100px">
        <el-form-item label="奖项名称" prop="awardName">
          <el-input v-model="awardForm.awardName" placeholder="请输入奖项名称" />
        </el-form-item>
        <el-form-item label="奖项级别" prop="awardLevel">
          <el-select v-model="awardForm.awardLevel" placeholder="请选择奖项级别">
            <el-option label="国家级" value="国家级" />
            <el-option label="省级" value="省级" />
            <el-option label="市级" value="市级" />
            <el-option label="校级" value="校级" />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker v-model="awardForm.awardDate" type="date" placeholder="请选择获奖日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="颁发机构" prop="issuer">
          <el-input v-model="awardForm.issuer" placeholder="请输入颁发机构" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="awardForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="awardDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAward">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentPage, getMyStudents, getTeacherStudents } from '@/api/student'
import { getScoresByStudentId, addScore, deleteScore } from '@/api/score'
import { getEvaluationsByStudentId, addEvaluation, deleteEvaluation } from '@/api/evaluation'
import { getCommentsByStudentId, addComment, deleteComment } from '@/api/comment'
import { getAwardsByStudentId, addAward, deleteAward } from '@/api/award'

const userStore = useUserStore()
const isParent = computed(() => userStore.userInfo?.role === 'PARENT')
const isTeacher = computed(() => userStore.userInfo?.role === 'TEACHER')

const activeTab = ref('score')
const currentStudentId = ref(null)
const studentList = ref([])

const scoreList = ref([])
const evaluationList = ref([])
const commentList = ref([])
const awardList = ref([])

const scoreDialogVisible = ref(false)
const evaluationDialogVisible = ref(false)
const commentDialogVisible = ref(false)
const awardDialogVisible = ref(false)

const scoreFormRef = ref(null)
const evaluationFormRef = ref(null)
const commentFormRef = ref(null)
const awardFormRef = ref(null)

const scoreForm = reactive({
  studentId: null,
  subject: '',
  score: 0,
  examType: '',
  examDate: '',
  semester: ''
})

const evaluationForm = reactive({
  studentId: null,
  semester: '',
  moralScore: 0,
  intellectualScore: 0,
  physicalScore: 0,
  aestheticScore: 0,
  laborScore: 0,
  remark: ''
})

const commentForm = reactive({
  studentId: null,
  commentType: '',
  content: '',
  commentDate: ''
})

const awardForm = reactive({
  studentId: null,
  awardName: '',
  awardLevel: '',
  awardDate: '',
  issuer: '',
  remark: ''
})

const scoreRules = {
  subject: [{ required: true, message: '请输入科目', trigger: 'blur' }],
  score: [{ required: true, message: '请输入分数', trigger: 'blur' }],
  examType: [{ required: true, message: '请输入考试类型', trigger: 'blur' }],
  examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }]
}

const evaluationRules = {
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }]
}

const commentRules = {
  commentType: [{ required: true, message: '请选择评语类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入评语内容', trigger: 'blur' }],
  commentDate: [{ required: true, message: '请选择评语日期', trigger: 'change' }]
}

const awardRules = {
  awardName: [{ required: true, message: '请输入奖项名称', trigger: 'blur' }],
  awardLevel: [{ required: true, message: '请选择奖项级别', trigger: 'change' }],
  awardDate: [{ required: true, message: '请选择获奖日期', trigger: 'change' }]
}

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
      fetchGrowthData()
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchGrowthData = async () => {
  if (!currentStudentId.value) return

  try {
    const [scoreRes, evaluationRes, commentRes, awardRes] = await Promise.all([
      getScoresByStudentId(currentStudentId.value),
      getEvaluationsByStudentId(currentStudentId.value),
      getCommentsByStudentId(currentStudentId.value),
      getAwardsByStudentId(currentStudentId.value)
    ])
    scoreList.value = scoreRes.data
    evaluationList.value = evaluationRes.data
    commentList.value = commentRes.data
    awardList.value = awardRes.data
  } catch (error) {
    console.error(error)
  }
}

const handleStudentChange = () => {
  fetchGrowthData()
}

const handleTabChange = () => {}

const handleAddScore = () => {
  Object.assign(scoreForm, {
    studentId: currentStudentId.value,
    subject: '',
    score: 0,
    examType: '',
    examDate: '',
    semester: ''
  })
  scoreDialogVisible.value = true
}

const handleDeleteScore = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该成绩记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteScore(row.id)
    ElMessage.success('删除成功')
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmitScore = async () => {
  const valid = await scoreFormRef.value.validate()
  if (!valid) return

  try {
    await addScore(scoreForm)
    ElMessage.success('新增成功')
    scoreDialogVisible.value = false
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleAddEvaluation = () => {
  Object.assign(evaluationForm, {
    studentId: currentStudentId.value,
    semester: '',
    moralScore: 0,
    intellectualScore: 0,
    physicalScore: 0,
    aestheticScore: 0,
    laborScore: 0,
    remark: ''
  })
  evaluationDialogVisible.value = true
}

const handleDeleteEvaluation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteEvaluation(row.id)
    ElMessage.success('删除成功')
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmitEvaluation = async () => {
  const valid = await evaluationFormRef.value.validate()
  if (!valid) return

  try {
    evaluationForm.totalScore = evaluationForm.moralScore + evaluationForm.intellectualScore + evaluationForm.physicalScore + evaluationForm.aestheticScore + evaluationForm.laborScore
    if (evaluationForm.totalScore >= 450) {
      evaluationForm.level = '优秀'
    } else if (evaluationForm.totalScore >= 400) {
      evaluationForm.level = '良好'
    } else if (evaluationForm.totalScore >= 350) {
      evaluationForm.level = '合格'
    } else {
      evaluationForm.level = '待改进'
    }
    await addEvaluation(evaluationForm)
    ElMessage.success('新增成功')
    evaluationDialogVisible.value = false
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleAddComment = () => {
  Object.assign(commentForm, {
    studentId: currentStudentId.value,
    commentType: '',
    content: '',
    commentDate: ''
  })
  commentDialogVisible.value = true
}

const handleDeleteComment = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评语吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteComment(row.id)
    ElMessage.success('删除成功')
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmitComment = async () => {
  const valid = await commentFormRef.value.validate()
  if (!valid) return

  try {
    await addComment(commentForm)
    ElMessage.success('新增成功')
    commentDialogVisible.value = false
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleAddAward = () => {
  Object.assign(awardForm, {
    studentId: currentStudentId.value,
    awardName: '',
    awardLevel: '',
    awardDate: '',
    issuer: '',
    remark: ''
  })
  awardDialogVisible.value = true
}

const handleDeleteAward = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该获奖记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAward(row.id)
    ElMessage.success('删除成功')
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmitAward = async () => {
  const valid = await awardFormRef.value.validate()
  if (!valid) return

  try {
    await addAward(awardForm)
    ElMessage.success('新增成功')
    awardDialogVisible.value = false
    fetchGrowthData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.growth-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tab-header {
  margin-bottom: 20px;
}
</style>

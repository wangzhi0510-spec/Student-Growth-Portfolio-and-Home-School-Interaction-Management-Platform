import request from '@/utils/request'

export const getEvaluationsByStudentId = (studentId) => {
  return request({
    url: `/evaluation/student/${studentId}`,
    method: 'get'
  })
}

export const addEvaluation = (data) => {
  return request({
    url: '/evaluation',
    method: 'post',
    data
  })
}

export const updateEvaluation = (data) => {
  return request({
    url: '/evaluation',
    method: 'put',
    data
  })
}

export const deleteEvaluation = (id) => {
  return request({
    url: `/evaluation/${id}`,
    method: 'delete'
  })
}

import request from '@/utils/request'

export const getScoresByStudentId = (studentId) => {
  return request({
    url: `/score/student/${studentId}`,
    method: 'get'
  })
}

export const getScorePage = (params) => {
  return request({
    url: '/score/page',
    method: 'get',
    params
  })
}

export const addScore = (data) => {
  return request({
    url: '/score',
    method: 'post',
    data
  })
}

export const updateScore = (data) => {
  return request({
    url: '/score',
    method: 'put',
    data
  })
}

export const deleteScore = (id) => {
  return request({
    url: `/score/${id}`,
    method: 'delete'
  })
}

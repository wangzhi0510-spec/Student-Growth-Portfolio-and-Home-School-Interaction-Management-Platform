import request from '@/utils/request'

export const getCommentsByStudentId = (studentId) => {
  return request({
    url: `/comment/student/${studentId}`,
    method: 'get'
  })
}

export const addComment = (data) => {
  return request({
    url: '/comment',
    method: 'post',
    data
  })
}

export const updateComment = (data) => {
  return request({
    url: '/comment',
    method: 'put',
    data
  })
}

export const deleteComment = (id) => {
  return request({
    url: `/comment/${id}`,
    method: 'delete'
  })
}

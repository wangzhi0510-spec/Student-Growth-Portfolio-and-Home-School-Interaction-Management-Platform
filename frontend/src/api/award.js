import request from '@/utils/request'

export const getAwardsByStudentId = (studentId) => {
  return request({
    url: `/award/student/${studentId}`,
    method: 'get'
  })
}

export const addAward = (data) => {
  return request({
    url: '/award',
    method: 'post',
    data
  })
}

export const updateAward = (data) => {
  return request({
    url: '/award',
    method: 'put',
    data
  })
}

export const deleteAward = (id) => {
  return request({
    url: `/award/${id}`,
    method: 'delete'
  })
}

import request from '@/utils/request'

export const getStudentPage = (params) => {
  return request({
    url: '/student/page',
    method: 'get',
    params
  })
}

export const getMyStudents = () => {
  return request({
    url: '/student/parent/my',
    method: 'get'
  })
}

export const getTeacherStudents = () => {
  return request({
    url: '/student/teacher/my',
    method: 'get'
  })
}

export const bindStudent = (studentNo) => {
  return request({
    url: '/student/bind',
    method: 'post',
    params: { studentNo }
  })
}

export const unbindStudent = (studentId) => {
  return request({
    url: '/student/unbind',
    method: 'post',
    params: { studentId }
  })
}

export const addStudent = (data) => {
  return request({
    url: '/student',
    method: 'post',
    data
  })
}

export const updateStudent = (data) => {
  return request({
    url: '/student',
    method: 'put',
    data
  })
}

export const deleteStudent = (id) => {
  return request({
    url: `/student/${id}`,
    method: 'delete'
  })
}

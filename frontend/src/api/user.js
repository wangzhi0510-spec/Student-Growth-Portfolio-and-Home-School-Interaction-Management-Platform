import request from '@/utils/request'

export const getTeacherList = () => {
  return request({
    url: '/user/list/teacher',
    method: 'get'
  })
}

export const getUserPage = (params) => {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export const getUserById = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export const addUser = (data) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export const updateUserProfile = (data) => {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

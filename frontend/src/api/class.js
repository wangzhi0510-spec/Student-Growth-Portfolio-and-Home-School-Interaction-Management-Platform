import request from '@/utils/request'

export const getClassList = () => {
  return request({
    url: '/class/list',
    method: 'get'
  })
}

export const getClassById = (id) => {
  return request({
    url: `/class/${id}`,
    method: 'get'
  })
}

export const addClass = (data) => {
  return request({
    url: '/class',
    method: 'post',
    data
  })
}

export const updateClass = (data) => {
  return request({
    url: '/class',
    method: 'put',
    data
  })
}

export const deleteClass = (id) => {
  return request({
    url: `/class/${id}`,
    method: 'delete'
  })
}

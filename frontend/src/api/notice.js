import request from '@/utils/request'

export const getNoticePage = (params) => {
  return request({
    url: '/notice/page',
    method: 'get',
    params
  })
}

export const addNotice = (data) => {
  return request({
    url: '/notice',
    method: 'post',
    data
  })
}

export const deleteNotice = (id) => {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

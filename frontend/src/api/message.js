import request from '@/utils/request'

export const getReceivedMessages = (params) => {
  return request({
    url: '/message/received',
    method: 'get',
    params
  })
}

export const getSentMessages = (params) => {
  return request({
    url: '/message/sent',
    method: 'get',
    params
  })
}

export const getUnreadCount = () => {
  return request({
    url: '/message/unread-count',
    method: 'get'
  })
}

export const sendMessage = (data) => {
  return request({
    url: '/message',
    method: 'post',
    data
  })
}

export const markAsRead = (id) => {
  return request({
    url: `/message/${id}/read`,
    method: 'put'
  })
}

export const deleteMessage = (id) => {
  return request({
    url: `/message/${id}`,
    method: 'delete'
  })
}

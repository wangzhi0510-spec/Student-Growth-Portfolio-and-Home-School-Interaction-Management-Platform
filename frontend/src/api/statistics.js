import request from '@/utils/request'

export const getDashboardStats = () => {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

export const getScoreStatistics = (studentId) => {
  return request({
    url: `/statistics/score/${studentId}`,
    method: 'get'
  })
}

export const getScoreTrend = (studentId, params) => {
  return request({
    url: `/statistics/score/trend/${studentId}`,
    method: 'get',
    params
  })
}

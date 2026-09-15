import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const BASE_URL = '/xxl-job-admin'

const request = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
})

request.interceptors.response.use(
  function (response) {
    // 检测响应是否为 HTML（后端 302 重定向到 /toLogin 后的登录页面）
    var data = response.data
    if (typeof data === 'string' && data.indexOf('<!DOCTYPE') !== -1) {
      sessionStorage.removeItem('xxl-job-login')
      var currentPath = router.currentRoute.fullPath
      if (router.currentRoute.name !== 'Login') {
        router.replace({ path: '/login', query: { redirect: currentPath } })
      }
      Message.error('登录已过期，请重新登录')
      return Promise.reject(new Error('登录已过期'))
    }
    return response
  },
  function (error) {
    Message.error(error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export function post(url, data) {
  if (!data) data = {}
  return request.post(url, new URLSearchParams(data).toString())
}

export function get(url, params) {
  if (!params) params = {}
  return request.get(url, { params: params })
}

export default request

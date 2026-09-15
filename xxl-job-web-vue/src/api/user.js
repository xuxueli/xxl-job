import { post } from '@/utils/request'

export function pageList(params) {
  return post('/user/pageList', params)
}

export function addUser(user) {
  return post('/user/add', user)
}

export function updateUser(user) {
  return post('/user/update', user)
}

export function removeUser(id) {
  return post('/user/remove', { id: id })
}

export function updatePwd(data) {
  return post('/user/updatePwd', data)
}

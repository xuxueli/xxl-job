import { post } from '@/utils/request'

export function login(userName, password, ifRemember) {
  return post('/login', {
    userName: userName,
    password: password,
    ifRemember: ifRemember ? 'on' : ''
  })
}

export function logout() {
  return post('/logout')
}

import { post } from '@/utils/request'

export function pageList(params) {
  return post('/jobgroup/pageList', params)
}

export function loadById(id) {
  return post('/jobgroup/loadById', { id: id })
}

export function save(jobGroup) {
  return post('/jobgroup/save', jobGroup)
}

export function update(jobGroup) {
  return post('/jobgroup/update', jobGroup)
}

export function remove(id) {
  return post('/jobgroup/remove', { id: id })
}

export function findAll() {
  return post('/jobgroup/pageList', { start: 0, length: 1000 })
}

import { post } from '@/utils/request'

export function pageList(params) {
  return post('/jobinfo/pageList', params)
}

export function addJob(jobInfo) {
  return post('/jobinfo/add', jobInfo)
}

export function updateJob(jobInfo) {
  return post('/jobinfo/update', jobInfo)
}

export function removeJob(id) {
  return post('/jobinfo/remove', { id: id })
}

export function stopJob(id) {
  return post('/jobinfo/stop', { id: id })
}

export function startJob(id) {
  return post('/jobinfo/start', { id: id })
}

export function triggerJob(id, executorParam, addressList) {
  return post('/jobinfo/trigger', {
    id: id,
    executorParam: executorParam,
    addressList: addressList
  })
}

export function nextTriggerTime(scheduleType, scheduleConf) {
  return post('/jobinfo/nextTriggerTime', {
    scheduleType: scheduleType,
    scheduleConf: scheduleConf
  })
}

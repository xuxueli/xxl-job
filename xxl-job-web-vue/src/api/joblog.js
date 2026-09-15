import { post } from '@/utils/request'

export function pageList(params) {
  return post('/joblog/pageList', params)
}

export function getJobsByGroup(jobGroup) {
  return post('/joblog/getJobsByGroup', { jobGroup: jobGroup })
}

export function logDetailCat(logId, fromLineNum) {
  return post('/joblog/logDetailCat', { logId: logId, fromLineNum: fromLineNum })
}

export function logKill(id) {
  return post('/joblog/logKill', { id: id })
}

export function clearLog(jobGroup, jobId, type) {
  return post('/joblog/clearLog', { jobGroup: jobGroup, jobId: jobId, type: type })
}

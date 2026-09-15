import { post } from '@/utils/request'

export function chartInfo(startDate, endDate) {
  return post('/chartInfo', {
    startDate: startDate,
    endDate: endDate
  })
}

package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.pojo.vo.JobLogVO;

/**
 * 任务报警服务
 *
 * @author Rong.Jia
 * @date 2023/05/14
 */
public interface JobAlarmService {

    /**
     * 发送报警
     *
     * @param jobInfoVO 任务信息VO
     * @param jobLogVO  任务日志VO
     * @return boolean
     */
    Boolean sendAlarm(JobInfoVO jobInfoVO, JobLogVO jobLogVO);






    
}

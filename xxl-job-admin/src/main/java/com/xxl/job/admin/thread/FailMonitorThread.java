package com.xxl.job.admin.thread;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.admin.common.enums.TriggerTypeEnum;
import com.xxl.job.admin.common.pojo.dto.TriggerLogDTO;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.pojo.vo.JobLogVO;
import com.xxl.job.admin.service.JobAlarmService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 任务失败监听线程
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class FailMonitorThread extends AbstractThreadListener implements Ordered {

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private TriggerThreadPool jobTriggerThreadPool;

    @Autowired
    private JobAlarmService jobAlarmService;

    private Thread monitorThread;
    private volatile boolean toStop = false;

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void start() {

        monitorThread = new Thread(() -> {

            // monitor
            while (!toStop) {
                try {

                    List<Long> failLogIds = jobLogService.findFailJobLogIds(1000);
                    if (CollectionUtil.isNotEmpty(failLogIds)) {
                        for (Long failLogId : failLogIds) {

                            // lock log
                            int lockRet = jobLogService.updateAlarmStatus(failLogId, 0, -1);
                            if (lockRet < 1) continue;

                            JobLogVO jobLogVO = jobLogService.queryById(failLogId);
                            JobInfoVO info = jobLogVO.getJob();

                            // 1、fail retry monitor
                            if (jobLogVO.getExecutorFailRetryCount() > 0) {
                                jobTriggerThreadPool.addTrigger(info.getId(), TriggerTypeEnum.RETRY,
                                        (jobLogVO.getExecutorFailRetryCount() - 1), jobLogVO.getExecutorShardingParam(),
                                        jobLogVO.getExecutorParam(), Collections.emptyList());
                                String retryMessage = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试触发<<<<<<<<<<< </span><br>";
                                jobLogVO.setTriggerMessage(jobLogVO.getTriggerMessage() + retryMessage);

                                TriggerLogDTO triggerLogDTO = new TriggerLogDTO();
                                BeanUtil.copyProperties(jobLogVO, triggerLogDTO);
                                triggerLogDTO.setTriggerMessage(jobLogVO.getTriggerMessage() + retryMessage);
                                jobLogService.updateTriggerInfo(triggerLogDTO);
                            }

                            // 2、fail alarm monitor, 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
                            int newAlarmStatus = 0;
                            if (ObjectUtil.isNotEmpty(info)) {
                                newAlarmStatus = jobAlarmService.sendAlarm(info, jobLogVO) ? 2 : 3;
                            } else {
                                newAlarmStatus = 1;
                            }
                            jobLogService.updateAlarmStatus(failLogId, -1, newAlarmStatus);
                        }
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        log.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }

            }

            log.info(">>>>>>>>>>> xxl-job, job fail monitor thread stop");

        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobFailMonitorHelper");
        monitorThread.start();
    }

    @Override
    public void stop() {
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}

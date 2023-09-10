package com.xxl.job.admin.thread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.admin.common.config.XxlJobAdminProperties;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.pojo.dto.LogReportDTO;
import com.xxl.job.admin.common.pojo.vo.JobLogReportVO;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.admin.service.LogReportService;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 任务日志报告线程
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class LogReportThread extends AbstractThreadListener implements Ordered {

    @Autowired
    private LogReportService logReportService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private XxlJobAdminProperties xxlJobAdminProperties;

    private Thread logThread;
    private volatile boolean toStop = false;

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public void start() {

        logThread = new Thread(() -> {

            log.info(">>>>>>>>>>> xxl-job, job log report thread start...");

            // last clean log time
            long lastCleanLogTime = 0;


            while (!toStop) {

                // 1、log-report refresh: refresh log report in 3 days
                try {

                    for (int i = 0; i < 3; i++) {

                        DateTime from = DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -i));
                        DateTime to = DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -i));

                        // refresh log-report every minute
                        LogReportDTO logReportDTO = new LogReportDTO();
                        logReportDTO.setTriggerDay(from.getTime());
                        logReportDTO.setRunningCount(NumberConstant.ZERO.longValue());
                        logReportDTO.setSucCount(NumberConstant.ZERO.longValue());
                        logReportDTO.setFailCount(NumberConstant.ZERO.longValue());


                        JobLogReportVO jobLogReportVO = jobLogService.queryLogReportByTriggerTime(from.getTime(), to.getTime());
                        if (ObjectUtil.isNotEmpty(jobLogReportVO)) {
                            Long triggerDayCount = jobLogReportVO.getTriggerDayCount();
                            Long triggerDayCountRunning = jobLogReportVO.getTriggerDayCountRunning();
                            Long triggerDayCountSuc = jobLogReportVO.getTriggerDayCountSuc();
                            Long triggerDayCountFail = jobLogReportVO.getTriggerDayCountFail();

                            logReportDTO.setRunningCount(triggerDayCountRunning);
                            logReportDTO.setSucCount(triggerDayCountSuc);
                            logReportDTO.setFailCount(triggerDayCountFail);
                        }
                        logReportService.syncLogReport(logReportDTO);
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        log.error(">>>>>>>>>>> xxl-job, job log report thread error: ", e);
                    }
                }

                // 2、log-clean: switch open & once each day
                if (xxlJobAdminProperties.getLogRetentionDay() > 0 && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {

                    // expire-time
                    Date clearBeforeTime = DateUtil.offsetDay(DateUtil.date(), -1 * xxlJobAdminProperties.getLogRetentionDay());

                    // clean expired log
                    List<Long> logIds = null;
                    do {
                        logIds = jobLogService.queryClearLogIds(null, null,
                                clearBeforeTime.getTime(), NumberConstant.ZERO.longValue(), 1000);
                        if (CollectionUtil.isNotEmpty(logIds)) {
                            jobLogService.clearLog(logIds);
                        }
                    } while (CollectionUtil.isNotEmpty(logIds));

                    // update clean time
                    lastCleanLogTime = System.currentTimeMillis();
                }

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info(">>>>>>>>>>> xxl-job, job log report thread stop");
        });
        logThread.setDaemon(true);
        logThread.setName("xxl-job, admin JobLogReportHelper");
        logThread.start();
    }

    @Override
    public void stop() {
        toStop = true;
        // interrupt and wait
        logThread.interrupt();
        try {
            logThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}

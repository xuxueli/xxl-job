package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.Constants;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobRegistryDao;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobFailMonitorHelper {
    private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);

    private final MailUtil mailUtil;

    private final IXxlJobLogDao xxlJobLogDao;
    private final IXxlJobInfoDao xxlJobInfoDao;
    private final IXxlJobRegistryDao xxlJobRegistryDao;
    private final IXxlJobGroupDao xxlJobGroupDao;

    private boolean toStop = false;

    public void start() {
        Thread monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!toStop) {
                    try {
                        logger.info(">>>>>>>>>>> job monitor beat ... ");
                        Integer jobLogId = Constants.queue.take();
                        if (jobLogId != null && jobLogId > 0) {
                            logger.debug(">>>>>>>>>>> job monitor heat success, JobLogId:{}", jobLogId);
                            XxlJobLog log = xxlJobLogDao.load(jobLogId);
                            if (log != null) {
                                if (ReturnT.SUCCESS_CODE == log.getTriggerCode() && log.getHandleCode() == 0) {
                                    // running
                                    try {
                                        TimeUnit.SECONDS.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    JobFailMonitorHelper.monitor(jobLogId);
                                }
                                if (ReturnT.SUCCESS_CODE == log.getTriggerCode() && ReturnT.SUCCESS_CODE == log.getHandleCode()) {
                                    // pass
                                }
                                if (ReturnT.FAIL_CODE == log.getTriggerCode() || ReturnT.FAIL_CODE == log.getHandleCode()) {
                                    XxlJobInfo info = xxlJobInfoDao.loadById(log.getJobId());
                                    if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {

                                        Set<String> emailSet = new HashSet<>(Arrays.asList(info.getAlarmEmail().split(",")));
                                        for (String email : emailSet) {
                                            String title = "《调度监控报警》(任务调度中心XXL-JOB)";
                                            XxlJobGroup group = xxlJobGroupDao.load(info.getJobGroup());
                                            String content = MessageFormat.format("任务调度失败, 执行器名称:{0}, 任务描述:{1}.", group != null ? group.getTitle() : "null", info.getJobDesc());
                                            mailUtil.sendMail(email, title, content, false, null);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("job monitor error:{}", e);
                    }
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;
        //monitorThread.interrupt();
    }

    // producer
    public static void monitor(int jobLogId) {
        Constants.queue.offer(jobLogId);
    }

}

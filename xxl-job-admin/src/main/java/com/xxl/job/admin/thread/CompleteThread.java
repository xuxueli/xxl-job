package com.xxl.job.admin.thread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.xxl.job.admin.common.pojo.dto.HandleLogDTO;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务完成线程
 * job lose-monitor instance
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class CompleteThread extends AbstractThreadListener implements Ordered {

    @Autowired
    private JobLogService jobLogService;

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile boolean toStop = false;

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public void start() {

        // for callback
        callbackThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3000),
                r -> new Thread(r, "xxl-job, admin LosedMonitor-callbackThreadPool-" + r.hashCode()),
                (r, executor) -> {
                    r.run();
                    log.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
                });


        // for monitor
        monitorThread = new Thread(() -> {

            // wait for JobTriggerPoolHelper-init
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                if (!toStop) {
                    log.error(e.getMessage(), e);
                }
            }

            log.info(">>>>>>>>>>> xxl-job, LosedMonitor start...");

            // monitor
            while (!toStop) {
                try {
                    // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                    List<Long> lostJobIds = jobLogService.queryLostJobIds(DateUtil.offsetMinute(DateUtil.date(), -10).getTime());
                    if (CollectionUtil.isNotEmpty(lostJobIds)) {
                        for (Long logId : lostJobIds) {
                            HandleLogDTO handleLogDTO = new HandleLogDTO();
                            handleLogDTO.setId(logId);
                            handleLogDTO.setHandleTime(DateUtil.current());
                            handleLogDTO.setHandleCode(ResponseEnum.ERROR.getCode());
                            handleLogDTO.setHandleMessage(ResponseEnum.LOST_FAIL.getMessage());
                            jobLogService.updateHandleInfo(handleLogDTO);
                        }
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(">>>>>>>>>>> xxl-job, job fail monitor thread error: ", e);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }

            }
            log.info(">>>>>>>>>>> xxl-job, LosedMonitor stop");
        });

        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin LosedMonitor");
        monitorThread.start();
    }

    @Override
    public void stop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void pushTask(Runnable task) {
        callbackThreadPool.execute(task);
    }


}

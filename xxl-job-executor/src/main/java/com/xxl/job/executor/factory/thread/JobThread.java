package com.xxl.job.executor.factory.thread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.context.XxlJobContext;
import com.xxl.job.executor.context.XxlJobHelper;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.repository.XxlJobRepository;
import com.xxl.job.executor.utils.JobLogUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 任务线程
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class JobThread extends Thread {

    private Long jobId;
    private JobHandler handler;
    private LinkedBlockingQueue<TriggerParam> triggerQueue;
    private XxlJobRepository xxlJobRepository;

    /**
     * avoid repeat trigger for the same TRIGGER_LOG_ID
     */
    private Set<Long> triggerLogIdSet;

    private volatile boolean toStop = false;
    private String stopReason;

    /**
     * if running job
     */
    private boolean running = false;

    /**
     * idel times
     */
    private int idleTimes = 0;

    public JobThread(Long jobId, JobHandler handler, XxlJobRepository xxlJobRepository) {
        this.jobId = jobId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingQueue<>();
        this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<>());
        this.xxlJobRepository = xxlJobRepository;

        // assign job thread name
        this.setName("xxl-job, JobThread-" + jobId + "-" + System.currentTimeMillis());
    }

    public JobHandler getHandler() {
        return handler;
    }

    /**
     * new trigger to queue
     *
     * @param triggerParam 触发参数
     * @return {@link ResponseVO}
     */
    public ResponseVO<Void> pushTriggerQueue(TriggerParam triggerParam) {
        // avoid repeat
        if (triggerLogIdSet.contains(triggerParam.getLogId())) {
            log.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
            return ResponseVO.error("repeate trigger job, logId:" + triggerParam.getLogId());
        }

        triggerLogIdSet.add(triggerParam.getLogId());
        triggerQueue.add(triggerParam);
        return ResponseVO.success();
    }

    /**
     * kill job thread
     *
     * @param stopReason 停止原因
     */
    public void toStop(String stopReason) {

        /*
         * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
         * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
         * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
         */
        this.toStop = true;
        this.stopReason = stopReason;
    }

    /**
     * is running job
     *
     * @return
     */
    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size() > 0;
    }

    @Override
    public void run() {

        // init
        try {
            handler.init();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // execute
        while (!toStop) {
            running = false;
            idleTimes++;

            TriggerParam triggerParam = null;
            try {
                // to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
                triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (triggerParam != null) {
                    running = true;
                    idleTimes = 0;
                    triggerLogIdSet.remove(triggerParam.getLogId());

                    // log filename, like "logPath/yyyy-MM-dd/9999.log"
                    String logFileName = JobLogUtils.makeLogFileName(new Date(triggerParam.getLogDateTime()), triggerParam.getLogId());
                    XxlJobContext xxlJobContext = new XxlJobContext(
                            triggerParam.getJobId(),
                            triggerParam.getExecutorParams(),
                            logFileName,
                            triggerParam.getBroadcastIndex(),
                            triggerParam.getBroadcastTotal());

                    // init job context
                    XxlJobContext.setJobContext(xxlJobContext);

                    // execute
                    XxlJobHelper.log("<br>----------- xxl-job job execute start -----------<br>----------- Param: " +
                            (StrUtil.isBlank(xxlJobContext.getJobParam()) ? null : xxlJobContext.getJobParam()) + " -----------<br>");

                    if (triggerParam.getExecutorTimeout() > 0) {
                        // limit timeout
                        Thread futureThread = null;
                        try {
                            FutureTask<Boolean> futureTask = new FutureTask<>(() -> {

                                // init job context
                                XxlJobContext.setJobContext(xxlJobContext);

                                handler.execute(XxlJobHelper.getJobParam());
                                return true;
                            });
                            futureThread = new Thread(futureTask);
                            futureThread.start();

                            futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
                        } catch (TimeoutException e) {

                            XxlJobHelper.log("<br>----------- xxl-job job execute timeout");
                            XxlJobHelper.log(e);

                            // handle result
                            XxlJobHelper.handleTimeout("job execute timeout ");
                        } finally {
                            futureThread.interrupt();
                        }
                    } else {
                        // just execute
                        handler.execute(XxlJobHelper.getJobParam());
                    }

                    // valid execute handle data
                    if (XxlJobContext.getJobContext().getHandleCode() < 0) {
                        XxlJobHelper.handleFail("job handle result lost.");
                    } else {
                        XxlJobContext.getJobContext().setHandleMessage(XxlJobContext.getJobContext().getHandleMessage());
                    }
                    XxlJobHelper.log("<br>----------- xxl-job job execute end(finish) -----------<br>----------- Result: handleCode="
                            + XxlJobContext.getJobContext().getHandleCode()
                            + ", handleMsg = "
                            + XxlJobContext.getJobContext().getHandleMessage()
                    );

                } else {

                    // avoid concurrent trigger causes jobId-lost
                    if (idleTimes > 30 && CollectionUtil.isEmpty(triggerQueue)) {
                        xxlJobRepository.removeJob(jobId, "excutor idel times over limit.");
                    }
                }
            } catch (Throwable e) {
                if (toStop) {
                    XxlJobHelper.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
                }

                // handle result
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String errorMsg = stringWriter.toString();

                XxlJobHelper.handleFail(errorMsg);

                XxlJobHelper.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- xxl-job job execute end(error) -----------");
            } finally {
                if (triggerParam != null) {
                    // callback handler info
                    if (!toStop) {
                        // commonm
                        xxlJobRepository.pushCallBack(new HandleCallbackParam(
                                triggerParam.getLogId(),
                                triggerParam.getLogDateTime(),
                                XxlJobContext.getJobContext().getHandleCode(),
                                XxlJobContext.getJobContext().getHandleMessage())
                        );
                    } else {
                        // is killed
                        xxlJobRepository.pushCallBack(new HandleCallbackParam(
                                triggerParam.getLogId(),
                                triggerParam.getLogDateTime(),
                                ResponseEnum.ERROR.getCode(),
                                stopReason + " [job running, killed]")
                        );
                    }
                }
            }
        }

        // callback trigger request in queue
        while (CollectionUtil.isNotEmpty(triggerQueue)) {
            TriggerParam triggerParam = triggerQueue.poll();
            if (ObjectUtil.isNotNull(triggerParam)) {
                // is killed
                xxlJobRepository.pushCallBack(new HandleCallbackParam(
                        triggerParam.getLogId(),
                        triggerParam.getLogDateTime(),
                        ResponseEnum.ERROR.getCode(),
                        stopReason + " [job not executed, in the job queue, killed.]")
                );
            }
        }

        // destroy
        try {
            handler.destroy();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }

        log.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
    }
}

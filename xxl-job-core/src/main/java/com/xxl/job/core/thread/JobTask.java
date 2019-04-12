package com.xxl.job.core.thread;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.AbstractJobHandler;
import com.xxl.job.core.handler.AbstractMultiJobHandler;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;


/**
 * 具体的job任务
 * @author xuemc 2018-11-13
 */
public class JobTask implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(JobTask.class);

	private int jobId;
	private IJobHandler handler;
	private Set<Integer> triggerLogIdSet;		// avoid repeat trigger for the same TRIGGER_LOG_ID
	private TriggerParam triggerParam;
	private Method jobMethod;  //支持method式job
	
	public JobTask(IJobHandler handler,TriggerParam triggerParam,Method method) {
		this.jobId = triggerParam.getJobId();
		this.handler = handler;
		this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Integer>());
		Assert.notNull(triggerParam, "triggerParam不能为null!");
		this.triggerParam = triggerParam;
		this.jobMethod = method;
	}
	public IJobHandler getHandler() {
		return handler;
	}

    /**
     * new trigger to queue
     *
     * @param triggerParam
     * @return
     */
	public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
		// avoid repeat
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
			return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
		}

		triggerLogIdSet.add(triggerParam.getLogId());
        return ReturnT.SUCCESS;
	}

	@SuppressWarnings("unchecked")
    @Override
	public void run() {

    	// init
    	try {
			handler.init();
		} catch (Throwable e) {
    		logger.error(e.getMessage(), e);
		}

		// execute
        ReturnT<String> executeResult = null;
        try {
			// to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
			triggerLogIdSet.remove(triggerParam.getLogId());

			XxlJobFileAppender.makeIntraDayLogFileName(new Date(triggerParam.getLogDateTim()));
			// log filename, like "logPath/yyyy-MM-dd/9999.log"
			String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
			XxlJobFileAppender.contextHolder.set(logFileName);
			ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));

			// execute
			XxlJobLogger.log("<br>----------- xxl-job job execute start -----------<br>----------- Param:" + triggerParam.getExecutorParams());

			if (triggerParam.getExecutorTimeout() > 0) {
				// limit timeout
				Thread futureThread = null;
				try {
					final TriggerParam triggerParamTmp = triggerParam;
					FutureTask<ReturnT<String>> futureTask = new FutureTask<ReturnT<String>>(new Callable<ReturnT<String>>() {
						
						@Override
						public ReturnT<String> call() throws Exception {
							if(handler instanceof AbstractMultiJobHandler && jobMethod != null){
								ReturnT<String> result = (ReturnT<String>)jobMethod.invoke(handler, triggerParamTmp.getExecutorParams());
								return result;
							}
							else {
								return handler.execute(triggerParamTmp.getExecutorParams());
							}
							
						}
					});
					futureThread = new Thread(futureTask);
					futureThread.start();

					executeResult = futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
				} catch (TimeoutException e) {

					XxlJobLogger.log("<br>----------- xxl-job job execute timeout");
					XxlJobLogger.log(e);

					executeResult = new ReturnT<String>(AbstractJobHandler.FAIL_TIMEOUT.getCode(), "job execute timeout ");
				} finally {
					futureThread.interrupt();
				}
			} else {
				// just execute
				if(handler instanceof AbstractMultiJobHandler && jobMethod != null){
					executeResult = (ReturnT<String>)jobMethod.invoke(handler, triggerParam.getExecutorParams());
				}
				else {
					executeResult = handler.execute(triggerParam.getExecutorParams());
				}
			}

			if (executeResult == null) {
				executeResult = AbstractJobHandler.FAIL;
			}
			XxlJobLogger.log("<br>----------- xxl-job job execute end(finish) -----------<br>----------- ReturnT:" + executeResult);
		} catch (Throwable e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			String errorMsg = stringWriter.toString();
			executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);

			XxlJobLogger.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- xxl-job job execute end(error) -----------");
		} finally {
            if(triggerParam != null) {
            	//after job execution done, clear future reference associated with the job id 
            	int code = executeResult.getCode();
            	String removeReason = code == ReturnT.SUCCESS_CODE ? "job执行成功" : "job执行异常:"+executeResult.getMsg();
            	XxlJobExecutor.removeJobFuture(jobId, removeReason);
                // callback handler info
                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
            }
        }
		// destroy
		try {
			handler.destroy();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		logger.info(">>>>>>>>>>> xxl-job JobTask completed, hashCode:{}", Thread.currentThread());
	}
}

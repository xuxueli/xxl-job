package com.xxl.job.core.thread;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class JobThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);
	
	private IJobHandler handler;
	private LinkedBlockingQueue<TriggerParam> triggerQueue;
	private ConcurrentHashSet<Integer> triggerLogIdSet;		// avoid repeat trigger for the same TRIGGER_LOG_ID

	private boolean toStop = false;
	private String stopReason;

	public JobThread(IJobHandler handler) {
		this.handler = handler;
		triggerQueue = new LinkedBlockingQueue<TriggerParam>();
		triggerLogIdSet = new ConcurrentHashSet<Integer>();
	}
	public IJobHandler getHandler() {
		return handler;
	}

	public void pushTriggerQueue(TriggerParam triggerParam) {
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.debug("repeate trigger job, logId:{}", triggerParam.getLogId());
			return;
		}

		triggerLogIdSet.add(triggerParam.getLogId());
		triggerQueue.add(triggerParam);
	}

	public void toStop(String stopReason) {
		/**
		 * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
		 * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
		 * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
		 */
		this.toStop = true;
		this.stopReason = stopReason;
	}
	
	int i = 1;
	@Override
	public void run() {
		while(!toStop){
			try {
				// to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
				TriggerParam triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerParam!=null) {
					triggerLogIdSet.remove(triggerParam.getLogId());
					
					// parse param
					String[] handlerParams = (triggerParam.getExecutorParams()!=null && triggerParam.getExecutorParams().trim().length()>0)
							? (String[])(Arrays.asList(triggerParam.getExecutorParams().split(",")).toArray()) : null;
					
					// handle job
					ReturnT<String> executeResult = null;
					try {
						// log filename: yyyy-MM-dd/9999.log
						String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());

						XxlJobFileAppender.contextHolder.set(logFileName);
						logger.info("----------- xxl-job job execute start -----------");

						executeResult = handler.execute(handlerParams);
						if (executeResult == null) {
							executeResult = ReturnT.FAIL;
						}
					} catch (Exception e) {
						if (toStop) {
							logger.error("<br>----------- xxl-job toStop, stopReason:{}", stopReason);
						}
						logger.error("JobThread Exception:", e);
						StringWriter out = new StringWriter();
						e.printStackTrace(new PrintWriter(out));

						executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, out.toString());
					}
					logger.info("----------- xxl-job job execute end ----------- <br> Look : ExecutorParams:{}, Code:{}, Msg:{}",
							new Object[]{handlerParams, executeResult.getCode(), executeResult.getMsg()});
					
					// callback handler info
					if (!toStop) {
						// commonm
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogAddress(), executeResult));
					} else {
						// is killed
						ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [业务运行中，被强制终止]");
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogAddress(), stopResult));
					}
				}
			} catch (Exception e) {
				if (toStop) {
					logger.error("<br>----------- xxl-job toStop, stopReason:{}", stopReason);
				}
				logger.error("----------- xxl-job JobThread Exception:", e);
			}
		}
		
		// callback trigger request in queue
		while(triggerQueue !=null && triggerQueue.size()>0){
			TriggerParam triggerParam = triggerQueue.poll();
			if (triggerParam!=null) {
				// is killed
				ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [任务尚未执行，在调度队列中被终止]");
				TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogAddress(), stopResult));
			}
		}
		
		logger.info(">>>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
	}
}

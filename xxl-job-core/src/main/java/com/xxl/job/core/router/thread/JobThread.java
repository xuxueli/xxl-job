package com.xxl.job.core.router.thread;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.IJobHandler.JobHandleStatus;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.util.XxlJobNetCommUtil;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class JobThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);
	
	private IJobHandler handler;
	private LinkedBlockingQueue<RequestModel> triggerQueue;
	private ConcurrentHashSet<Integer> triggerLogIdSet;		// avoid repeat trigger for the same TRIGGER_LOG_ID

	private boolean toStop = false;
	private String stopReason;

	public JobThread(IJobHandler handler) {
		this.handler = handler;
		triggerQueue = new LinkedBlockingQueue<RequestModel>();
		triggerLogIdSet = new ConcurrentHashSet<Integer>();
	}
	public IJobHandler getHandler() {
		return handler;
	}

	public void pushTriggerQueue(RequestModel requestModel) {
		if (triggerLogIdSet.contains(requestModel.getLogId())) {
			logger.info("repeate trigger job, logId:{}", requestModel.getLogId());
			return;
		}

		triggerLogIdSet.add(requestModel.getLogId());
		triggerQueue.add(requestModel);
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
				RequestModel triggerDate = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerDate!=null) {
					triggerLogIdSet.remove(triggerDate.getLogId());
					
					// parse param
					String[] handlerParams = (triggerDate.getExecutorParams()!=null && triggerDate.getExecutorParams().trim().length()>0)
							? (String[])(Arrays.asList(triggerDate.getExecutorParams().split(",")).toArray()) : null;
					
					// handle job
					JobHandleStatus _status = JobHandleStatus.FAIL;
					String _msg = null;

					try {
						XxlJobFileAppender.contextHolder.set(String.valueOf(triggerDate.getLogId()));
						logger.info("----------- xxl-job job handle start -----------");
						_status = handler.execute(handlerParams);
					} catch (Exception e) {
						logger.info("JobThread Exception:", e);
						StringWriter out = new StringWriter();
						e.printStackTrace(new PrintWriter(out));
						_msg = out.toString();
					}
					logger.info("----------- xxl-job job handle end ----------- <br>: ExecutorParams:{}, Status:{}, Msg:{}",
							new Object[]{handlerParams, _status, _msg});
					
					// callback handler info
					if (!toStop) {
						// commonm
						triggerDate.setStatus(_status.name());
						triggerDate.setMsg(_msg);
						TriggerCallbackThread.pushCallBack(triggerDate);
					} else {
						// is killed
						triggerDate.setStatus(JobHandleStatus.FAIL.name());
						triggerDate.setMsg(stopReason + "人工手动终止[业务运行中，被强制终止]");
						TriggerCallbackThread.pushCallBack(triggerDate);
					}
				}
			} catch (Exception e) {
				logger.info("JobThread Exception:", e);
			}
		}
		
		// callback trigger request in queue
		while(triggerQueue !=null && triggerQueue.size()>0){
			RequestModel triggerDate = triggerQueue.poll();
			if (triggerDate!=null) {
				// is killed
				RequestModel callback = new RequestModel();
				callback.setLogAddress(XxlJobNetCommUtil.addressToUrl(triggerDate.getLogAddress()));
				callback.setLogId(triggerDate.getLogId());
				callback.setStatus(JobHandleStatus.FAIL.name());
				callback.setMsg(stopReason + "[任务尚未执行，在调度队列中被终止]");
				TriggerCallbackThread.pushCallBack(callback);
			}
		}
		
		logger.info(">>>>>>>>>>>> xxl-job handlerThrad stoped, hashCode:{}", Thread.currentThread());
	}
}

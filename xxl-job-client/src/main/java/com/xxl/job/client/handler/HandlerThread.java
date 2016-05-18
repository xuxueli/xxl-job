package com.xxl.job.client.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.handler.IJobHandler.JobHandleStatus;
import com.xxl.job.client.log.XxlJobFileAppender;
import com.xxl.job.client.util.HttpUtil;
import com.xxl.job.client.util.HttpUtil.RemoteCallBack;

/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class HandlerThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(HandlerThread.class);
	
	private IJobHandler handler;
	private LinkedBlockingQueue<Map<String, String>> handlerDataQueue;
	private ConcurrentHashSet<String> logIdSet;		// avoid repeat trigger for the same TRIGGER_LOG_ID
	private boolean toStop = false;
	
	public HandlerThread(IJobHandler handler) {
		this.handler = handler;
		handlerDataQueue = new LinkedBlockingQueue<Map<String,String>>();
		logIdSet = new ConcurrentHashSet<String>();
	}
	
	public IJobHandler getHandler() {
		return handler;
	}
	public void toStop() {
		/**
		 * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
		 * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
		 * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
		 */
		this.toStop = true;
	}
	
	public void pushData(Map<String, String> param) {
		if (param.get(HandlerRepository.TRIGGER_LOG_ID)!=null && !logIdSet.contains(param.get(HandlerRepository.TRIGGER_LOG_ID))) {
			handlerDataQueue.offer(param);
		}
	}
	
	int i = 1;
	@Override
	public void run() {
		while(!toStop){
			try {
				Map<String, String> handlerData = handlerDataQueue.poll();
				if (handlerData!=null) {
					i= 0;
					String trigger_log_url = handlerData.get(HandlerRepository.TRIGGER_LOG_URL);
					String trigger_log_id = handlerData.get(HandlerRepository.TRIGGER_LOG_ID);
					String handler_params = handlerData.get(HandlerRepository.HANDLER_PARAMS);
					logIdSet.remove(trigger_log_id);
					
					// parse param
					String[] handlerParams = null; 
					if (handler_params!=null && handler_params.trim().length()>0) {
						handlerParams = handler_params.split(",");
					} else {
						handlerParams = new String[0];
					}
					
					// handle job
					JobHandleStatus _status = JobHandleStatus.FAIL;
					String _msg = null;
					try {
						XxlJobFileAppender.contextHolder.set(trigger_log_id);
						logger.info(">>>>>>>>>>> xxl-job handle start.");
						_status = handler.handle(handlerParams);
					} catch (Exception e) {
						logger.info("HandlerThread Exception:", e);
						StringWriter out = new StringWriter();
						e.printStackTrace(new PrintWriter(out));
						_msg = out.toString();
					}
					logger.info(">>>>>>>>>>> xxl-job handle end, handlerParams:{}, _status:{}, _msg:{}", 
							new Object[]{handlerParams, _status, _msg});

					// callback handler info
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("trigger_log_id", trigger_log_id);
					params.put("status", _status.name());
					params.put("msg", _msg);
					RemoteCallBack callback = null;
					logger.info(">>>>>>>>>>> xxl-job callback start.");
					try {
						callback = HttpUtil.post(trigger_log_url, params);
					} catch (Exception e) {
						logger.info("HandlerThread Exception:", e);
					}
					logger.info(">>>>>>>>>>> xxl-job callback end, params:{}, result:{}", new Object[]{params, callback.toString()});
					
				} else {
					i++;
					logIdSet.clear();
					try {
						TimeUnit.MILLISECONDS.sleep(i * 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (i>5) {
						i= 0;
					}
				}
			} catch (Exception e) {
				logger.info("HandlerThread Exception:", e);
			}
		}
		logger.info(">>>>>>>>>>>> xxl-job handlerThrad stoped, hashCode:{}", Thread.currentThread());
	}
}

package com.xxl.job.client.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.handler.IJobHandler.JobHandleStatus;
import com.xxl.job.client.util.HttpUtil;

/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class HandlerThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(HandlerThread.class);
	
	private IJobHandler handler;
	private LinkedBlockingQueue<Map<String, String>> handlerDataQueue;
	
	public HandlerThread(IJobHandler handler) {
		this.handler = handler;
		handlerDataQueue = new LinkedBlockingQueue<Map<String,String>>();
	}
	
	public void pushData(Map<String, String> param) {
		handlerDataQueue.offer(param);
	}
	
	int i = 1;
	@Override
	public void run() {
		while(true){
			try {
				i++;
				Map<String, String> handlerData = handlerDataQueue.poll();
				if (handlerData!=null) {
					String trigger_log_url = handlerData.get(HandlerRepository.TRIGGER_LOG_URL);
					String trigger_log_id = handlerData.get(HandlerRepository.TRIGGER_LOG_ID);
					String handler_params = handlerData.get(HandlerRepository.HANDLER_PARAMS);
					
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
						_status = handler.handle(handlerParams);
					} catch (Exception e) {
						logger.info("HandlerThread Exception:", e);
						StringWriter out = new StringWriter();
						e.printStackTrace(new PrintWriter(out));
						_msg = out.toString();
					}

					// callback handler info
					String callback_response[] = null;
					try {
						
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(HandlerRepository.TRIGGER_LOG_ID, trigger_log_id);
						params.put(HttpUtil.status, _status.name());
						params.put(HttpUtil.msg, _msg);
						callback_response = HttpUtil.post(trigger_log_url, params);
					} catch (Exception e) {
						logger.info("HandlerThread Exception:", e);
					}
					logger.info("<<<<<<<<<<< xxl-job thread handle, handlerData:{}, callback_status:{}, callback_msg:{}, callback_response:{}, thread:{}", 
							new Object[]{handlerData, _status, _msg, callback_response, this});
				} else {
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
	}
}

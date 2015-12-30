package com.xxl.job.client.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.handler.IJobHandler.JobHandleStatus;
import com.xxl.job.client.util.HttpUtil;
import com.xxl.job.client.util.JacksonUtil;


/**
 * handler repository
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRepository {
	private static Logger logger = LoggerFactory.getLogger(HandlerRepository.class);
	
	public static final String job_desc = "job_desc";
	public static final String job_url = "job_url";
	public static final String handleName = "handleName";
	public static final String triggerLogId = "triggerLogId";
	public static final String triggerLogUrl = "triggerLogUrl";

	// handler class map
	private static ConcurrentHashMap<String, IJobHandler> handlerClassMap = new ConcurrentHashMap<String, IJobHandler>();
	// handler thread map
	private static ConcurrentHashMap<String, HandlerThread> handlerTreadMap = new ConcurrentHashMap<String, HandlerThread>();
	// handler date queue map
	private static ConcurrentHashMap<String, LinkedBlockingQueue<Map<String, String>>> handlerDataQueueMap = new ConcurrentHashMap<String, LinkedBlockingQueue<Map<String, String>>>();
	
	// regist handler
	public static void regist(String handleName, IJobHandler handler){
		handlerClassMap.put(handleName, handler);
		LinkedBlockingQueue<Map<String, String>> handlerDateQueue = new LinkedBlockingQueue<Map<String, String>>();
		handlerDataQueueMap.put(handleName, handlerDateQueue);
		HandlerThread handlerThread = new HandlerThread(handleName);
		handlerThread.start();
		handlerTreadMap.put(handleName, handlerThread);
		logger.info(">>>>>>>>>>> xxl-job regist handler success, handleName:{}, handler:{}, handlerDateQueue:{}, handlerThread:{}", 
				new Object[]{handleName, handler, handlerDateQueue, handlerThread});
	}
	
	// create handler thread
	static class HandlerThread extends Thread{
		private String _handleName;
		public HandlerThread(String _handleName) {
			this._handleName = _handleName;
		}
		public boolean isValid = true;
		public void stopThread(){
			isValid = false;
		}
		@Override
		public void run() {
			while (isValid) {
				LinkedBlockingQueue<Map<String, String>> handlerDateQueue = handlerDataQueueMap.get(_handleName);
				Map<String, String> handlerData = handlerDateQueue.poll();
				if (handlerData!=null) {
					// handle job
					JobHandleStatus _status = JobHandleStatus.FAIL;
					String _msg = null;
					try {
						IJobHandler handler = handlerClassMap.get(_handleName);
						_status = handler.handle(handlerData);
					} catch (Exception e) {
						e.printStackTrace();
						_status = JobHandleStatus.FAIL;
						StringWriter out = new StringWriter();
						e.printStackTrace(new PrintWriter(out));
						_msg = out.toString();
					}

					// callback handler info
					String callback_response[] = null;
					try {
						String _triggerLogUrl = handlerData.get(HandlerRepository.triggerLogUrl);
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(HandlerRepository.triggerLogId, handlerData.get(HandlerRepository.triggerLogId));
						params.put(HttpUtil.status, _status.name());
						params.put(HttpUtil.msg, _msg);
						callback_response = HttpUtil.post(_triggerLogUrl, params);
					} catch (Exception e) {
						e.printStackTrace();
					}
					logger.info("<<<<<<<<<<< xxl-job thread handle, handlerData:{}, callback_status:{}, callback_msg:{}, callback_response:{}, thread:{}", 
							new Object[]{handlerData, _status, _msg, callback_response, this});
				} else {
					try {
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	// handler push to queue
	public static String pushHandleQueue(Map<String, String> _param) {
		
		// resuolt
		String _status = HttpUtil.FAIL;
		String _msg = "";
		// push data to queue
		String _handleName = _param.get(HandlerRepository.handleName);
		int _triggerLogId = Integer.valueOf(_param.get(HandlerRepository.triggerLogId));
		try {
			if (_handleName!=null && _handleName.trim().length()>0) {
				IJobHandler handler = handlerClassMap.get(_handleName);
				if (handler != null) {
					// push data to handler queue
					LinkedBlockingQueue<Map<String, String>> handlerDateQueue = handlerDataQueueMap.get(_handleName);
					if (handlerDateQueue == null) {
						handlerDateQueue = new LinkedBlockingQueue<Map<String, String>>();
						handlerDataQueueMap.put(_handleName, handlerDateQueue);
						logger.info(">>>>>>>>>>> xxl-job handler lazy fresh handlerDateQueue, _handleName:{}, handler:{}, handlerDateQueue:{}", 
								new Object[]{_handleName, handler, handlerDateQueue});
					}
					// check handler thread
					HandlerThread handlerThreadOld = handlerTreadMap.get(_handleName);
					if (!handlerThreadOld.isAlive()) {
						handlerThreadOld.stopThread();
						HandlerThread handlerThread = new HandlerThread(_handleName);
						handlerThread.start();
						handlerTreadMap.put(_handleName, handlerThread);
						logger.info(">>>>>>>>>>> xxl-job handler lazy fresh thread, _handleName:{}, handler:{}, handlerThread:{}", 
								new Object[]{_handleName, handler, handlerThread});
					}
					// push to queue
					handlerDateQueue.offer(_param);
					_status = HttpUtil.SUCCESS;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			_status = HttpUtil.FAIL;
			_msg = out.toString();
		}
		logger.info(">>>>>>>>>>> xxl-job pushHandleQueue, _handleName:{}, _triggerLogId:{}, _param:{}, _status:{}, _msg:{}", 
				new Object[]{_handleName, _triggerLogId, _param, _status, _msg});
		
		HashMap<String, String> triggerData = new HashMap<String, String>();
		triggerData.put(HttpUtil.status, _status);
		triggerData.put(HttpUtil.msg, _msg);
		return JacksonUtil.writeValueAsString(triggerData);
		
		/**
		 * trigger-log : 
		 * 		trigger side : store trigger-info >> trigger request >> update trigger-response-status
		 * 		job side : handler trigger >> update trigger-result
		 */
	}
	
}

package com.xxl.job.client.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.handler.IJobHandler.JobHandleStatus;
import com.xxl.job.client.handler.IJobHandler.JobTriggerStatus;


/**
 * handler repository
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRepository {
	private static Logger logger = LoggerFactory.getLogger(HandlerRepository.class);
	
	public static final String triggerUuid = "triggerUuid";
	public static final String handleName = "handleName";

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
				Map<String, String> handlerDate = handlerDateQueue.poll();
				if (handlerDate!=null) {
					JobHandleStatus jobHandleStatus = null;
					String jobHandleDetail = null;
					try {
						IJobHandler handler = handlerClassMap.get(_handleName);
						jobHandleStatus = handler.handle(handlerDate);
					} catch (Exception e) {
						e.printStackTrace();
						jobHandleStatus = JobHandleStatus.FAIL;
						StringWriter out = new StringWriter();
						e.printStackTrace(new PrintWriter(out));
						jobHandleDetail = out.toString();
					}
					String _triggerUuid = handlerDate.get(triggerUuid);
					logger.info("<<<<<<<<<<< xxl-job thread handle, _triggerUuid:{}, _handleName:{}, jobHandleStatus:{}, jobHandleDetail:{}, thread:{}", 
							new Object[]{_triggerUuid, _handleName, jobHandleStatus, jobHandleDetail, this});
				} else {
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	// handler push to queue
	public static String pushHandleQueue(String triggerUuid, String handleName, Map<String, String> _param) {
		JobTriggerStatus _triggerStatus = JobTriggerStatus.FAIL;
		String _triggerDetailLog = null;
		
		try {
			if (handleName!=null && handleName.trim().length()>0) {
				IJobHandler handler = handlerClassMap.get(handleName);
				if (handler != null) {
					// push data to handler queue
					LinkedBlockingQueue<Map<String, String>> handlerDateQueue = handlerDataQueueMap.get(handleName);
					if (handlerDateQueue == null) {
						handlerDateQueue = new LinkedBlockingQueue<Map<String, String>>();
						handlerDataQueueMap.put(handleName, handlerDateQueue);
						logger.info(">>>>>>>>>>> xxl-job handler lazy fresh handlerDateQueue, handleName:{}, handler:{}, handlerDateQueue:{}", 
								new Object[]{handleName, handler, handlerDateQueue});
					}
					handlerDateQueue.offer(_param);
					// check handler thread
					HandlerThread handlerThreadOld = handlerTreadMap.get(handleName);
					if (!handlerThreadOld.isAlive()) {
						handlerThreadOld.stopThread();
						HandlerThread handlerThread = new HandlerThread(handleName);
						handlerThread.start();
						handlerTreadMap.put(handleName, handlerThread);
						logger.info(">>>>>>>>>>> xxl-job handler lazy fresh thread, handleName:{}, handler:{}, handlerThread:{}", 
								new Object[]{handleName, handler, handlerThread});
					}
					_triggerStatus = JobTriggerStatus.SUCCESS;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			_triggerStatus = JobTriggerStatus.FAIL;
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			_triggerDetailLog = out.toString();
		}
		logger.info(">>>>>>>>>>> xxl-job pushHandleQueue, triggerUuid:{}, handleName, _triggerStatus:{}, _triggerDetailLog", 
				new Object[]{triggerUuid, handleName, _triggerStatus, _triggerDetailLog});
		
		String responseBody = _triggerStatus.name();
		if (JobTriggerStatus.SUCCESS != _triggerStatus) {
			responseBody += "#" + _triggerDetailLog;
		}
		return responseBody;
		/**
		 * trigger-log : 
		 * 		trigger side : store trigger-info >> trigger request >> update trigger-response-status
		 * 		job side : handler trigger >> update trigger-result
		 */
	}
	
}

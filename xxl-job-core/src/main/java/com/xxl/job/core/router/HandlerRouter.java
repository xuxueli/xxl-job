package com.xxl.job.core.router;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.router.action.BeatAction;
import com.xxl.job.core.router.action.KillAction;
import com.xxl.job.core.router.action.LogAction;
import com.xxl.job.core.router.action.RunAction;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.router.thread.JobThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * handler repository
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRouter {
	private static Logger logger = LoggerFactory.getLogger(HandlerRouter.class);

	/**
	 * job handler repository
     */
	private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
	public static IJobHandler registJobHandler(String name, IJobHandler jobHandler){
		logger.info("xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
		return HandlerRouter.jobHandlerRepository.put(name, jobHandler);
	}
	public static IJobHandler loadJobHandler(String name){
		return HandlerRouter.jobHandlerRepository.get(name);
	}

	/**
	 * job thread repository
     */
	private static ConcurrentHashMap<String, JobThread> JobThreadRepository = new ConcurrentHashMap<String, JobThread>();
	public static JobThread registJobThread(String jobkey, IJobHandler handler){
		JobThread jobThread = new JobThread(handler);
		jobThread.start();
		logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobkey:{}, handler:{}", new Object[]{jobkey, handler});
		HandlerRouter.JobThreadRepository.put(jobkey, jobThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
		return jobThread;
	}
	public static JobThread loadJobThread(String jobKey){
		return HandlerRouter.JobThreadRepository.get(jobKey);
	}

	/**
	 * route action repository
	 */
	public enum ActionRepository {
		RUN(new RunAction()),
		KILL(new KillAction()),
		LOG(new LogAction()),
		BEAT(new BeatAction());

		private IAction action;
		private ActionRepository(IAction action){
			this.action = action;
		}

		/**
		 * match Action by enum name
		 * @param name
         * @return action
         */
		public static IAction matchAction(String name){
			if (name!=null && name.trim().length()>0) {
				for (ActionRepository item : ActionRepository.values()) {
					if (item.name().equals(name)) {
						return item.action;
					}
				}
			}
			return null;
		}

	}

	// handler push to queue
	public static ResponseModel route(RequestModel requestModel) {
		logger.debug(">>>>>>>>>>> xxl-job route, RequestModel:{}", new Object[]{requestModel.toString()});

		// timestamp check
		if (System.currentTimeMillis() - requestModel.getTimestamp() > 60000) {
			return new ResponseModel(ResponseModel.FAIL, "Timestamp Timeout.");
		}

		// match action
		IAction action = ActionRepository.matchAction(requestModel.getAction());
		if (action == null) {
			return new ResponseModel(ResponseModel.FAIL, "Action match fail.");
		}

		return action.execute(requestModel);
	}

}

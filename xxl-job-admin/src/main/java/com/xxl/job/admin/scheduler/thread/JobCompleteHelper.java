package com.xxl.job.admin.scheduler.thread;

import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.openapi.model.HandleCallbackRequest;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * job complate, for callback and result-lost
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobCompleteHelper {
	private static Logger logger = LoggerFactory.getLogger(JobCompleteHelper.class);
	
	// ---------------------- monitor ----------------------

	private ThreadPoolExecutor callbackThreadPool = null;
	private Thread monitorThread;
	private volatile boolean toStop = false;

	/**
	 * start
	 */
	public void start(){

		// for callback
		callbackThreadPool = new ThreadPoolExecutor(
				2,
				20,
				30L,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(3000),
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "xxl-job, admin JobLosedMonitorHelper-callbackThreadPool-" + r.hashCode());
					}
				},
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						r.run();
						logger.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
					}
				});


		// for monitor
		monitorThread = new Thread(new Runnable() {

			@Override
			public void run() {

				// wait for JobTriggerPoolHelper-init
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (Throwable e) {
					if (!toStop) {
						logger.error(e.getMessage(), e);
					}
				}

				// monitor
				while (!toStop) {
					try {
						// 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
						Date losedTime = DateTool.addMinutes(new Date(), -10);
						List<Long> losedJobIds  = XxlJobAdminBootstrap.getInstance().getXxlJobLogMapper().findLostJobIds(losedTime);

						if (losedJobIds!=null && losedJobIds.size()>0) {
							for (Long logId: losedJobIds) {

								XxlJobLog jobLog = new XxlJobLog();
								jobLog.setId(logId);

								jobLog.setHandleTime(new Date());
								jobLog.setHandleCode(XxlJobContext.HANDLE_CODE_FAIL);
								jobLog.setHandleMsg( I18nUtil.getString("joblog_lost_fail") );

								XxlJobAdminBootstrap.getInstance().getJobCompleter().complete(jobLog);
							}

						}
					} catch (Throwable e) {
						if (!toStop) {
							logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
						}
					}

                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

				logger.info(">>>>>>>>>>> xxl-job, JobLosedMonitorHelper stop");

			}
		});
		monitorThread.setDaemon(true);
		monitorThread.setName("xxl-job, admin JobLosedMonitorHelper");
		monitorThread.start();
	}

	/**
	 * stop
	 */
	public void stop(){
		toStop = true;

		// stop registryOrRemoveThreadPool
		callbackThreadPool.shutdownNow();

		// stop monitorThread (interrupt and wait)
		monitorThread.interrupt();
		try {
			monitorThread.join();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}


	// ---------------------- helper ----------------------

	public Response<String> callback(List<HandleCallbackRequest> callbackParamList) {

		callbackThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				for (HandleCallbackRequest handleCallbackParam: callbackParamList) {
					Response<String> callbackResult = doCallback(handleCallbackParam);
					logger.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
							(callbackResult.isSuccess()?"success":"fail"), handleCallbackParam, callbackResult);
				}
			}
		});

		return Response.ofSuccess();
	}

	private Response<String> doCallback(HandleCallbackRequest handleCallbackParam) {
		// valid log item
		XxlJobLog log = XxlJobAdminBootstrap.getInstance().getXxlJobLogMapper().load(handleCallbackParam.getLogId());
		if (log == null) {
			return Response.ofFail( "log item not found.");
		}
		if (log.getHandleCode() > 0) {
			return Response.ofFail("log repeate callback.");     // avoid repeat callback, trigger child job etc
		}

		// handle msg
		StringBuffer handleMsg = new StringBuffer();
		if (log.getHandleMsg()!=null) {
			handleMsg.append(log.getHandleMsg()).append("<br>");
		}
		if (handleCallbackParam.getHandleMsg() != null) {
			handleMsg.append(handleCallbackParam.getHandleMsg());
		}

		// success, save log
		log.setHandleTime(new Date());
		log.setHandleCode(handleCallbackParam.getHandleCode());
		log.setHandleMsg(handleMsg.toString());
		XxlJobAdminBootstrap.getInstance().getJobCompleter().complete(log);

		return Response.ofSuccess();
	}



}

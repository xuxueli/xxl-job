package com.xxl.job.admin.core.thread;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;

/**
 * job monitor helper
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobMonitorHelper {
	private static Logger logger = LoggerFactory.getLogger(JobMonitorHelper.class);
	
	private static JobMonitorHelper helper = new JobMonitorHelper();
	private ExecutorService executor = Executors.newCachedThreadPool();
	private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(0xfff8);
	private ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<String, Integer>();
	
	public JobMonitorHelper(){
		// consumer
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					logger.info(">>>>>>>>>>> job monitor run ... ");
					Integer jobLogId = JobMonitorHelper.helper.queue.poll();
					if (jobLogId != null && jobLogId > 0) {
						XxlJobLog log = DynamicSchedulerUtil.xxlJobLogDao.load(jobLogId);
						if (log!=null) {
							if (RemoteCallBack.SUCCESS.equals(log.getTriggerStatus()) && StringUtils.isBlank(log.getHandleStatus())) {
								try {
									TimeUnit.SECONDS.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								JobMonitorHelper.monitor(jobLogId);
							}
							if (RemoteCallBack.SUCCESS.equals(log.getTriggerStatus()) && RemoteCallBack.SUCCESS.equals(log.getHandleStatus())) {
								// pass
							}
							if (RemoteCallBack.FAIL.equals(log.getTriggerStatus()) || RemoteCallBack.FAIL.equals(log.getHandleStatus())) {
								String monotorKey = log.getJobGroup().concat("_").concat(log.getJobName());
								Integer count = countMap.get(monotorKey);
								if (count == null) {
									count = new Integer(0);
								}
								count += 1;
								countMap.put(monotorKey, count);
								XxlJobInfo info = DynamicSchedulerUtil.xxlJobInfoDao.load(log.getJobGroup(), log.getJobName());
								if (count >= info.getAlarmThreshold()) {
									MailUtil.sendMail(info.getAlarmEmail(), "《调度平台中心-监控报警》", 
											MessageFormat.format("调度任务[{0}]失败报警，连续失败次数：{1}", monotorKey, count), false, null);
									countMap.remove(monotorKey);
								}
							}
						}
					} else {
						try {
							TimeUnit.SECONDS.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	// producer
	public static void monitor(int jobLogId){
		JobMonitorHelper.helper.queue.offer(jobLogId);
	}
	
}

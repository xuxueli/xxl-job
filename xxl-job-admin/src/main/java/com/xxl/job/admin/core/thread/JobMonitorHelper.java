package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.core.router.model.ResponseModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

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
					try {
						logger.debug(">>>>>>>>>>> job monitor beat ... ");
						Integer jobLogId = JobMonitorHelper.helper.queue.take();
						if (jobLogId != null && jobLogId > 0) {
							logger.info(">>>>>>>>>>> job monitor heat success, JobLogId:{}", jobLogId);
							XxlJobLog log = DynamicSchedulerUtil.xxlJobLogDao.load(jobLogId);
							if (log!=null) {
								if (ResponseModel.SUCCESS.equals(log.getTriggerStatus()) && StringUtils.isBlank(log.getHandleStatus())) {
									try {
										TimeUnit.SECONDS.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									JobMonitorHelper.monitor(jobLogId);
								}
								if (ResponseModel.SUCCESS.equals(log.getTriggerStatus()) && ResponseModel.SUCCESS.equals(log.getHandleStatus())) {
									// pass
								}
								if (ResponseModel.FAIL.equals(log.getTriggerStatus()) || ResponseModel.FAIL.equals(log.getHandleStatus())) {
									XxlJobInfo info = DynamicSchedulerUtil.xxlJobInfoDao.load(log.getJobGroup(), log.getJobName());
									if (info!=null && info.getAlarmEmail()!=null && info.getAlarmEmail().trim().length()>0) {

										Set<String> emailSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
										for (String email: emailSet) {
											String title = "《调度监控报警-任务调度中心XXL-JOB》";
											XxlJobGroup group = DynamicSchedulerUtil.xxlJobGroupDao.load(Integer.valueOf(info.getJobGroup()));
											String content = MessageFormat.format("任务调度失败, 执行器名称:{0}, 任务描述:{1}.", group!=null?group.getTitle():"null", info.getJobDesc());
											MailUtil.sendMail(email, title, content, false, null);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						logger.error("job monitor error:{}", e);
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

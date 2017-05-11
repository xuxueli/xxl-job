package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * job monitor instance
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {
	private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);
	
	private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
	public static JobFailMonitorHelper getInstance(){
		return instance;
	}

	private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(0xfff8);

	private Thread monitorThread;
	private boolean toStop = false;
	public void start(){
		monitorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!toStop) {
					try {
						logger.debug(">>>>>>>>>>> job monitor beat ... ");
						Integer jobLogId = JobFailMonitorHelper.instance.queue.take();
						if (jobLogId != null && jobLogId > 0) {
							logger.debug(">>>>>>>>>>> job monitor heat success, JobLogId:{}", jobLogId);
							XxlJobLog log = XxlJobDynamicScheduler.xxlJobLogDao.load(jobLogId);
							if (log!=null) {
								if (ReturnT.SUCCESS_CODE==log.getTriggerCode() && log.getHandleCode()==0) {
									// running
									try {
										TimeUnit.SECONDS.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									JobFailMonitorHelper.monitor(jobLogId);
								}
								if (ReturnT.SUCCESS_CODE==log.getTriggerCode() && ReturnT.SUCCESS_CODE==log.getHandleCode()) {
									// pass
								}
								if (ReturnT.FAIL_CODE == log.getTriggerCode()|| ReturnT.FAIL_CODE==log.getHandleCode()) {
									XxlJobInfo info = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(log.getJobId());
									if (info!=null && info.getAlarmEmail()!=null && info.getAlarmEmail().trim().length()>0) {

										Set<String> emailSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
										for (String email: emailSet) {
											String title = "《调度监控报警》(任务调度中心XXL-JOB)";
											XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(Integer.valueOf(info.getJobGroup()));
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
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public void toStop(){
		toStop = true;
		//monitorThread.interrupt();
	}
	
	// producer
	public static void monitor(int jobLogId){
		getInstance().queue.offer(jobLogId);
	}
	
}

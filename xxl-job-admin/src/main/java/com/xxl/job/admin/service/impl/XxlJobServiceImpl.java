package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;
import com.xxl.job.admin.service.IXxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * core job action for xxl-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements IXxlJobService {
	private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

	@Resource
	private IXxlJobGroupDao xxlJobGroupDao;
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	@Resource
	public IXxlJobLogDao xxlJobLogDao;
	@Resource
	private IXxlJobLogGlueDao xxlJobLogGlueDao;
	
	@Override
	public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime) {

		// page list
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, executorHandler);
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, executorHandler);
		
		// fill job info
		if (list!=null && list.size()>0) {
			for (XxlJobInfo jobInfo : list) {
				XxlJobDynamicScheduler.fillJobInfo(jobInfo);
			}
		}
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
	    maps.put("data", list);  					// 分页列表
		return maps;
	}

	@Override
	public ReturnT<String> add(int jobGroup, String jobCron, String jobDesc, String author, String alarmEmail,
							   String executorHandler, String executorParam,
							   int glueSwitch, String glueSource, String glueRemark, String childJobKey) {
		// valid
		XxlJobGroup group = xxlJobGroupDao.load(jobGroup);
		if (group == null) {
			return new ReturnT<String>(500, "请选择“执行器”");
		}
		if (!CronExpression.isValidExpression(jobCron)) {
			return new ReturnT<String>(500, "请输入格式正确的“Cron”");
		}
		if (StringUtils.isBlank(jobDesc)) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (glueSwitch==0 && StringUtils.isBlank(executorHandler)) {
			return new ReturnT<String>(500, "请输入“JobHandler”");
		}

		// childJobKey valid
		if (StringUtils.isNotBlank(childJobKey)) {
			String[] childJobKeys = childJobKey.split(",");
			for (String childJobKeyItem: childJobKeys) {
				String[] childJobKeyArr = childJobKeyItem.split("_");
				if (childJobKeyArr.length!=2) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
				}
				XxlJobInfo childJobInfo = xxlJobInfoDao.load(Integer.valueOf(childJobKeyArr[0]), childJobKeyArr[1]);
				if (childJobInfo==null) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
				}
			}
		}

		// generate jobName
		String jobName = FastDateFormat.getInstance("yyyyMMddHHmmssSSSS").format(new Date());
		try {
			if (XxlJobDynamicScheduler.checkExists(jobName, String.valueOf(jobGroup))) {
				return new ReturnT<String>(500, "系统繁忙，请稍后重试");
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
			return new ReturnT<String>(500, "系统繁忙，请稍后重试");
		}

		// Backup to the database
		XxlJobInfo jobInfo = new XxlJobInfo();
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setJobName(jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setExecutorHandler(executorHandler);
		jobInfo.setExecutorParam(executorParam);
		jobInfo.setGlueSwitch(glueSwitch);
		jobInfo.setGlueSource(glueSource);
		jobInfo.setGlueRemark(glueRemark);
		jobInfo.setChildJobKey(childJobKey);

		try {
			// add job 2 quartz
			boolean result = XxlJobDynamicScheduler.addJob(String.valueOf(jobGroup), jobName, jobCron);
			if (result) {
				xxlJobInfoDao.save(jobInfo);
				return ReturnT.SUCCESS;
			} else {
				return new ReturnT<String>(500, "新增任务失败");
			}
		} catch (SchedulerException e) {
			logger.error("", e);
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> reschedule(int jobGroup, String jobName, String jobCron, String jobDesc, String author, String alarmEmail,
			String executorHandler, String executorParam, int glueSwitch, String childJobKey) {

		// valid
		XxlJobGroup group = xxlJobGroupDao.load(jobGroup);
		if (group == null) {
			return new ReturnT<String>(500, "请选择“执行器”");
		}
		if (StringUtils.isBlank(jobName)) {
			return new ReturnT<String>(500, "请输入“任务名”");
		}
		if (!CronExpression.isValidExpression(jobCron)) {
			return new ReturnT<String>(500, "请输入格式正确的“Cron”");
		}
		if (StringUtils.isBlank(jobDesc)) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (glueSwitch==0 && StringUtils.isBlank(executorHandler)) {
			return new ReturnT<String>(500, "请输入“JobHandler”");
		}

		// childJobKey valid
		if (StringUtils.isNotBlank(childJobKey)) {
			String[] childJobKeys = childJobKey.split(",");
			for (String childJobKeyItem: childJobKeys) {
				String[] childJobKeyArr = childJobKeyItem.split("_");
				if (childJobKeyArr.length!=2) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
				}
				XxlJobInfo childJobInfo = xxlJobInfoDao.load(Integer.valueOf(childJobKeyArr[0]), childJobKeyArr[1]);
				if (childJobInfo==null) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
				}
			}
		}

		// stage job info
		XxlJobInfo jobInfo = xxlJobInfoDao.load(jobGroup, jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setExecutorHandler(executorHandler);
		jobInfo.setExecutorParam(executorParam);
		jobInfo.setGlueSwitch(glueSwitch);
		jobInfo.setChildJobKey(childJobKey);
		
		try {
			// fresh quartz
			boolean ret = XxlJobDynamicScheduler.rescheduleJob(String.valueOf(jobGroup), jobName, jobCron);
			if (ret) {
				xxlJobInfoDao.update(jobInfo);
				return ReturnT.SUCCESS;
			} else {
				return new ReturnT<String>(500, "更新任务失败");
			}
		} catch (SchedulerException e) {
			logger.error("", e);
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> remove(int jobGroup, String jobName) {
		try {
			XxlJobDynamicScheduler.removeJob(jobName, String.valueOf(jobGroup));
			xxlJobInfoDao.delete(jobGroup, jobName);
			xxlJobLogDao.delete(jobGroup, jobName);
			xxlJobLogGlueDao.delete(jobGroup, jobName);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> pause(int jobGroup, String jobName) {
		try {
			XxlJobDynamicScheduler.pauseJob(jobName, String.valueOf(jobGroup));	// jobStatus do not store
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> resume(int jobGroup, String jobName) {
		try {
			XxlJobDynamicScheduler.resumeJob(jobName, String.valueOf(jobGroup));
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> triggerJob(int jobGroup, String jobName) {
		try {
			XxlJobDynamicScheduler.triggerJob(jobName, String.valueOf(jobGroup));
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
}

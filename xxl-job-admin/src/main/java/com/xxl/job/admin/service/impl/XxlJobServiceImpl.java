package com.xxl.job.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import com.xxl.job.admin.core.constant.Constants.JobGroupEnum;
import com.xxl.job.admin.core.jobbean.RemoteHttpJobBean;
import com.xxl.job.admin.core.model.ReturnT;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;
import com.xxl.job.admin.service.IXxlJobService;

/**
 * core job service for xxl-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements IXxlJobService {

	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	@Resource
	public IXxlJobLogDao xxlJobLogDao;
	@Resource
	private IXxlJobLogGlueDao xxlJobLogGlueDao;
	
	@Override
	public Map<String, Object> pageList(int start, int length, String jobGroup, String jobName, String filterTime) {
		
		// page list
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, jobName);
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, jobName);
		
		// fill job info
		if (list!=null && list.size()>0) {
			for (XxlJobInfo jobInfo : list) {
				DynamicSchedulerUtil.fillJobInfo(jobInfo);
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
	public ReturnT<String> add(String jobGroup, String jobName, String jobCron, String jobDesc, 
			String executorAddress,	String executorHandler, String executorParam, 
			String author, String alarmEmail, int alarmThreshold,
			int glueSwitch, String glueSource, String glueRemark) {
		// valid
		if (JobGroupEnum.match(jobGroup) == null) {
			return new ReturnT<String>(500, "请选择“任务组”");
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
		if (StringUtils.isBlank(executorAddress)) {
			return new ReturnT<String>(500, "请输入“执行器地址”");
		}
		if (glueSwitch==0 && StringUtils.isBlank(executorHandler)) {
			return new ReturnT<String>(500, "请输入“jobHandler”");
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (alarmThreshold < 0) {
			alarmThreshold = 0;
		}
		
		try {
			if (DynamicSchedulerUtil.checkExists(jobName, jobGroup)) {
				return new ReturnT<String>(500, "此任务已存在，请更换任务组或任务名");
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
			return new ReturnT<String>(500, "此任务已存在，请更换任务组或任务名");
		}
		
		// Backup to the database
		XxlJobInfo jobInfo = new XxlJobInfo();
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setJobName(jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setJobClass(RemoteHttpJobBean.class.getName());
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setAlarmThreshold(alarmThreshold);
		jobInfo.setGlueSwitch(glueSwitch);
		jobInfo.setGlueSource(glueSource);
		jobInfo.setGlueRemark(glueRemark);
		jobInfo.setExecutorAddress(executorAddress);
		jobInfo.setExecutorHandler(executorHandler);
		jobInfo.setExecutorParam(executorParam);
		xxlJobInfoDao.save(jobInfo);
		
		try {
			// add job 2 quartz
			boolean result = DynamicSchedulerUtil.addJob(jobInfo);
			if (result) {
				return ReturnT.SUCCESS;
			} else {
				xxlJobInfoDao.delete(jobGroup, jobName);
				return new ReturnT<String>(500, "新增任务失败");
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> reschedule(String jobGroup, String jobName, String jobCron, String jobDesc,
			String executorAddress,	String executorHandler, String executorParam, 
			String author, String alarmEmail, int alarmThreshold, int glueSwitch) {
		
		// valid
		if (JobGroupEnum.match(jobGroup) == null) {
			return new ReturnT<String>(500, "请选择“任务组”");
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
		if (StringUtils.isBlank(executorAddress)) {
			return new ReturnT<String>(500, "请输入“执行器地址”");
		}
		if (glueSwitch==0 && StringUtils.isBlank(executorHandler)) {
			return new ReturnT<String>(500, "请输入“jobHandler”");
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (alarmThreshold < 0) {
			alarmThreshold = 0;
		}
		
		XxlJobInfo jobInfo = xxlJobInfoDao.load(jobGroup, jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setAlarmThreshold(alarmThreshold);
		jobInfo.setGlueSwitch(glueSwitch);
		jobInfo.setExecutorAddress(executorAddress);
		jobInfo.setExecutorHandler(executorHandler);
		jobInfo.setExecutorParam(executorParam);
		
		try {
			// fresh quartz
			DynamicSchedulerUtil.rescheduleJob(jobInfo);
			
			// fresh db
			xxlJobInfoDao.update(jobInfo);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> remove(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.removeJob(jobName, jobGroup);
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
	public ReturnT<String> pause(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.pauseJob(jobName, jobGroup);	// jobStatus do not store
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> resume(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.resumeJob(jobName, jobGroup);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> triggerJob(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.triggerJob(jobName, jobGroup);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
}

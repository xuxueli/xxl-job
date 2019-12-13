package com.xxl.job.admin.service.impl;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.admin.dao.XxlJobLogReportDao;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;

/**
 * core job action for xxl-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements XxlJobService {
	private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

	@Resource
	private JobGroupService jobGroupService;
	@Resource
	private JobInfoService jobInfoService;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;
	@Resource
	private XxlJobLogReportDao xxlJobLogReportDao;

	@Override
	public Page<XxlJobInfo> select(Page<XxlJobInfo> pg, XxlJobInfo j) {
		return jobInfoService.select(pg, j);
	}

	@Override
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		// valid
		XxlJobGroup group = jobGroupService.query(new XxlJobGroup().setId(jobInfo.getJobGroup()));
		if (group == null)
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_field_jobgroup")));
		if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0)
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
		if (jobInfo.getAuthor() == null || jobInfo.getAuthor().trim().length() == 0)
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author")));
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null)
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null)
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid")));
		if (GlueTypeEnum.match(jobInfo.getGlueType()) == null)
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_gluetype") + I18nUtil.getString("system_unvalid")));
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && (jobInfo.getExecutorHandler() == null || jobInfo.getExecutorHandler().trim().length() == 0))
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + "JobHandler"));
		Date now = new Date();
		/*if (!CronExpression.isValidExpression(jobInfo.getJobCron()))
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));*/
		try {
			CronExpression ce = new CronExpression(jobInfo.getJobCron());
			if (jobInfo.getTriggerStatus() == 1) {
				long ms = System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS;
				Date next = ce.getNextValidTimeAfter(new Date(ms));
				if (next != null) {
					jobInfo.setTriggerNextTime(next.getTime());
				} else {
					if (jobInfo.isLeastOnce())
						jobInfo.setTriggerNextTime(ms);	//无下次执行时间，则手动设置，至少执行一次
					else
						jobInfo.setTriggerStatus(0);
				}
			}
		} catch (ParseException e) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
		}
		// fix "\r" in shell
		if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null)
			jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));

		// ChildJobId valid
		String cIdStr = jobInfo.getChildJobId();
		if (cIdStr != null && (cIdStr = cIdStr.trim()).length() > 1) {
			String[] cIds = cIdStr.split(",");
			StringBuilder sbd = new StringBuilder(cIdStr.length());
			for (int i = 0; i < cIds.length; i++) {
				String cId = cIds[i];
				if (cId != null && (cId = cId.trim()).length() > 0 && isNumeric(cId)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.parseInt(cId));
					if (childJobInfo != null)
						sbd.append(i == 0 ? "" : ",").append(cId);
					else
						return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found")), cId));
//				} else {
//					return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid")), cId));
				}
			}
			// join , avoid "xxx,,"
			String temp = sbd.toString();	//"";
			/*for (String item : cIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length() - 1);*/
			jobInfo.setChildJobId(temp);
		}
		// add in db
		jobInfo.setAddTime(now).setUpdateTime(now).setGlueUpdatetime(now);
		xxlJobInfoDao.save(jobInfo);
		if (jobInfo.getId() <= 0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail")));
		}
		return new ReturnT<String>(String.valueOf(jobInfo.getId()));
	}

	private boolean isNumeric(String str) {
		try {
			Integer.valueOf(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public ReturnT<Integer> update(XxlJobInfo jobInfo) {
		// valid
		if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0)
			return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
		if (jobInfo.getAuthor() == null || jobInfo.getAuthor().trim().length() == 0)
			return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author")));
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null)
			return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null)
			return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid")));
		Date now = new Date();
		/*if (!CronExpression.isValidExpression(jobInfo.getJobCron()))
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));*/
		CronExpression ce;
		try {
			ce = new CronExpression(jobInfo.getJobCron());
		} catch (Exception e) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
		}
		// ChildJobId valid
		String cIdStr = jobInfo.getChildJobId();
		if (cIdStr != null && (cIdStr = cIdStr.trim()).length() > 1) {
			String[] cIds = cIdStr.split(",");
			StringBuilder sbd = new StringBuilder(cIdStr.length());
			for (int i = 0; i < cIds.length; i++) {
				String cId = cIds[i];
				if (cId != null && (cId = cId.trim()).length() > 0 && isNumeric(cId)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.parseInt(cId));
					if (childJobInfo != null)
						sbd.append(i == 0 ? "" : ",").append(cId);
					else
						return new ReturnT<>(ReturnT.FAIL_CODE, MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found")), cId));
//				} else {
//					return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid")), cId));
				}
			}
			// join , avoid "xxx,,"
			String temp = sbd.toString();	//"";
			/*for (String item : cIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length() - 1);*/
			jobInfo.setChildJobId(temp);
		}
		// group valid
		XxlJobGroup jobGroup = jobGroupService.query(new XxlJobGroup().setId(jobInfo.getJobGroup()));
		if (jobGroup == null)
			return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_jobgroup") + I18nUtil.getString("system_unvalid")));

		// stage job info
		XxlJobInfo jobi = xxlJobInfoDao.loadById(jobInfo.getId());
		if (jobi == null)
			return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_not_found")));

		// next trigger time (5s后生效，避开预读周期)
		if (jobi.getTriggerStatus() == 1 && !jobInfo.getJobCron().equals(jobi.getJobCron())) {
			long ms = System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS;
			Date next = ce.getNextValidTimeAfter(new Date(ms));
			if (next != null) {
				jobi.setTriggerNextTime(next.getTime());
			} else {
				if (jobInfo.isLeastOnce())
					jobi.setTriggerNextTime(ms);	//无下次执行时间，则手动设置，至少执行一次
				else
					jobi.setTriggerStatus(0);
					//return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
			}
			/*Date nt = ce.getNextValidTimeAfter(new Date(ms));
			if (nt == null)
				return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
			jobi.setTriggerNextTime(nt.getTime());*/
		}
		jobi.setJobGroup(jobInfo.getJobGroup());
		jobi.setJobCron(jobInfo.getJobCron());
		jobi.setJobDesc(jobInfo.getJobDesc());
		jobi.setAuthor(jobInfo.getAuthor());
		jobi.setAlarmEmail(jobInfo.getAlarmEmail());
		jobi.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		jobi.setExecutorHandler(jobInfo.getExecutorHandler());
		jobi.setExecutorParam(jobInfo.getExecutorParam());
		jobi.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		jobi.setExecutorTimeout(jobInfo.getExecutorTimeout());
		jobi.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
		jobi.setChildJobId(jobInfo.getChildJobId());
		jobi.setUpdateTime(now);
		int num = xxlJobInfoDao.update(jobi);
		if (num <= 0)
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_update") + I18nUtil.getString("system_fail"));
		return new ReturnT<Integer>(num);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public ReturnT<Integer> remove(int id) {
		int num = xxlJobInfoDao.delete(id);
		if (num <= 0)
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_fail"));
		xxlJobLogDao.delete(id);
		xxlJobLogGlueDao.deleteByJobId(id);
		return new ReturnT<>(num);
	}

	@Override
	public ReturnT<String> start(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);

		// next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = 0;
		try {
			Date nextValidTime = new CronExpression(xxlJobInfo.getJobCron()).getNextValidTimeAfter(new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
			if (nextValidTime == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
			}
			nextTriggerTime = nextValidTime.getTime();
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") + " | " + e.getMessage());
		}

		xxlJobInfo.setTriggerStatus(1);
		xxlJobInfo.setTriggerLastTime(0);
		xxlJobInfo.setTriggerNextTime(nextTriggerTime);

		xxlJobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(xxlJobInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> stop(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);

		xxlJobInfo.setTriggerStatus(0);
		xxlJobInfo.setTriggerLastTime(0);
		xxlJobInfo.setTriggerNextTime(0);

		xxlJobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(xxlJobInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public Map<String, Object> dashboardInfo() {

		int jobInfoCount = xxlJobInfoDao.findAllCount();
		int jobLogCount = 0;
		int jobLogSuccessCount = 0;
		XxlJobLogReport xxlJobLogReport = xxlJobLogReportDao.queryLogReportTotal();
		if (xxlJobLogReport != null) {
			jobLogCount = xxlJobLogReport.getRunningCount() + xxlJobLogReport.getSucCount() + xxlJobLogReport.getFailCount();
			jobLogSuccessCount = xxlJobLogReport.getSucCount();
		}

		// executor count
		Set<String> executorAddressSet = new HashSet<String>();
		List<XxlJobGroup> groupList = jobGroupService.select(null, new XxlJobGroup());
		if (groupList != null && !groupList.isEmpty()) {
			for (XxlJobGroup group : groupList) {
				if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
					executorAddressSet.addAll(group.getRegistryList());
				}
			}
		}

		int executorCount = executorAddressSet.size();

		Map<String, Object> dashboardMap = new HashMap<String, Object>();
		dashboardMap.put("jobInfoCount", jobInfoCount);
		dashboardMap.put("jobLogCount", jobLogCount);
		dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
		dashboardMap.put("executorCount", executorCount);
		return dashboardMap;
	}

	@Override
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {

		// process
		List<String> triggerDayList = new ArrayList<String>();
		List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		int triggerCountRunningTotal = 0;
		int triggerCountSucTotal = 0;
		int triggerCountFailTotal = 0;

		List<XxlJobLogReport> logReportList = xxlJobLogReportDao.queryLogReport(startDate, endDate);

		if (logReportList != null && logReportList.size() > 0) {
			for (XxlJobLogReport item : logReportList) {
				String day = DateUtil.formatDate(item.getTriggerDay());
				int triggerDayCountRunning = item.getRunningCount();
				int triggerDayCountSuc = item.getSucCount();
				int triggerDayCountFail = item.getFailCount();

				triggerDayList.add(day);
				triggerDayCountRunningList.add(triggerDayCountRunning);
				triggerDayCountSucList.add(triggerDayCountSuc);
				triggerDayCountFailList.add(triggerDayCountFail);

				triggerCountRunningTotal += triggerDayCountRunning;
				triggerCountSucTotal += triggerDayCountSuc;
				triggerCountFailTotal += triggerDayCountFail;
			}
		} else {
			for (int i = -6; i <= 0; i++) {
				triggerDayList.add(DateUtil.formatDate(DateUtil.addDays(new Date(), i)));
				triggerDayCountRunningList.add(0);
				triggerDayCountSucList.add(0);
				triggerDayCountFailList.add(0);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("triggerDayList", triggerDayList);
		result.put("triggerDayCountRunningList", triggerDayCountRunningList);
		result.put("triggerDayCountSucList", triggerDayCountSucList);
		result.put("triggerDayCountFailList", triggerDayCountFailList);

		result.put("triggerCountRunningTotal", triggerCountRunningTotal);
		result.put("triggerCountSucTotal", triggerCountSucTotal);
		result.put("triggerCountFailTotal", triggerCountFailTotal);

		return new ReturnT<Map<String, Object>>(result);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Integer add4appName(XxlJobInfo jobInfo) {
		Integer gid = this.getGroup(jobInfo);
		ReturnT<String> ra = this.add(jobInfo.setJobGroup(gid));
		if (ra.getCode() != ReturnT.SUCCESS_CODE)
			throw new XxlJobException(ra.getMsg());
		return Integer.valueOf(ra.getContent());
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Integer update4appName(XxlJobInfo jobInfo) {
		Integer gid = this.getGroup(jobInfo);
		jobInfo = jobInfoService.query(jobInfo.setJobGroup(gid));
		if (jobInfo == null)
			return -1;

		ReturnT<Integer> ra = this.update(jobInfo);
		if (ra.getCode() != ReturnT.SUCCESS_CODE)
			throw new XxlJobException(ra.getMsg());
		return ra.getContent();
	}

	@Override
	public Integer rm4appName(XxlJobInfo jobInfo) {
		Integer gid = this.getGroup(jobInfo);
		Page<XxlJobInfo> rs = jobInfoService.select(null, jobInfo.setJobGroup(gid));
		if (rs == null || rs.isEmpty())
			return 0;

		int num = 0;
		for (int i = 0; i < rs.size(); i++) {
			XxlJobInfo r = rs.get(i);
			ReturnT<Integer> ra = this.remove(r.getId());
			if (ra.getCode() == ReturnT.SUCCESS_CODE)
				num += ra.getContent();
		}
		return num;
	}

	/**
	 * @param jobInfo {@link XxlJobInfo}
	 * @return  {@link Integer}
	 * @author Haining.Liu
	 * @date 2019年12月5日 下午2:19:24
	 */
	private Integer getGroup(XxlJobInfo jobInfo) {
		if (jobInfo.getJobGroup() > 0)
			return jobInfo.getJobGroup();

		if (StringUtils.isEmpty(jobInfo.getAppName()))
			throw new XxlJobException("AppName is invalid");

		XxlJobGroup jg = jobGroupService.query(new XxlJobGroup().setAppName(jobInfo.getAppName()));
		if (jg == null)
			throw new XxlJobException("JobGroup is invalid");

		jobInfo.setJobGroup(jg.getId());
		return jobInfo.getJobGroup();
	}
}

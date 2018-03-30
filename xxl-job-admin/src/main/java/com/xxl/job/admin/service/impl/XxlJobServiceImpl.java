package com.xxl.job.admin.service.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.LocalCacheUtil;
import com.xxl.job.admin.core.util.MailBodyTemplate;
import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;

/**
 * core job action for xxl-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements XxlJobService {
	private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;
	
	@Override
	public Map<String, Object> pageList(int start, int length, int jobGroup, String jobDesc, String executorHandler, String filterTime) {

		// page list
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, jobDesc, executorHandler);
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, jobDesc, executorHandler);
		
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
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		// valid
		XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (group == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_choose")+I18nUtil.getString("jobinfo_field_jobgroup")) );
		}
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") );
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc")) );
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author")) );
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorFailStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_gluetype")+I18nUtil.getString("system_unvalid")) );
		}
		if (GlueTypeEnum.BEAN==GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"JobHandler") );
		}

		// fix "\r" in shell
		if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource()!=null) {
			jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
		}

		// ChildJobId valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobId())) {
			String[] childJobIds = StringUtils.split(jobInfo.getChildJobId(), ",");
			for (String childJobIdItem: childJobIds) {
				if (StringUtils.isNotBlank(childJobIdItem) && StringUtils.isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobIdItem));
					if (childJobInfo==null) {
						return new ReturnT<String>(ReturnT.FAIL_CODE,
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
				} else {
					return new ReturnT<String>(ReturnT.FAIL_CODE,
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
				}
			}
			jobInfo.setChildJobId(StringUtils.join(childJobIds, ","));
		}

		// add in db
		xxlJobInfoDao.save(jobInfo);
		if (jobInfo.getId() < 1) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add")+I18nUtil.getString("system_fail")) );
		}

		// add in quartz
        String qz_group = String.valueOf(jobInfo.getJobGroup());
        String qz_name = String.valueOf(jobInfo.getId());
        try {
            XxlJobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
            //XxlJobDynamicScheduler.pauseJob(qz_name, qz_group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            try {
                xxlJobInfoDao.delete(jobInfo.getId());
                XxlJobDynamicScheduler.removeJob(qz_name, qz_group);
            } catch (SchedulerException e1) {
                logger.error(e.getMessage(), e1);
            }
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add")+I18nUtil.getString("system_fail"))+":" + e.getMessage());
        }
	}

	@Override
	public ReturnT<String> update(XxlJobInfo jobInfo) {

		// valid
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") );
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc")) );
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author")) );
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorFailStrategy")+I18nUtil.getString("system_unvalid")));
		}

		// ChildJobId valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobId())) {
			String[] childJobIds = StringUtils.split(jobInfo.getChildJobId(), ",");
			for (String childJobIdItem: childJobIds) {
				if (StringUtils.isNotBlank(childJobIdItem) && StringUtils.isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobIdItem));
					if (childJobInfo==null) {
						return new ReturnT<String>(ReturnT.FAIL_CODE,
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
					// avoid cycle relate
					if (childJobInfo.getId() == jobInfo.getId()) {
						return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format(I18nUtil.getString("jobinfo_field_childJobId_limit"), childJobIdItem));
					}
				} else {
					return new ReturnT<String>(ReturnT.FAIL_CODE,
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
				}
			}
			jobInfo.setChildJobId(StringUtils.join(childJobIds, ","));
		}

		// stage job info
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
		if (exists_jobInfo == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_id")+I18nUtil.getString("system_not_found")) );
		}
		//String old_cron = exists_jobInfo.getJobCron();

		exists_jobInfo.setJobCron(jobInfo.getJobCron());
		exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
		exists_jobInfo.setAuthor(jobInfo.getAuthor());
		exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
		exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
		exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
		exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		exists_jobInfo.setExecutorFailStrategy(jobInfo.getExecutorFailStrategy());
		exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
        xxlJobInfoDao.update(exists_jobInfo);

		// fresh quartz
		String qz_group = String.valueOf(exists_jobInfo.getJobGroup());
		String qz_name = String.valueOf(exists_jobInfo.getId());
        try {
            boolean ret = XxlJobDynamicScheduler.rescheduleJob(qz_group, qz_name, exists_jobInfo.getJobCron());
            return ret?ReturnT.SUCCESS:ReturnT.FAIL;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }

		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> remove(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

		try {
			XxlJobDynamicScheduler.removeJob(name, group);
			xxlJobInfoDao.delete(id);
			xxlJobLogDao.delete(id);
			xxlJobLogGlueDao.deleteByJobId(id);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> pause(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

		try {
            boolean ret = XxlJobDynamicScheduler.pauseJob(name, group);	// jobStatus do not store
            return ret?ReturnT.SUCCESS:ReturnT.FAIL;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> resume(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

		try {
			boolean ret = XxlJobDynamicScheduler.resumeJob(name, group);
			return ret?ReturnT.SUCCESS:ReturnT.FAIL;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> triggerJob(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        if (xxlJobInfo == null) {
        	return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_id")+I18nUtil.getString("system_unvalid")) );
		}

        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

		try {
			XxlJobDynamicScheduler.triggerJob(name, group);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> dashboardInfo() {

		int jobInfoCount = xxlJobInfoDao.findAllCount();
		int jobLogCount = xxlJobLogDao.triggerCountByHandleCode(-1);
		int jobLogSuccessCount = xxlJobLogDao.triggerCountByHandleCode(ReturnT.SUCCESS_CODE);

		// executor count
		Set<String> executerAddressSet = new HashSet<String>();
		List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();

		if (CollectionUtils.isNotEmpty(groupList)) {
			for (XxlJobGroup group: groupList) {
				if (CollectionUtils.isNotEmpty(group.getRegistryList())) {
					executerAddressSet.addAll(group.getRegistryList());
				}
			}
		}

		int executorCount = executerAddressSet.size();

		Map<String, Object> dashboardMap = new HashMap<String, Object>();
		dashboardMap.put("jobInfoCount", jobInfoCount);
		dashboardMap.put("jobLogCount", jobLogCount);
		dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
		dashboardMap.put("executorCount", executorCount);
		return dashboardMap;
	}

	private static final String TRIGGER_CHART_DATA_CACHE = "trigger_chart_data_cache";
	@Override
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
		// get cache
		String cacheKey = TRIGGER_CHART_DATA_CACHE + "_" + startDate.getTime() + "_" + endDate.getTime();
		Map<String, Object> chartInfo = (Map<String, Object>) LocalCacheUtil.get(cacheKey);
		if (chartInfo != null) {
			return new ReturnT<Map<String, Object>>(chartInfo);
		}

		// process
		List<String> triggerDayList = new ArrayList<String>();
		List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		int triggerCountRunningTotal = 0;
		int triggerCountSucTotal = 0;
		int triggerCountFailTotal = 0;

		List<Map<String, Object>> triggerCountMapAll = xxlJobLogDao.triggerCountByDay(startDate, endDate);
		if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
			for (Map<String, Object> item: triggerCountMapAll) {
				String day = String.valueOf(item.get("triggerDay"));
				int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
				int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
				int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
				int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

				triggerDayList.add(day);
				triggerDayCountRunningList.add(triggerDayCountRunning);
				triggerDayCountSucList.add(triggerDayCountSuc);
				triggerDayCountFailList.add(triggerDayCountFail);

				triggerCountRunningTotal += triggerDayCountRunning;
				triggerCountSucTotal += triggerDayCountSuc;
				triggerCountFailTotal += triggerDayCountFail;
			}
		} else {
            for (int i = 4; i > -1; i--) {
                triggerDayList.add(FastDateFormat.getInstance("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -i)));
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

		// set cache
		LocalCacheUtil.set(cacheKey, result, 60*1000);     // cache 60s

		return new ReturnT<Map<String, Object>>(result);
	}

	private static final String TRIGGER_ANALYSIS_DATA_CACHE = "trigger_analysis_data_cache";
	
	@Override
	public ReturnT<Map<String, Object>> analysis(Date startDate, Date endDate) {
		// get cache
		String cacheKey = TRIGGER_ANALYSIS_DATA_CACHE + "_" + startDate.getTime() + "_" + endDate.getTime();
		Map<String, Object> chartInfo = (Map<String, Object>) LocalCacheUtil.get(cacheKey);
		if (chartInfo != null) {
			return new ReturnT<Map<String, Object>>(chartInfo);
		}

		// process
		List<String> triggerJobNameList = new ArrayList<String>();
		List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		List<Map<String, Object>> allJobList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> triggerAnalysisMapAll = xxlJobLogDao.triggerAnalysisByDay(startDate, endDate);
		if (CollectionUtils.isNotEmpty(triggerAnalysisMapAll)) {
			for (Map<String, Object> item: triggerAnalysisMapAll) {
				String jobName = String.valueOf(item.get("jobName"));
				int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
				int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
				int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
				int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;
						
				triggerJobNameList.add(jobName);
				triggerDayCountRunningList.add(triggerDayCountRunning);
				triggerDayCountSucList.add(triggerDayCountSuc);
				triggerDayCountFailList.add(triggerDayCountFail);
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("name", jobName);
				param.put("successNum", triggerDayCountSuc);
				param.put("failNum", triggerDayCountFail);
				param.put("proccesNum", triggerDayCountRunning);
				param.put("value", 0);
				allJobList.add(param);
			}
		} else {
			for (int i = 4; i > -1; i--) {
				triggerJobNameList.add(null);
				triggerDayCountSucList.add(0);
				triggerDayCountFailList.add(0);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("jobNames", triggerJobNameList);
		result.put("triggerDayCountRunningList", triggerDayCountRunningList);
		result.put("triggerDayCountSucList", triggerDayCountSucList);
		result.put("triggerDayCountFailList", triggerDayCountFailList);
		result.put("other", allJobList);
		// set cache
		LocalCacheUtil.set(cacheKey, result, 60*1000);     // cache 60s
			
		return new ReturnT<Map<String, Object>>(result);
	}

	@Override
	public ReturnT<String> monitor(String emails) {
		ReturnT<String> returnT = new ReturnT<String>();
		try {
			// 获取昨天时间
			Calendar   cal  =  Calendar.getInstance();
			cal.add(Calendar.DATE,   -1);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			String yesterday = format.format(cal.getTime());
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			Date startDate = dateFormat.parse(yesterday + " 00:00:00");
			Date endDate = dateFormat.parse(yesterday + " 23:59:59");
			List<Map<String, Object>> triggerYesterdayMapAll = xxlJobLogDao.triggerAnalysisByDay(startDate, endDate);
			
			StringBuffer data = new StringBuffer();
			data = MailBodyTemplate.template(data, true, false);
			data = MailBodyTemplate.data(triggerYesterdayMapAll, data,"昨天["+yesterday + " 00:00:00"+" ==>"+yesterday + " 23:59:59"+"]");
			
			triggerYesterdayMapAll = null;
			startDate = null;
			endDate = null;
			
			//获取最近一周
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, -7);
			String lastday = format.format(cal.getTime());
			String lasttoday = format.format(new Date());
			Date lastWeekStartDate = dateFormat.parse(lastday + " 00:00:00");
			Date lastWeekEndDate = dateFormat.parse(lasttoday + " 23:59:59");
			List<Map<String, Object>> triggerLastWeekMapAll = xxlJobLogDao.triggerAnalysisByDay(lastWeekStartDate, lastWeekEndDate);
			
			data = MailBodyTemplate.data(triggerLastWeekMapAll, data,"最近一周["+lastday + " 00:00:00"+" ==>"+lasttoday + " 23:59:59"+"]");
			
			triggerLastWeekMapAll = null;
			lastWeekStartDate = null;
			lastWeekEndDate = null;
			
			//获取本月
			
			//第一天  
	        cal = Calendar.getInstance();  
	        cal.add(Calendar.MONTH, 0);  
	        cal.set(Calendar.DAY_OF_MONTH, 1);  
	        String currentMonfirstday = format.format(cal.getTime());  
			
	        //最后一天 
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH, 0);
			String currentMonEndTime = format.format(cal.getTime());
			
			Date currentMonthStartDate = dateFormat.parse(currentMonfirstday + " 00:00:00");
			Date currentMonthEndDate = dateFormat.parse(currentMonEndTime + " 23:59:59");
			
			List<Map<String, Object>> triggerCurrentMonthMapAll = xxlJobLogDao.triggerAnalysisByDay(currentMonthStartDate, currentMonthEndDate);
			
			data = MailBodyTemplate.data(triggerCurrentMonthMapAll, data,"本月["+currentMonfirstday + " 00:00:00"+" ==>"+currentMonEndTime + " 23:59:59"+"]");
			
			triggerCurrentMonthMapAll = null;
			currentMonthStartDate = null;
			currentMonthEndDate = null;
			
			//获取最近一个月
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MONTH, -1);
	        String mon = format.format(cal.getTime());
	        String today = format.format(new Date());
	        Date lastMonthStartDate = dateFormat.parse(mon + " 00:00:00");
			Date lastMonthEndDate = dateFormat.parse(today + " 23:59:59");
			List<Map<String, Object>> triggerLastMonthMapAll = xxlJobLogDao.triggerAnalysisByDay(lastMonthStartDate, lastMonthEndDate);
			
			data = MailBodyTemplate.data(triggerLastMonthMapAll, data,"最近一个月["+mon + " 00:00:00"+" ==>"+today + " 23:59:59"+"]");
			
			triggerLastMonthMapAll = null;
			lastMonthStartDate = null;
			lastMonthEndDate = null;
			
			//获取上一个月
			
			//获取前月的第一天
	        cal=Calendar.getInstance();
	        cal.add(Calendar.MONTH, -1);
	        cal.set(Calendar.DAY_OF_MONTH,1);
	        String firstDay = format.format(cal.getTime());
	        
	        //获取前月的最后一天
	        cal=Calendar.getInstance();
	        cal.set(Calendar.DAY_OF_MONTH,0);
	        String lastDay = format.format(cal.getTime());
			
	        Date MonthStartDate = dateFormat.parse(firstDay + " 00:00:00");
			Date MonthEndDate = dateFormat.parse(lastDay + " 23:59:59");
			
			List<Map<String, Object>> MonthMapAll = xxlJobLogDao.triggerAnalysisByDay(MonthStartDate, MonthEndDate);
			
			data = MailBodyTemplate.data(MonthMapAll, data,"上个月");
			
			data = MailBodyTemplate.template(data, false, true);
			
			String title = "调度中心监控报警";
			String content = data.toString();
			
			String[] list = emails.split(",");
			for(String key : list){
				MailUtil.sendMail(key, title, content);
			}
			returnT.setCode(ReturnT.SUCCESS_CODE);
			returnT.setMsg("执行完成");
		} catch (ParseException e) {
			e.printStackTrace();
			returnT.setCode(ReturnT.FAIL_CODE);
			returnT.setMsg(e.getMessage());
		}
		
		return returnT;
	}

}

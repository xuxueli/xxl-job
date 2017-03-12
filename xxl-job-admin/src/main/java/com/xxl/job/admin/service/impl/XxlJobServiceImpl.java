package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;
import com.xxl.job.admin.service.IXxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
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
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		// valid
		XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (group == null) {
			return new ReturnT<String>(500, "请选择“执行器”");
		}
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(500, "请输入格式正确的“Cron”");
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(jobInfo.getAlarmEmail())) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(500, "路由策略非法");
		}
		if (jobInfo.getGlueSwitch()==0 && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
			return new ReturnT<String>(500, "请输入“JobHandler”");
		}

		// childJobKey valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
			String[] childJobKeys = jobInfo.getChildJobKey().split(",");
			for (String childJobKeyItem: childJobKeys) {
				String[] childJobKeyArr = childJobKeyItem.split("_");
				if (childJobKeyArr.length!=2) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
				}
				XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobKeyArr[1]));
				if (childJobInfo==null) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
				}
			}
		}

		// add in db
		xxlJobInfoDao.save(jobInfo);
		if (jobInfo.getId() < 1) {
			return new ReturnT<String>(500, "新增任务失败");
		}

		// add in quartz
        String qz_group = String.valueOf(jobInfo.getJobGroup());
        String qz_name = String.valueOf(jobInfo.getId());
        try {
            XxlJobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
            XxlJobDynamicScheduler.pauseJob(qz_name, qz_group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error("", e);
            try {
                xxlJobInfoDao.delete(jobInfo.getId());
                XxlJobDynamicScheduler.removeJob(qz_name, qz_group);
            } catch (SchedulerException e1) {
                logger.error("", e1);
            }
            return new ReturnT<String>(500, "新增任务失败:" + e.getMessage());
        }
	}

	@Override
	public ReturnT<String> reschedule(XxlJobInfo jobInfo) {

		// valid
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(500, "请输入格式正确的“Cron”");
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(jobInfo.getAlarmEmail())) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(500, "路由策略非法");
		}
		if (jobInfo.getGlueSwitch()==0 && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
			return new ReturnT<String>(500, "请输入“JobHandler”");
		}

		// childJobKey valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
			String[] childJobKeys = jobInfo.getChildJobKey().split(",");
			for (String childJobKeyItem: childJobKeys) {
				String[] childJobKeyArr = childJobKeyItem.split("_");
				if (childJobKeyArr.length!=2) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
				}
                XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobKeyArr[1]));
				if (childJobInfo==null) {
					return new ReturnT<String>(500, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
				}
			}
		}

		// stage job info
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, "参数异常");
		}

		exists_jobInfo.setJobCron(jobInfo.getJobCron());
		exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
		exists_jobInfo.setAuthor(jobInfo.getAuthor());
		exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
		exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
		exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
		exists_jobInfo.setGlueSwitch(jobInfo.getGlueSwitch());
		exists_jobInfo.setChildJobKey(jobInfo.getChildJobKey());
		
		try {
			// fresh quartz
			boolean ret = XxlJobDynamicScheduler.rescheduleJob(String.valueOf(exists_jobInfo.getJobGroup()), String.valueOf(exists_jobInfo.getId()), exists_jobInfo.getJobCron());
			if (ret) {
				xxlJobInfoDao.update(exists_jobInfo);
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> triggerJob(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

		try {
			XxlJobDynamicScheduler.triggerJob(name, group);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
}

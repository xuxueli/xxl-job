package com.xxl.job.core.extension.server.service;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.extension.server.param.JobAutoRegisterParam;
import com.xxl.job.core.extension.server.param.JobTask;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author lesl
 */
@Slf4j
@Service
public class JobAutoRegisterService {

	@Autowired
	XxlJobGroupDao xxlJobGroupDao;

	@Autowired
	XxlJobService xxlJobService;

	public XxlJobGroup createGroupIfAbsent(JobAutoRegisterParam param) {
		String appName = param.getAppName();
		return Optional.ofNullable(getGroupByAppName(appName))
				.orElseGet(() -> createGroup(param));
	}

	private XxlJobGroup getGroupByAppName(String appName) {
		// 查找appName 是否存在, 如果不存在则新建
		List<XxlJobGroup> xxlJobGroups = xxlJobGroupDao.pageList(0, 10, appName, null);
		return xxlJobGroups.stream()
				.filter(d -> d.getAppname().equals(appName))
				.findFirst()
				.orElse(null);
	}

	private XxlJobGroup createGroup(JobAutoRegisterParam param) {
		XxlJobGroup xxlJobGroup = new XxlJobGroup();
		xxlJobGroup.setAddressList(null);
		xxlJobGroup.setAddressType(0);
		xxlJobGroup.setAppname(param.getAppName());
		xxlJobGroup.setTitle(param.getAppTitle());
		xxlJobGroupDao.save(xxlJobGroup);
		return getGroupByAppName(param.getAppName());
	}


	private XxlJobInfo getTaskJobHandler(int groupId, String jobHandler) {
		Map<String, Object> stringObjectMap = xxlJobService
				.pageList(0, 10, groupId, -1, null, jobHandler, "");

		@SuppressWarnings("unchecked")
		List<XxlJobInfo> data = (List<XxlJobInfo>) stringObjectMap.get("data");
		return data.stream()
				.filter(v -> v.getExecutorHandler().equals(jobHandler))
				.findFirst()
				.orElse(null);
	}

	private void createTaskJob(int groupId, JobTask jobTask) {
		boolean isEmptyCron = StringUtils.isEmpty(jobTask.getCron());
		XxlJobInfo xxlJobInfo = new XxlJobInfo();
		xxlJobInfo.setJobGroup(groupId);
		xxlJobInfo.setJobCron(jobTask.getCron());
		// 如果cron为空则
		if (isEmptyCron) {
			xxlJobInfo.setJobCron("0 0 0 1 1 ?");
		}
		xxlJobInfo.setJobDesc(jobTask.getJobHandler());
		// 默认使用轮询的方式
		xxlJobInfo.setExecutorRouteStrategy("ROUND");
		xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
		xxlJobInfo.setExecutorHandler(jobTask.getJobHandler());
		xxlJobInfo.setAuthor("无");
		xxlJobInfo.setGlueType("BEAN");
		ReturnT<String> addResult = xxlJobService.add(xxlJobInfo);
		if (addResult.getCode() != ReturnT.SUCCESS_CODE) {
			log.error("add task job failed, {}", addResult.getMsg());
		}
		// 如果cron 不为空，则直接启动
		if(!isEmptyCron) {
			xxlJobInfo = getTaskJobHandler(groupId, jobTask.getJobHandler());
			assert xxlJobInfo != null;
			xxlJobService.start(xxlJobInfo.getId());
		}
	}


	public void createTaskIfAbsent(int groupId, JobAutoRegisterParam param) {
		param.getJobTasks().forEach(jobTask -> {
			XxlJobInfo taskJobHandler = getTaskJobHandler(groupId, jobTask.getJobHandler());
			if (taskJobHandler == null) {
				createTaskJob(groupId, jobTask);
			}
		});
	}
}

package com.xxl.job.core.extension.server.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.extension.server.param.JobAutoRegisterParam;
import com.xxl.job.core.extension.server.service.JobAutoRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lesl
 */
@RestController
@RequestMapping("/api/extension")
public class JobAutoRegisterController {

	@Autowired
	JobAutoRegisterService jobAutoRegisterService;

	@RequestMapping("/job-register")
	@ResponseBody
	@PermissionLimit(limit = false)
	public ReturnT<String> api(@RequestBody JobAutoRegisterParam param) {
		XxlJobGroup xxlJobGroup = jobAutoRegisterService.createGroupIfAbsent(param);

		int groupId = xxlJobGroup.getId();

		jobAutoRegisterService.createTaskIfAbsent(groupId, param);

		return ReturnT.SUCCESS;
	}
}

package com.xxl.job.admin.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api")
public class JobApiController {

	@Resource
	private AdminBiz adminBiz;

	/**
	 * api
	 */
	@RequestMapping("/{uri}")
	@ResponseBody
	@PermissionLimit(limit = false)
	public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

		// valid
		if (!"POST".equalsIgnoreCase(request.getMethod())) {
			return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
		}
		if (!StringUtils.hasText(uri)) {
			return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
		}
		String accessToken = XxlJobAdminConfig.getAdminConfig().getAccessToken();
		if (StringUtils.hasText(accessToken)
				&& !accessToken.equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
			return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
		}

		// services mapping
		switch (uri) {
			case "callback":
				List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
				return adminBiz.callback(callbackParamList);
			case "registry": {
				RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
				return adminBiz.registry(registryParam);
			}
			case "registryRemove": {
				RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
				return adminBiz.registryRemove(registryParam);
			}
			default:
				return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
		}

	}

}
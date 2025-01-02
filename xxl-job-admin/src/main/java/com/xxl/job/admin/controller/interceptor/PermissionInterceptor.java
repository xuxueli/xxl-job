package com.xxl.job.admin.controller.interceptor;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.impl.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 权限拦截
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {

	@Resource
	private LoginService loginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			// if need login
			boolean needLogin = true;
			boolean needAdminUser = false;
			HandlerMethod method = (HandlerMethod) handler;
			PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
			if (permission != null) {
				needLogin = permission.limit();
				needAdminUser = permission.adminuser();
			}

			if (needLogin) {
				XxlJobUser loginUser = loginService.ifLogin(request, response);
				if (loginUser == null) {
					response.setStatus(302);
					response.setHeader("location", request.getContextPath() + "/toLogin");
					return false;
				}
				if (needAdminUser && loginUser.getRole() != 1) {
					throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
				}
				request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);    // set loginUser, with request
			}
		}

		return true;    // proceed with the next interceptor
	}

	// -------------------- permission tool --------------------

	/**
	 * get loginUser
	 */
	public static XxlJobUser getLoginUser(HttpServletRequest request) {
		return (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
	}

	/**
	 * valid permission by JobGroup
	 */
	public static void validJobGroupPermission(HttpServletRequest request, int jobGroup) {
		XxlJobUser loginUser = getLoginUser(request);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username=" + loginUser.getUsername() + "]");
		}
	}

	/**
	 * filter XxlJobGroup by role
	 */
	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all) {
		if (jobGroupList_all != null && !jobGroupList_all.isEmpty()) {
			XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
			if (loginUser.getRole() == 1) {
				return jobGroupList_all;
			}
			if (StringUtils.hasLength(loginUser.getPermission())) {
				List<String> groupIdStrs = Arrays.asList(loginUser.getPermission().split(","));
				List<XxlJobGroup> jobGroupList = new ArrayList<>();
				for (XxlJobGroup groupItem : jobGroupList_all) {
					if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
						jobGroupList.add(groupItem);
					}
				}
				return jobGroupList;
			}
		}
		return Collections.emptyList();
	}

}
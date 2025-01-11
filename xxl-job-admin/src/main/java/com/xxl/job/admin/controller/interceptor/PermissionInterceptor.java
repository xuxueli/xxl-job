package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.impl.LoginService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		if (!(handler instanceof HandlerMethod)) {
			return true;	// proceed with the next interceptor
		}

		// if need login
		boolean needLogin = true;
		boolean needAdminuser = false;
		HandlerMethod method = (HandlerMethod)handler;
		PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
		if (permission!=null) {
			needLogin = permission.limit();
			needAdminuser = permission.adminuser();
		}

		if (needLogin) {
			XxlJobUser loginUser = loginService.ifLogin(request, response);
			if (loginUser == null) {
				response.sendRedirect(request.getContextPath()+"/toLogin");
				return false;
			}
			if (needAdminuser && loginUser.getRole()!=1) {
				throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
			}

			// set loginUser, with request
			setLoginUser(request, loginUser);
		}

		return true;	// proceed with the next interceptor
	}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if(request.getAttribute(LoginService.LOGIN_USER_KEY)==null) {
			if (modelAndView != null) {
				XxlJobUser loginUser = loginService.ifLogin(request, response);
				modelAndView.getModel().putIfAbsent(LoginService.LOGIN_USER_KEY, loginUser);
			}
		}
    }

	// -------------------- permission tool --------------------

	/**
	 * set loginUser
	 *
	 * @param request
	 * @param loginUser
	 */
	private static void setLoginUser(HttpServletRequest request, XxlJobUser loginUser){
		request.setAttribute(LoginService.LOGIN_USER_KEY, loginUser);
	}

	/**
	 * get loginUser
	 *
	 * @param request
	 * @return
	 */
	public static XxlJobUser getLoginUser(HttpServletRequest request){
		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_USER_KEY);	// get loginUser, with request
		return loginUser;
	}

	/**
	 * valid permission by JobGroup
	 *
	 * @param request
	 * @param jobGroup
	 */
	public static void validJobGroupPermission(HttpServletRequest request, int jobGroup) {
		XxlJobUser loginUser = getLoginUser(request);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginUser.getUsername() +"]");
		}
	}

	/**
	 * filter XxlJobGroup by role
	 *
	 * @param request
	 * @param jobGroupAllList
	 * @return
	 */
	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupAllList){
		List<XxlJobGroup> jobGroupList = new ArrayList<>();
		if (jobGroupAllList!=null && !jobGroupAllList.isEmpty()) {
			XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
			if (loginUser.getRole() == 1) {
				jobGroupList = jobGroupAllList;
			} else {
				List<String> groupIdStrs = new ArrayList<>();
				if (loginUser.getPermission()!=null && !loginUser.getPermission().trim().isEmpty()) {
					groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
				}
				for (XxlJobGroup groupItem:jobGroupAllList) {
					if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
						jobGroupList.add(groupItem);
					}
				}
			}
		}
		return jobGroupList;
	}


}

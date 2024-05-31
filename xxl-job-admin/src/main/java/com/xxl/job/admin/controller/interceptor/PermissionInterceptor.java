package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
            return true;    // proceed with the next interceptor
        }

        // if need login
        boolean needLogin = true;
        boolean needAdminuser = false;
        HandlerMethod method = (HandlerMethod) handler;
        PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
        if (permission != null) {
            needLogin = permission.limit();
            needAdminuser = permission.adminuser();
        }

        if (needLogin) {
            XxlJobUser loginUser = loginService.ifLogin(request, response);
            if (loginUser == null) {
                response.setStatus(302);
                response.setHeader("location", request.getContextPath() + "/toLogin");
                return false;
            }
            if (needAdminuser && loginUser.getRole() != 1) {
                throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
            }
            request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);
        }

        return true;    // proceed with the next interceptor
    }

    /**
     * 升级springboot3 jdk 17
     * spring6移除了对freemarker的jsp支持，
     * 所以导致了内置的Request对象用不了，可以在PermissionInterceptor下添加以下代码
     * <p> 来自issues:
     * <a  href="https://github.com/xuxueli/xxl-job/issues/3338"> https://github.com/xuxueli/xxl-job/issues/3338</a>
     * 感谢 <a  href="https://github.com/zuihou"> @zuihou </a>
     * </p>
     * @param request      请求
     * @param response     响应
     * @param handler      处理对象
     * @param modelAndView 视图
     * @throws Exception 异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            Map<String, Object> attributes = new HashMap<>();
            Enumeration<String> enumeration = request.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                attributes.put(key, request.getAttribute(key));
            }
            modelAndView.addObject("Request", attributes);
        }
    }

}

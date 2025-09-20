package com.xxl.job.admin.adapter;

import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ice2Faith
 * @date 2025/9/20 8:59
 */

public class XxlSsoWebInterceptorAdapter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoWebInterceptorAdapter.class);
    private final AntPathMatcher antPathMatcher;
    private String excludedPaths;
    private String loginPath;

    public XxlSsoWebInterceptorAdapter(String excludedPaths, String loginPath) {
        this.antPathMatcher = new AntPathMatcher();
        this.excludedPaths = excludedPaths;
        this.loginPath = loginPath;
        if (StringTool.isBlank(loginPath)) {
            this.loginPath = "/login";
        }

        logger.info("XxlSsoWebInterceptor init.");
    }

    public XxlSsoWebInterceptorAdapter(String loginPath) {
        this((String) null, loginPath);
    }

    public boolean isMatchExcludedPaths(HttpServletRequest request) {
        if (StringTool.isBlank(this.excludedPaths)) {
            return false;
        } else {
            String servletPath = request.getServletPath();
            String[] var3 = this.excludedPaths.split(",");
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String excludedPath = var3[var5];
                String uriPattern = excludedPath.trim();
                if (!StringTool.isBlank(uriPattern) && this.antPathMatcher.match(uriPattern, servletPath)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        XxlSso xxlSso = (XxlSso) method.getMethodAnnotation(XxlSso.class);
        boolean needLogin = xxlSso != null ? xxlSso.login() : true;
        String permission = xxlSso != null ? xxlSso.permission() : null;
        String role = xxlSso != null ? xxlSso.role() : null;
        if (this.isMatchExcludedPaths(request)) {
            return true;
        }
        if (!needLogin) {
            return true;
        }
        Response<LoginInfo> loginCheckResult = XxlSsoHelperAdapter.loginCheckWithCookie(request, response);
        LoginInfo loginInfo = null;
        if (loginCheckResult != null && loginCheckResult.isSuccess()) {
            loginInfo = (LoginInfo) loginCheckResult.getData();
        }

        if (loginInfo == null) {
            boolean isJson = method.getMethodAnnotation(ResponseBody.class) != null;
            if (isJson) {
                throw new XxlSsoException(501, "not login for path:" + request.getServletPath());
            }
            String finalLoginPath = request.getContextPath().concat(this.loginPath);
            response.sendRedirect(finalLoginPath);
            return false;

        }
        request.setAttribute("xxl_sso_user", loginInfo);
        if (!XxlSsoHelperAdapter.hasPermission(loginInfo, permission).isSuccess()) {
            throw new XxlSsoException("permission limit, current login-user does not have permission:" + permission);
        }
        if (!XxlSsoHelperAdapter.hasRole(loginInfo, role).isSuccess()) {
            throw new XxlSsoException("permission limit, current login-user does not have role:" + role);
        }
        return true;

    }
}

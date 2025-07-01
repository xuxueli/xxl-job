package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * API token验证拦截器
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class ApiAuthTokenInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new RuntimeException("invalid request, HttpMethod not support.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
            && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
            && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            throw new RuntimeException("The access token is wrong.");
        }
        return true;    // proceed with the next interceptor
    }

}

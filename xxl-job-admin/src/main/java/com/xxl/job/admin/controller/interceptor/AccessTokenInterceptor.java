package com.xxl.job.admin.controller.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Luo Bao Ding
 * @since 2019/5/22
 */
@Component
public class AccessTokenInterceptor extends HandlerInterceptorAdapter {
    public static final String H_ACCESS_TOKEN = "ACCESS_TOKEN";

    @Value("${xxl.job.accessToken:}")
    private String accessToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqToken = request.getHeader(H_ACCESS_TOKEN);
        if (accessToken != null && !accessToken.equals("") && !accessToken.equals(reqToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return super.preHandle(request, response, handler);
    }
}

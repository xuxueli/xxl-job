package com.xxl.job.admin.controller.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * api token拦截
 *
 * @author xingducai
 */
@Component
public class ApiInterceptor implements AsyncHandlerInterceptor {
    @Value("${xxl.api.accessToken}")
    private String accessToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if (!(handler instanceof HandlerMethod)) {
            return true;    // proceed with the next interceptor
        }
        String token = request.getHeader("access_token");
        if (accessToken != null && accessToken.equals(token)) {
            return true;
        }
        response.setStatus(302);
        response.setHeader("location", request.getContextPath() + "/toLogin");
        return false;
    }

}

package com.xxl.job.admin.common.config;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 *
 * @author Rong.Jia
 * @date 2023/07/23
 **/
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 检测全局session对象中是否有account数据，有则放行，没有则重定向到登陆界面
     * @param request 请求对象
     * @param response 响应对象
     * @param handler   处理器（url+Controller：映射）
     * @return 如果返回值为true放行当前的请求，如果为false，则表示拦截当前的请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object obj = request.getSession().getAttribute("account");
        if(ObjectUtil.isNull(obj)){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}

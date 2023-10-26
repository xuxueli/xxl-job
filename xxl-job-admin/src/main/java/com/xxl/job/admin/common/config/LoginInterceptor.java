package com.xxl.job.admin.common.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.pojo.entity.LoginToken;
import com.xxl.job.admin.common.utils.AuthUtils;
import com.xxl.job.admin.service.LoginTokenService;
import com.xxl.job.core.constants.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 *
 * @author Rong.Jia
 * @date 2023/07/23
 **/
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTokenService loginTokenService;

    /**
     * 检测全局Header对象中是否有account数据，有则放行，没有则重定向到登陆界面
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器（url+Controller：映射）
     * @return 如果返回值为true放行当前的请求，如果为false，则表示拦截当前的请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie cookie = AuthUtils.getCookie();
        if (ObjectUtil.isNull(cookie)) {
            Cookie newCookie = new Cookie(AuthConstant.AUTHORIZATION_HEADER.toUpperCase(), StrUtil.EMPTY);
            newCookie.setMaxAge(0);
            response.addCookie(newCookie);
            response.sendRedirect("login");
            return false;
        }else {
            LoginToken loginToken = loginTokenService.findLoginTokenByToken(cookie.getValue());
            if (ObjectUtil.isNull(loginToken)) {
                response.sendRedirect("login");
                return false;
            }
        }
        loginTokenService.updateLoginTokenByToken(cookie.getValue());
        return true;
    }
}

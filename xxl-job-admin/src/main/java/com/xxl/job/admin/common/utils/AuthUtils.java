package com.xxl.job.admin.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWTUtil;
import com.xxl.job.core.constants.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * auth工具类
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Slf4j
public class AuthUtils {

    /**
     * 获取当前用户
     *
     * @return {@link String}
     */
    public static String getCurrentUser() {
        Cookie cookie =getCookie();
        if (ObjectUtil.isNull(cookie)) {
            try {
                getResponse().sendRedirect("/login");
            } catch (IOException e) {
                log.error("getCurrentUser {}", e.getMessage());
            }
        }
        return JWTUtil.parseToken(cookie.getValue()).getPayload("account").toString();
    }

    private static HttpServletRequest getRequest() {
        return  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }

    private static HttpServletResponse getResponse() {
        return  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }

    public static Cookie getCookie() {
        return Arrays.stream(getRequest().getCookies())
                .filter(a -> a.getName().equals(AuthConstant.AUTHORIZATION_HEADER.toUpperCase())).findAny().orElse(null);
    }

}

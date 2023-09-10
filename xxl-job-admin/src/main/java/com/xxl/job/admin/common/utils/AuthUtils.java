package com.xxl.job.admin.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTUtil;
import com.xxl.job.admin.common.constants.AuthConstant;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * auth工具类
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
public class AuthUtils {

    /**
     * 获取当前用户
     *
     * @return {@link String}
     */
    public static String getCurrentUser() {
        String authorization = getRequest().getHeader(AuthConstant.AUTHORIZATION_HEADER);
        String parameter = getRequest().getParameter(AuthConstant.AUTHORIZATION_HEADER);

        if (StrUtil.isAllEmpty(authorization, parameter)) {
            try {
                getResponse().sendRedirect("/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String token = StrUtil.isBlank(authorization) ? parameter : authorization;
        Object account = JWTUtil.parseToken(token).getPayload("account");
        if (ObjectUtil.isNull(account)) {
            try {
                getResponse().sendRedirect("/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return account.toString();
    }

    private static HttpServletRequest getRequest() {
        return  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }

    private static HttpServletResponse getResponse() {
        return  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }




}

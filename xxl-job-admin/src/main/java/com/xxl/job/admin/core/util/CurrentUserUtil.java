package com.xxl.job.admin.core.util;

import com.xxl.job.admin.core.model.XxlJobUser;

import java.math.BigInteger;

import static com.xxl.job.admin.service.LoginService.LOGIN_IDENTITY_KEY;

public class CurrentUserUtil {

    public static  XxlJobUser getCurrentUser(){
        String cookieToken = CookieUtil.getValue(WebUtil.getRequest(), LOGIN_IDENTITY_KEY);
        if (cookieToken != null) {
            XxlJobUser cookieUser = parseToken(cookieToken);
            return cookieUser;
        }
        return null;
    }

    public static String getCurrentUserName(){
        return getCurrentUser() == null ? null : getCurrentUser().getUsername();
    }

    public static String makeToken(XxlJobUser xxlJobUser){
        String tokenJson = JacksonUtil.writeValueAsString(xxlJobUser);
        String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
        return tokenHex;
    }
    public static XxlJobUser parseToken(String tokenHex){
        XxlJobUser xxlJobUser = null;
        if (tokenHex != null) {
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
            xxlJobUser = JacksonUtil.readValue(tokenJson, XxlJobUser.class);
        }
        return xxlJobUser;
    }
}

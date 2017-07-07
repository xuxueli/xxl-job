package com.xxl.job.admin.service;

import com.xxl.job.admin.config.AppConfig;
import com.xxl.job.admin.core.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * Author: Antergone
 * Date: 2017/6/29
 */
@Service
public class LoginService {

    @Autowired
    AppConfig.LoginConfig loginConfig;

    private static final String LOGIN_IDENTITY_KEY = "LOGIN_IDENTITY";

    public boolean login(HttpServletResponse response, boolean ifRemember) {
        CookieUtil.set(response, LOGIN_IDENTITY_KEY,
                _buildToken(loginConfig.getUsername(), loginConfig.getPassword()), ifRemember);
        return true;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
    }

    public boolean ifLogin(HttpServletRequest request) {
        String identityInfo = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);

        return !(identityInfo == null ||
                !_buildToken(loginConfig.getUsername(), loginConfig.getPassword()).equals(identityInfo.trim()));
    }

    private String _buildToken(String username, String password) {
        String temp = username + "_" + password;
        return new BigInteger(1, temp.getBytes()).toString(16);
    }
}

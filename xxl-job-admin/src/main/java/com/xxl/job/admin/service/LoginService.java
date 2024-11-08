package com.xxl.job.admin.service;

import java.math.BigInteger;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.*;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Service
public class LoginService {

	public static final String LOGIN_IDENTITY_KEY = "XXL_JOB_LOGIN_IDENTITY";

	@Resource
	private XxlJobUserDao xxlJobUserDao;

	private String makeToken(XxlJobUser xxlJobUser) {
		String tokenJson = JacksonUtil.writeValueAsString(xxlJobUser);
		return new BigInteger(tokenJson.getBytes()).toString(16);
	}

	private XxlJobUser parseToken(String tokenHex) {
		XxlJobUser xxlJobUser = null;
		if (tokenHex != null) {
			String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
			xxlJobUser = JacksonUtil.readValue(tokenJson, XxlJobUser.class);
		}
		return xxlJobUser;
	}

	public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean ifRemember) {

		// param
		if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
			return new ReturnT<>(500, I18nUtil.getString("login_param_empty"));
		}

		// valid password
		XxlJobUser xxlJobUser = xxlJobUserDao.loadByUserName(username);
		if (xxlJobUser == null) {
			return new ReturnT<>(500, I18nUtil.getString("login_param_unvalid"));
		}
		String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
		if (!passwordMd5.equals(xxlJobUser.getPassword())) {
			return new ReturnT<>(500, I18nUtil.getString("login_param_unvalid"));
		}

		String loginToken = makeToken(xxlJobUser);

		// do login
		CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
		return ReturnT.SUCCESS;
	}

	/**
	 * logout
	 */
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
		return ReturnT.SUCCESS;
	}

	/**
	 * logout
	 */
	public XxlJobUser ifLogin(HttpServletRequest request, HttpServletResponse response) {
		String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
		if (cookieToken != null) {
			XxlJobUser cookieUser = null;
			try {
				cookieUser = parseToken(cookieToken);
			} catch (Exception e) {
				logout(request, response);
			}
			if (cookieUser != null) {
				XxlJobUser dbUser = xxlJobUserDao.loadByUserName(cookieUser.getUsername());
				if (dbUser != null && cookieUser.getPassword().equals(dbUser.getPassword())) {
					return dbUser;
				}
			}
		}
		return null;
	}

}
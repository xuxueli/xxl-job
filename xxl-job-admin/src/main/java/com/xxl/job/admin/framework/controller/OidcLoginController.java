package com.xxl.job.admin.framework.controller;

import com.xxl.job.admin.framework.oidc.OidcClient;
import com.xxl.job.admin.framework.oidc.OidcLoginService;
import com.xxl.job.admin.framework.oidc.OidcProperties;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.WebUtils;

import java.util.Map;

/**
 * oidc login controller (authorization code + pkce)
 *
 * login state: random secret in cookie, state/nonce/code_verifier derived from it, so no server-side session needed.
 *
 * @author siyixiong 2026-10-10
 */
@Controller
@RequestMapping("/auth/oidc")
@ConditionalOnProperty(prefix = "xxl.job.oidc", name = "enabled", havingValue = "true")
public class OidcLoginController {
	private static final Logger logger = LoggerFactory.getLogger(OidcLoginController.class);

	private static final String STATE_COOKIE = "xxl_job_oidc_state";
	private static final int STATE_COOKIE_MAX_AGE = 300;
	private static final String LOGIN_FAIL_REDIRECT = "redirect:/auth/login?oidcError=1";

	@Resource
	private OidcClient oidcClient;
	@Resource
	private OidcLoginService oidcLoginService;
	@Resource
	private OidcProperties oidcProperties;

	@RequestMapping("/login")
	@XxlSso(login = false)
	public String login(HttpServletRequest request, HttpServletResponse response) {
		try {
			String secret = OidcClient.newSecret();
			String authorizationUrl = oidcClient.buildAuthorizationUrl(
					OidcClient.deriveState(secret),
					OidcClient.deriveNonce(secret),
					secret);

			writeStateCookie(request, response, secret, STATE_COOKIE_MAX_AGE);
			return "redirect:" + authorizationUrl;
		} catch (Exception e) {
			logger.warn(">>>>>>>>>>> xxl-job oidc login fail.", e);
			return LOGIN_FAIL_REDIRECT;
		}
	}

	@RequestMapping("/callback")
	@XxlSso(login = false)
	public String callback(HttpServletRequest request, HttpServletResponse response, String code, String state) {
		Cookie stateCookie = WebUtils.getCookie(request, STATE_COOKIE);
		writeStateCookie(request, response, "", 0);
		try {
			// valid state
			String secret = stateCookie != null ? stateCookie.getValue() : null;
			if (secret == null || !OidcClient.deriveState(secret).equals(state)) {
				throw new IllegalStateException("oidc state invalid");
			}
			if (code == null) {
				throw new IllegalStateException("oidc code missing, error=" + sanitize(request.getParameter("error"))
						+ ", error_description=" + sanitize(request.getParameter("error_description")));
			}

			// exchange code, valid id_token
			Map<String, Object> claims = oidcClient.exchangeAndValidate(code, secret, OidcClient.deriveNonce(secret));

			// xxl-sso, do login
			LoginInfo loginInfo = oidcLoginService.login(claims);
			Response<String> result = XxlSsoHelper.loginWithCookie(loginInfo, response, false);
			if (!result.isSuccess()) {
				throw new IllegalStateException("xxl-sso login fail, msg=" + result.getMsg());
			}
			return "redirect:/";
		} catch (Exception e) {
			logger.warn(">>>>>>>>>>> xxl-job oidc callback fail.", e);
			return LOGIN_FAIL_REDIRECT;
		}
	}

	private void writeStateCookie(HttpServletRequest request, HttpServletResponse response, String value, int maxAge) {
		String redirectUri = oidcProperties.getRedirectUri();
		ResponseCookie cookie = ResponseCookie.from(STATE_COOKIE, value)
				.httpOnly(true)
				.secure(request.isSecure() || (redirectUri != null && redirectUri.startsWith("https://")))
				.path(StringTool.isNotBlank(request.getContextPath()) ? request.getContextPath() : "/")
				.maxAge(maxAge)
				.sameSite("Lax")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	/**
	 * idp error param for log: strip CR/LF, limit length
	 */
	private static String sanitize(String value) {
		if (value == null) {
			return null;
		}
		value = value.replaceAll("[\\r\\n]", "");
		return value.length() > 100 ? value.substring(0, 100) : value;
	}

}

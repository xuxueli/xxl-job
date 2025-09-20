package com.xxl.sso.core.helper;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Ice2Faith
 * @date 2025/9/19 18:41
 */
public class XxlSsoHelper {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoHelper.class);
    private static XxlSsoHelper instance;
    private final LoginStore loginStore;
    private String tokenKey;
    private long tokenTimeout;

    public static void init(LoginStore loginStore, String tokenKey, long tokenTimeout) {
        instance = new XxlSsoHelper(loginStore, tokenKey, tokenTimeout);
    }

    public static XxlSsoHelper getInstance() {
        return instance;
    }

    public XxlSsoHelper(LoginStore loginStore, String tokenKey, long tokenTimeout) {
        this.loginStore = loginStore;
        this.tokenKey = tokenKey;
        this.tokenTimeout = tokenTimeout;
        if (!StringUtils.hasText(this.tokenKey)) {
            this.tokenKey = "xxl_sso_token";
        }

        if (this.tokenTimeout <= 0L) {
            this.tokenTimeout = 315360000000L;
        }

    }

    public LoginStore getLoginStore() {
        return this.loginStore;
    }

    public String getTokenKey() {
        return this.tokenKey;
    }

    public long getTokenTimeout() {
        return this.tokenTimeout;
    }

    public static Response<String> login(LoginInfo loginInfo) {
        Response<String> tokenResponse = TokenHelper.generateToken(loginInfo);
        if (!tokenResponse.isSuccess()) {
            return tokenResponse;
        } else {
            loginInfo.setExpireTime(System.currentTimeMillis() + getInstance().getTokenTimeout());
            Response<String> setResponse = getInstance().getLoginStore().set(loginInfo);
            return !setResponse.isSuccess() ? setResponse : Response.ofSuccess((String)tokenResponse.getData());
        }
    }

    public static Response<String> loginWithCookie(LoginInfo loginInfo, HttpServletResponse response, boolean ifRemember) {
        Response<String> loginResult = login(loginInfo);
        if (loginResult.isSuccess()) {
            String token = (String)loginResult.getData();
            CookieTool.set(response, getInstance().getTokenKey(), token, ifRemember);
        }

        return loginResult;
    }

    public static Response<String> loginUpdate(LoginInfo loginInfo) {
        if (loginInfo != null) {
            loginInfo.setExpireTime(System.currentTimeMillis() + getInstance().getTokenTimeout());
        }

        return getInstance().getLoginStore().update(loginInfo);
    }

    public static Response<String> logout(String token) {
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        return loginInfoForToken == null ? Response.ofFail("token is invalid") : getInstance().getLoginStore().remove(loginInfoForToken.getUserId());
    }

    public static Response<String> logoutWithCookie(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieTool.getValue(request, getInstance().getTokenKey());
        if (!StringUtils.hasText(token)) {
            return Response.ofSuccess();
        } else {
            Response<String> logoutResult = logout(token);
            CookieTool.remove(request, response, getInstance().getTokenKey());
            return logoutResult;
        }
    }

    public static Response<LoginInfo> loginCheck(String token) {
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken != null && StringUtils.hasText(loginInfoForToken.getSignature())) {
            Response<LoginInfo> loginInfoResponse = getInstance().getLoginStore().get(loginInfoForToken.getUserId());
            if (!loginInfoResponse.isSuccess()) {
                return loginInfoResponse;
            } else {
                LoginInfo loginInfo = (LoginInfo)loginInfoResponse.getData();
                return !loginInfoForToken.getSignature().equals(loginInfo.getSignature()) ? Response.ofFail("token signature is invalid") : Response.ofSuccess(loginInfo);
            }
        } else {
            return Response.ofFail("token is invalid");
        }
    }

    public static Response<LoginInfo> loginCheckWithHeader(HttpServletRequest request) {
        String token = request.getHeader(getInstance().getTokenKey());
        return loginCheck(token);
    }

    public static Response<LoginInfo> loginCheckWithCookie(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieTool.getValue(request, getInstance().getTokenKey());
        Response<LoginInfo> result = loginCheck(token);
        if (result == null || !result.isSuccess()) {
            CookieTool.remove(request, response, getInstance().getTokenKey());
        }

        return result;
    }

    public static Response<LoginInfo> loginCheckWithAttr(HttpServletRequest request) {
        LoginInfo loginInfo = (LoginInfo)request.getAttribute("xxl_sso_user");
        return loginInfo != null ? Response.ofSuccess(loginInfo) : Response.ofFail("not login.");
    }

    public static Response<String> createTicket(HttpServletRequest request) {
        String token = CookieTool.getValue(request, getInstance().getTokenKey());
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null) {
            return Response.ofFail("not login.");
        } else {
            String ticket = loginInfoForToken.getUserId().concat("_").concat(UUID.randomUUID().toString().replaceAll("-",""));
            long ticketTimeout = 60000L;
            return getInstance().getLoginStore().createTicket(ticket, token, ticketTimeout);
        }
    }

    public static Response<LoginInfo> validTicket(HttpServletRequest request, HttpServletResponse response) {
        String ticket = request.getParameter("xxl_sso_ticket");
        if (!StringUtils.hasText(ticket)) {
            return Response.ofFail("ticket is null.");
        } else {
            Response<String> validTicketResult = getInstance().getLoginStore().validTicket(ticket);
            if (!validTicketResult.isSuccess()) {
                return Response.ofFail(validTicketResult.getMsg());
            } else {
                String token = (String)validTicketResult.getData();
                Response<LoginInfo> result = loginCheck(token);
                if (result.isSuccess()) {
                    CookieTool.set(response, getInstance().getTokenKey(), token, false);
                }

                return loginCheck(token);
            }
        }
    }

    public static Response<String> hasRole(LoginInfo loginInfo, String role) {
        if (!StringUtils.hasText(role)) {
            return Response.ofSuccess();
        } else if (loginInfo.getRoleList()==null || loginInfo.getRoleList().isEmpty()) {
            return Response.ofFail("roleList is null.");
        } else {
            return loginInfo.getRoleList().contains(role) ? Response.ofSuccess() : Response.ofFail("has no role.");
        }
    }

    public static Response<String> hasPermission(LoginInfo loginInfo, String permission) {
        if (!StringUtils.hasText(permission)) {
            return Response.ofSuccess();
        } else if (loginInfo.getPermissionList()==null || loginInfo.getPermissionList().isEmpty()) {
            return Response.ofFail("permissionList is null.");
        } else {
            return loginInfo.getPermissionList().contains(permission) ? Response.ofSuccess() : Response.ofFail("has no permission.");
        }
    }
}

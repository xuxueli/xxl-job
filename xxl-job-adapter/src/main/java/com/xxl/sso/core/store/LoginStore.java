package com.xxl.sso.core.store;


import com.xxl.tool.response.Response;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.model.LoginInfo;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:31
 */

public interface LoginStore {
    default void start() {
    }

    default void stop() {
    }

    Response<String> set(LoginInfo var1);

    Response<String> update(LoginInfo var1);

    Response<String> remove(String var1);

    Response<LoginInfo> get(String var1);

    default Response<String> createTicket(String userId, String token, long ticketTimeout) {
        throw new XxlSsoException("default not support.");
    }

    default Response<String> validTicket(String ticket) {
        throw new XxlSsoException("default not support.");
    }
}

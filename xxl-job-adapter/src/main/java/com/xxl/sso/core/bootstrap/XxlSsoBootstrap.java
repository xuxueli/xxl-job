package com.xxl.sso.core.bootstrap;

import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.store.LoginStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ice2Faith
 * @date 2025/9/20 10:18
 */
public class XxlSsoBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoBootstrap.class);
    private LoginStore loginStore;
    private String tokenKey;
    private long tokenTimeout;

    public XxlSsoBootstrap() {
    }

    public void setLoginStore(LoginStore loginStore) {
        this.loginStore = loginStore;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public void setTokenTimeout(long tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public LoginStore getLoginStore() {
        return this.loginStore;
    }

    public void start() {
        this.loginStore.start();
        XxlSsoHelper.init(this.loginStore, this.tokenKey, this.tokenTimeout);
        logger.info(">>>>>>>>>>> xxl-mq XxlSsoBootstrap started.");
    }

    public void stop() {
        this.loginStore.stop();
        logger.info(">>>>>>>>>>> xxl-mq XxlSsoBootstrap stopped.");
    }
}

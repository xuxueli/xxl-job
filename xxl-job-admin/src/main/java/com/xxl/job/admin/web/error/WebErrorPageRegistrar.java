package com.xxl.job.admin.web.error;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.stereotype.Component;

/**
 * error page
 */
@Component
public class WebErrorPageRegistrar implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage errorPage = new ErrorPage("/errorpage");
        registry.addErrorPages(errorPage);
    }
}

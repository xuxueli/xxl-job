package com.xxl.job.admin.framework.controller;

import com.xxl.job.admin.business.controller.AbstractSpringMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * oidc disabled by default
 */
public class OidcLoginDisabledControllerTest extends AbstractSpringMvcTest {

    @Test
    public void loginPageHideSsoButton() throws Exception {
        MvcResult ret = mockMvc.perform(get("/auth/login")).andReturn();

        assertFalse(ret.getResponse().getContentAsString().contains("/auth/oidc/login"));
    }

}

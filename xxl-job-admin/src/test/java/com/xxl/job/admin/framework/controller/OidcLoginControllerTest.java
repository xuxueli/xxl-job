package com.xxl.job.admin.framework.controller;

import com.xxl.job.admin.business.controller.AbstractSpringMvcTest;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.xxl.job.admin.framework.oidc.OidcClient;
import com.xxl.sso.core.helper.XxlSsoHelper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestPropertySource(properties = "xxl.job.oidc.enabled=true")
public class OidcLoginControllerTest extends AbstractSpringMvcTest {

    private static final String STATE_COOKIE = "xxl_job_oidc_state";

    @MockitoBean
    private OidcClient oidcClient;

    @Test
    public void loginPageShowSsoButton() throws Exception {
        MvcResult ret = mockMvc.perform(get("/auth/login")).andReturn();

        assertTrue(ret.getResponse().getContentAsString().contains("/auth/oidc/login"));
    }

    @Test
    public void loginRedirectToIdp() throws Exception {
        when(oidcClient.buildAuthorizationUrl(anyString(), anyString(), anyString())).thenReturn("https://idp.example.com/auth?client_id=xxl-job-admin");

        MvcResult ret = mockMvc.perform(get("/auth/oidc/login")).andReturn();

        assertEquals(302, ret.getResponse().getStatus());
        assertEquals("https://idp.example.com/auth?client_id=xxl-job-admin", ret.getResponse().getRedirectedUrl());
        Cookie stateCookie = ret.getResponse().getCookie(STATE_COOKIE);
        assertNotNull(stateCookie);
        assertTrue(stateCookie.isHttpOnly());
        assertEquals(300, stateCookie.getMaxAge());
        assertEquals("/", stateCookie.getPath());
    }

    @Test
    public void callbackWithoutCookie() throws Exception {
        MvcResult ret = mockMvc.perform(get("/auth/oidc/callback")
                .param("code", "c1")
                .param("state", OidcClient.deriveState(OidcClient.newSecret()))).andReturn();

        assertEquals("/auth/login?oidcError=1", ret.getResponse().getRedirectedUrl());
        verify(oidcClient, never()).exchangeAndValidate(any(), any(), any());
    }

    @Test
    public void callbackIdpErrorWithoutCode() throws Exception {
        String secret = OidcClient.newSecret();

        MvcResult ret = mockMvc.perform(get("/auth/oidc/callback")
                .param("error", "access_denied")
                .param("error_description", "user denied\r\nfake log line")
                .param("state", OidcClient.deriveState(secret))
                .cookie(new Cookie(STATE_COOKIE, secret))).andReturn();

        assertEquals("/auth/login?oidcError=1", ret.getResponse().getRedirectedUrl());
        assertEquals(0, ret.getResponse().getCookie(STATE_COOKIE).getMaxAge());
        verify(oidcClient, never()).exchangeAndValidate(any(), any(), any());
    }

    @Test
    public void callbackExchangeFail() throws Exception {
        String secret = OidcClient.newSecret();
        when(oidcClient.exchangeAndValidate(any(), any(), any())).thenThrow(new GeneralException("token request fail"));

        MvcResult ret = mockMvc.perform(get("/auth/oidc/callback")
                .param("code", "c1")
                .param("state", OidcClient.deriveState(secret))
                .cookie(new Cookie(STATE_COOKIE, secret))).andReturn();

        assertEquals("/auth/login?oidcError=1", ret.getResponse().getRedirectedUrl());
        assertEquals(0, ret.getResponse().getCookie(STATE_COOKIE).getMaxAge());
        assertNull(ret.getResponse().getCookie(XxlSsoHelper.getInstance().getTokenKey()));
    }

    @Test
    public void callbackStateMismatch() throws Exception {
        String secret = OidcClient.newSecret();

        MvcResult ret = mockMvc.perform(get("/auth/oidc/callback")
                .param("code", "c1")
                .param("state", "wrong-state")
                .cookie(new Cookie(STATE_COOKIE, secret))).andReturn();

        assertEquals("/auth/login?oidcError=1", ret.getResponse().getRedirectedUrl());
        assertEquals(0, ret.getResponse().getCookie(STATE_COOKIE).getMaxAge());
        verify(oidcClient, never()).exchangeAndValidate(any(), any(), any());
    }

    @Test
    public void callbackSuccess() throws Exception {
        String secret = OidcClient.newSecret();
        when(oidcClient.exchangeAndValidate("c1", secret, OidcClient.deriveNonce(secret)))
                .thenReturn(Map.of("preferred_username", "admin"));

        MvcResult ret = mockMvc.perform(get("/auth/oidc/callback")
                .param("code", "c1")
                .param("state", OidcClient.deriveState(secret))
                .cookie(new Cookie(STATE_COOKIE, secret))).andReturn();

        assertEquals("/", ret.getResponse().getRedirectedUrl());
        assertNotNull(ret.getResponse().getCookie(XxlSsoHelper.getInstance().getTokenKey()));
    }

}

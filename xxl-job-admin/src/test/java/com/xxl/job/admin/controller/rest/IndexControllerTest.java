package com.xxl.job.admin.controller.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.admin.controller.AbstractSpringMvcTest;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class IndexControllerTest extends AbstractSpringMvcTest {

    private Cookie cookie;

    @BeforeEach
    public void login() throws Exception {
        final LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUsername("admin");
        loginInfo.setPassword("123456");
        final String json = JSON.toJSONString(loginInfo);
        MvcResult ret = mockMvc.perform(
            post("/rest/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());

        cookie = ret.getResponse().getCookie(LoginService.LOGIN_IDENTITY_KEY);
    }

    @Test
    public void test10DashboardInfo() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/dashboardInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test11ChartInfo() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/chartInfo?startDate=2021-02-03 00:00:00&endDate=2021-02-03 23:59:59")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }
}

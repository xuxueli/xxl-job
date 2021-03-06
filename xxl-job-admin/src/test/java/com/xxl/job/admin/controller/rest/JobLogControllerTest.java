package com.xxl.job.admin.controller.rest;

import com.alibaba.fastjson.JSON;
import com.xxl.job.admin.controller.AbstractSpringMvcTest;
import com.xxl.job.admin.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class JobLogControllerTest extends AbstractSpringMvcTest {

    private Cookie cookie;

    private static Integer createdId;

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
        cookie = ret.getResponse().getCookie(LoginService.LOGIN_IDENTITY_KEY);
    }


    @Test
    public void test10Get() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/jobLogs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        final String contentAsString = ret.getResponse().getContentAsString();
        System.out.println(contentAsString);
    }

    @Test
    public void test10List() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/jobLogs")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        final String contentAsString = ret.getResponse().getContentAsString();
        System.out.println(contentAsString);
    }
}

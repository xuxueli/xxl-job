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

public class JobGroupControllerTest extends AbstractSpringMvcTest {

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
    public void test00Add() throws Exception {
        final XxlJobGroup jobGroup = new XxlJobGroup();
        jobGroup.setAppname("xxl-job-executor-test");
        jobGroup.setTitle("测试执行器");

        final String json = JSON.toJSONString(jobGroup);

        MvcResult ret = mockMvc.perform(
            post("/rest/jobGroups")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)
                .cookie(cookie)
        ).andReturn();


        ret.getResponse().setCharacterEncoding("utf-8");
        final String contentAsString = ret.getResponse().getContentAsString();
        final JSONObject jsonObject = JSON.parseObject(contentAsString);
        createdId = jsonObject.getInteger("content");
        System.out.println(contentAsString);
    }

    @Test
    public void test10Get() throws Exception {

        MvcResult ret = mockMvc.perform(
            get("/rest/jobGroups/" + createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();


        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test11List() throws Exception {

        MvcResult ret = mockMvc.perform(
            get("/rest/jobGroups/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test12ListMy() throws Exception {

        MvcResult ret = mockMvc.perform(
            get("/rest/jobGroups/i")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test20Update() throws Exception {

        final XxlJobGroup jobGroup = new XxlJobGroup();
        jobGroup.setAppname("xxl-job-executor-test");
        jobGroup.setTitle("测试执行器-update");

        final String json = JSON.toJSONString(jobGroup);

        MvcResult ret = mockMvc.perform(
            put("/rest/jobGroups/" + createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test30Delete() throws Exception {

        MvcResult ret = mockMvc.perform(
            delete("/rest/jobGroups/" + createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

}

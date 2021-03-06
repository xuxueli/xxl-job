package com.xxl.job.admin.controller.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.admin.controller.AbstractSpringMvcTest;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class JobInfoControllerTest extends AbstractSpringMvcTest {

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
        final XxlJobInfo job = new XxlJobInfo();
        job.setAuthor("xxljob");
        job.setJobGroup(1);
        job.setJobDesc("测试任务2");
        job.setScheduleType("CRON");
        job.setScheduleConf("0 0 0 * * ? *");
        job.setGlueType("BEAN");
        job.setExecutorHandler("abc");
        job.setExecutorRouteStrategy("FIRST");
        job.setMisfireStrategy("DO_NOTHING");
        job.setExecutorBlockStrategy("SERIAL_EXECUTION");

        final String json = JSON.toJSONString(job);

        MvcResult ret = mockMvc.perform(
            post("/rest/jobs")
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
    public void test10List() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test10Get() throws Exception {
        System.out.println("暂无");
    }

    @Test
    public void test11ListByGroup() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/jobs/all?jobGroup=1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test20Update() throws Exception {

        final XxlJobInfo job = new XxlJobInfo();
        job.setAuthor("xxljob");
        job.setJobGroup(1);
        job.setJobDesc("测试任务2-update");
        job.setScheduleType("CRON");
        job.setScheduleConf("0 0 0 * * ? *");
        job.setGlueType("BEAN");
        job.setExecutorHandler("abc");
        job.setExecutorRouteStrategy("FIRST");
        job.setMisfireStrategy("DO_NOTHING");
        job.setExecutorBlockStrategy("SERIAL_EXECUTION");

        final String json = JSON.toJSONString(job);

        MvcResult ret = mockMvc.perform(
            put("/rest/jobs/" + createdId)
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
            delete("/rest/jobs/" + createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

    @Test
    public void test40NextTriggerTime() throws Exception {
        MvcResult ret = mockMvc.perform(
            get("/rest/jobs/nextTriggerTimes?scheduleType=FIX_RATE&scheduleConf=3")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .cookie(cookie)
        ).andReturn();

        ret.getResponse().setCharacterEncoding("utf-8");
        System.out.println(ret.getResponse().getContentAsString());
    }

}

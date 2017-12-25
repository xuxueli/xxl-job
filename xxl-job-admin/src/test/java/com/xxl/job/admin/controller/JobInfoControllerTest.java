package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class JobInfoControllerTest extends AbstractSpringMvcTest {
  Cookie cookie;

  @Before
  public void login() throws Exception {
    MvcResult ret = mockMvc.perform(
        post("/login")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("userName", "admin")
            .param("password", "123456")
    ).andReturn();
    cookie = ret.getResponse().getCookie("LOGIN_IDENTITY");
  }

  @Test
  public void testAdd() throws Exception {
    XxlJobInfo jobInfo = new XxlJobInfo();
    jobInfo.setJobGroup(1);
    jobInfo.setJobDesc("autoEnquiryStatisPerWeek");
    jobInfo.setExecutorRouteStrategy("FIRST");
    jobInfo.setJobCron("0 0 1 ? * MON");
    jobInfo.setGlueType("BEAN");
    jobInfo.setExecutorHandler("AutoEnquriy");
    jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
    jobInfo.setExecutorFailStrategy("FAIL_ALARM");
    jobInfo.setAuthor("listening");

    ObjectMapper mapper = new ObjectMapper();
    String jobInfoStr = mapper.writeValueAsString(jobInfo);
    MvcResult ret = mockMvc.perform(
        post("/jobinfo/add")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(jobInfoStr)
            .cookie(cookie)
    ).andReturn();

    System.out.println(ret.getResponse().getContentAsString());
  }
}

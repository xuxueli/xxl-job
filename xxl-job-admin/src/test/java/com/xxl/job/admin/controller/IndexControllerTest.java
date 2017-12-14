package com.xxl.job.admin.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class IndexControllerTest extends AbstractSpringMvcTest {

  @Test
  public void testLogin() throws Exception {
    MvcResult ret = mockMvc.perform(
        post("/login")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("userName", "admin")
            .param("password", "123456")
    ).andReturn();

    System.out.println(ret.getResponse().getContentAsString());
  }
}

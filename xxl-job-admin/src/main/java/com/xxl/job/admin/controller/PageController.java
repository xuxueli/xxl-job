package com.xxl.job.admin.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 页面控制器，用于页面转发
 *
 * @author Rong.Jia
 * @date 2023/06/01
 */
@Slf4j
@Api(hidden = true)
@Controller
public class PageController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("welcome")
    public String welcome() {
        return "welcome";
    }

    @RequestMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @RequestMapping("page-jobGroup")
    public String keystore() {
        return "jobGroup";
    }

    @RequestMapping("page-jobInfo")
    public String project() {
        return "jobInfo";
    }

    @RequestMapping("page-jobLog")
    public String license() {
        return "jobLog";
    }

    @RequestMapping("page-userInfo")
    public String userInfo() {
        return "userInfo";
    }

    @RequestMapping("webide")
    public void webide(@RequestParam("glueType") String glueType,
                         @RequestParam(name = "jobId", defaultValue = "-1")Long jobId,
                         HttpServletResponse response) throws IOException {
        response.sendRedirect("/webide?glueType=" + glueType + "&jobId=" + jobId);
    }















}

package com.xxl.job.admin.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/index.html")
    public String index() {
        return "index";
    }

    @RequestMapping("welcome.html")
    public String welcome() {
        return "welcome";
    }

    @RequestMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @RequestMapping("page-jobGroup.html")
    public String keystore() {
        return "jobGroup";
    }

    @RequestMapping("page-jobInfo.html")
    public String project() {
        return "jobInfo";
    }

    @RequestMapping("page-jobLog.html")
    public String license() {
        return "jobLog";
    }

    @RequestMapping("page-userInfo.html")
    public String userInfo() {
        return "userInfo";
    }
















}

package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller(value = "restIndexController")
@RequestMapping("/rest")
public class IndexController {

    @Resource
    private LoginService loginService;
    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private XxlJobUserDao xxlJobUserDao;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<XxlJobUser> login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginInfo loginInfo) {
        final ReturnT<String> loginResult = loginService.login(request, response,
            loginInfo.getUsername(), loginInfo.getPassword(), loginInfo.isIfRemember()
        );

        final ReturnT<XxlJobUser> xxlJobUserReturnT = new ReturnT<>();
        xxlJobUserReturnT.setCode(loginResult.getCode());
        xxlJobUserReturnT.setMsg(loginResult.getMsg());

        if (loginResult.getCode() != 200) {
            return xxlJobUserReturnT;
        } else {
            XxlJobUser xxlJobUser =
                xxlJobUserDao.loadByUserName(loginInfo.getUsername());
            xxlJobUser.setPassword(null);
            return new ReturnT<>(xxlJobUser);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return loginService.logout(request, response);
    }

    @GetMapping("/dashboardInfo")
    @ResponseBody
    public ReturnT<Map<String, Object>> dashboardInfo() {
        final Map<String, Object> dashboardInfo = xxlJobService.dashboardInfo();
        return new ReturnT<>(dashboardInfo);
    }

    @GetMapping("/chartInfo")
    @ResponseBody
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(startDate, endDate);
        return chartInfo;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}

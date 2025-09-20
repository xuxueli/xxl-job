package com.xxl.job.admin.controller;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.xxl.job.admin.security.SecurityContext;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.sso.core.annotation.XxlSso;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import javax.script.ScriptException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

    @Resource
    private XxlJobService xxlJobService;


    @RequestMapping("/")
    public String index(Model model) {

        Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
        model.addAllAttributes(dashboardMap);

        return "index";
    }

    @RequestMapping("/chartInfo")
    @ResponseBody
    public ReturnT<Map<String, Object>> chartInfo(@RequestParam("startDate") Date startDate, @RequestParam("endDate") Date endDate) {
        ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(startDate, endDate);
        return chartInfo;
    }
/*
    @RequestMapping("/toLogin")
    @XxlSso(login = false)
    public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
        if (loginService.ifLogin(request, response) != null) {
            modelAndView.setView(new RedirectView("/", true, false));
            return modelAndView;
        }
        return new ModelAndView("login");
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @XxlSso(login = false)
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("userName") String userName,
                                   @RequestParam("password") String password,
                                   @RequestParam("sign") String sign,
                                   @RequestParam(value = "ifRemember", required = false) String ifRemember) throws ScriptException {
        Keypair keypair = SecurityContext.getInstance().findKeypair(sign);
        if (keypair == null) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }
        password = Sm2.doDecrypt(password, keypair.getPrivateKey());
        boolean ifRem = "on".equals(ifRemember);
        return loginService.login(request, response, userName, password, ifRem);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @XxlSso(login = false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return loginService.logout(request, response);
    }*/

    @RequestMapping(value = "spk", method = RequestMethod.POST)
    @ResponseBody
    @XxlSso(login = false)
    public ReturnT<String> getServerPublicKey(HttpServletRequest request, HttpServletResponse response,
                                              @RequestParam("pk") String pk,
                                              @RequestParam("sign") String sign) throws ScriptException {
        if (!StringUtils.hasLength(pk) || !StringUtils.hasLength(sign)) {
            return new ReturnT<String>(500, I18nUtil.getString("system_fail"));
        }
        if (!Sm3.sm3(pk).equals(sign)) {
            return new ReturnT<String>(500, I18nUtil.getString("system_fail"));
        }
        Keypair keypair = SecurityContext.getInstance().currentKeypair();
        String publicKey = keypair.getPublicKey();
        String ret = Sm2.doEncrypt(publicKey, pk);
        return ReturnT.ofSuccess(ret);
    }


    @RequestMapping("/help")
    public String help() {
        return "help";
    }

    @RequestMapping(value = "/errorpage")
    @XxlSso(login = false)
    public ModelAndView errorPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) {

        String exceptionMsg = "HTTP Status Code: " + response.getStatus();

        mv.addObject("exceptionMsg", exceptionMsg);
        mv.setViewName("common/common.errorpage");
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}

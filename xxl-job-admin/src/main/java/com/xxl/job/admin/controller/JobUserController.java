package com.xxl.job.admin.controller;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.controller.interceptor.PermissionInterceptor;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.platform.pageable.data.PageDto;
import com.xxl.job.admin.security.SecurityContext;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
@RequestMapping("/user")
public class JobUserController {

    @Resource
    private XxlJobUserDao xxlJobUserDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping
    @PermissionLimit(adminuser = true)
    public String index(Model model) {

        // 执行器列表
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public Map<String, Object> pageList(@RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                        @RequestParam(value = "length", required = false, defaultValue = "10") int length,
                                        @RequestParam("username") String username,
                                        @RequestParam("role") int role) {

        // page list
        PageDto page=PageDto.of(start/length+1,length);
        List<XxlJobUser> list= xxlJobUserDao.pageList(page, username, role);
        int list_count = xxlJobUserDao.pageListCount( username, role);

        // filter
        if (list!=null && !list.isEmpty()) {
            for (XxlJobUser item: list) {
                item.setPassword(null);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> add(XxlJobUser xxlJobUser) throws ScriptException {
        // valid password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }

        Keypair keypair = SecurityContext.getInstance().findKeypair(xxlJobUser.getSign());
        if(keypair==null){
            return new ReturnT<String>(500, I18nUtil.getString("system_fail"));
        }
        xxlJobUser.setPassword(Sm2.doDecrypt(xxlJobUser.getPassword(),keypair.getPrivateKey()));

        // valid username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length()>=4 && xxlJobUser.getUsername().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }

        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // hash password
        xxlJobUser.setPassword(SecurityContext.getInstance().encodePassword(xxlJobUser.getPassword()));

        // check repeat
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(xxlJobUser.getUsername());
        if (existUser != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("user_username_repeat") );
        }

        // write
        xxlJobUserDao.save(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> update(HttpServletRequest request, XxlJobUser xxlJobUser) throws ScriptException {

        // avoid opt login seft
        XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
        if (loginUser.getUsername().equals(xxlJobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        // valid password
        if (StringUtils.hasText(xxlJobUser.getPassword())) {
            Keypair keypair = SecurityContext.getInstance().findKeypair(xxlJobUser.getSign());
            if(keypair==null){
                return new ReturnT<String>(500, I18nUtil.getString("system_fail"));
            }
            xxlJobUser.setPassword(Sm2.doDecrypt(xxlJobUser.getPassword(),keypair.getPrivateKey()));
            if(!StringUtils.hasText(xxlJobUser.getRepeatPassword())){
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("repeat_password_not_match") );
            }
            xxlJobUser.setRepeatPassword(Sm2.doDecrypt(xxlJobUser.getRepeatPassword(),keypair.getPrivateKey()));
            if(!Objects.equals(xxlJobUser.getPassword(),xxlJobUser.getRepeatPassword())){
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("repeat_password_not_match") );
            }

            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
            }
            // hash password
            xxlJobUser.setPassword(SecurityContext.getInstance().encodePassword(xxlJobUser.getPassword()));
        } else {
            xxlJobUser.setPassword(null);
        }

        // write
        xxlJobUserDao.update(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> remove(HttpServletRequest request, @RequestParam("id") int id) {

        // avoid opt login seft
        XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
        if (loginUser.getId() == id) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        xxlJobUserDao.delete(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request,
                                     @RequestParam("password") String password,
                                     @RequestParam("repeatPassword") String repeatPassword,
                                     @RequestParam("oldPassword") String oldPassword,
                                     @RequestParam("sign") String sign) throws ScriptException {

        // valid
        if (!StringUtils.hasText(oldPassword)){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
        }
        if (!StringUtils.hasText(password)){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
        }
        if (!StringUtils.hasText(repeatPassword)){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
        }


        Keypair keypair = SecurityContext.getInstance().findKeypair(sign);
        if(keypair==null){
            return new ReturnT<String>(500, I18nUtil.getString("system_fail"));
        }
        password= Sm2.doDecrypt(password,keypair.getPrivateKey());
        repeatPassword= Sm2.doDecrypt(repeatPassword,keypair.getPrivateKey());
        oldPassword=Sm2.doDecrypt(oldPassword,keypair.getPrivateKey());

        password = password.trim();
        repeatPassword = repeatPassword.trim();
        oldPassword=oldPassword.trim();

        if(!Objects.equals(password,repeatPassword)){
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("repeat_password_not_match") );
        }

        if (!(password.length()>=4 && password.length()<=50)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }

        // hash password
        String hashPassword = SecurityContext.getInstance().encodePassword(password);

        // valid old pwd
        XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(loginUser.getUsername());
        if(!SecurityContext.getInstance().matchPassword(oldPassword,existUser.getPassword())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("change_pwd_field_oldpwd") + I18nUtil.getString("system_unvalid"));
        }

        // write new
        existUser.setPassword(hashPassword);
        xxlJobUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

}

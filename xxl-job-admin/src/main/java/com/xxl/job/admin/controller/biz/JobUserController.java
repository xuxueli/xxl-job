package com.xxl.job.admin.controller.biz;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.admin.platform.pageable.data.PageDto;
import com.xxl.job.admin.security.SecurityContext;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
@RequestMapping("/user")
public class JobUserController {

    @Resource
    private XxlJobUserMapper xxlJobUserMapper;
    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;

    @RequestMapping
    @XxlSso(role = Consts.ADMIN_ROLE)
    public String index(Model model) {

        // 执行器列表
        List<XxlJobGroup> groupList = xxlJobGroupMapper.findAll();
        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Map<String, Object> pageList(@RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                        @RequestParam(value = "length", required = false, defaultValue = "10") int length,
                                        @RequestParam("username") String username,
                                        @RequestParam("role") int role) {

        // page list
        PageDto page = PageDto.of(start / length + 1, length);
        List<XxlJobUser> list = xxlJobUserMapper.pageList(page, username, role);
        int listCount = xxlJobUserMapper.pageListCount( username, role);

        // filter
        if (list != null && !list.isEmpty()) {
            for (XxlJobUser item : list) {
                item.setPassword(null);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", listCount);        // 总记录数
        maps.put("recordsFiltered", listCount);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<String> add(XxlJobUser xxlJobUser) throws Exception {
        Keypair keypair = SecurityContext.getInstance().findKeypair(xxlJobUser.getSign());
        if (keypair == null) {
            return ReturnT.ofFail(I18nUtil.getString("login_param_unvalid"));
        }
        String decodePass = Sm2.doDecrypt(xxlJobUser.getPassword(), keypair.getPrivateKey());
        xxlJobUser.setPassword(decodePass);

        // valid username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return ReturnT.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("user_username"));
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length() >= 4 && xxlJobUser.getUsername().length() <= 20)) {
            return ReturnT.ofFail(I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }
        // valid password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return ReturnT.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("user_password"));
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length() >= 4 && xxlJobUser.getPassword().length() <= 20)) {
            return ReturnT.ofFail(I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }
        // md5 password
        String passwordHash = SecurityContext.getInstance().encodePassword(xxlJobUser.getPassword());
        xxlJobUser.setPassword(passwordHash);

        // check repeat
        XxlJobUser existUser = xxlJobUserMapper.loadByUserName(xxlJobUser.getUsername());
        if (existUser != null) {
            return ReturnT.ofFail(I18nUtil.getString("user_username_repeat"));
        }

        // write
        xxlJobUserMapper.save(xxlJobUser);
        return ReturnT.ofSuccess();
    }

    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<String> update(HttpServletRequest request, XxlJobUser xxlJobUser) throws Exception {
        // avoid opt login seft
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (loginInfoResponse.getData().getUserName().equals(xxlJobUser.getUsername())) {
            return ReturnT.ofFail(I18nUtil.getString("user_update_loginuser_limit"));
        }

        // valid password
        if (StringUtils.hasText(xxlJobUser.getPassword())) {
            Keypair keypair = SecurityContext.getInstance().findKeypair(xxlJobUser.getSign());
            if (keypair == null) {
                return ReturnT.ofFail(I18nUtil.getString("login_param_unvalid"));
            }
            String decodePass = Sm2.doDecrypt(xxlJobUser.getPassword(), keypair.getPrivateKey());
            xxlJobUser.setPassword(decodePass);
            String decodeRepeatPass = Sm2.doDecrypt(xxlJobUser.getRepeatPassword(), keypair.getPrivateKey());
            xxlJobUser.setRepeatPassword(decodeRepeatPass);
            if(!decodePass.equals(decodeRepeatPass)){
                return ReturnT.ofFail(I18nUtil.getString("repeat_password_not_match"));
            }

            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length() >= 4 && xxlJobUser.getPassword().length() <= 20)) {
                return ReturnT.ofFail(I18nUtil.getString("system_lengh_limit") + "[4-20]");
            }
            // md5 password
            String passwordHash = SecurityContext.getInstance().encodePassword(xxlJobUser.getPassword());
            xxlJobUser.setPassword(passwordHash);
        } else {
            xxlJobUser.setPassword(null);
        }

        // write
        xxlJobUserMapper.update(xxlJobUser);
        return ReturnT.ofSuccess();
    }

    @RequestMapping("/remove")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<String> remove(HttpServletRequest request, @RequestParam("id") int id) {

        // avoid opt login seft
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (Integer.parseInt(loginInfoResponse.getData().getUserId()) == id) {
            return ReturnT.ofFail(I18nUtil.getString("user_update_loginuser_limit"));
        }

        xxlJobUserMapper.delete(id);
        return ReturnT.ofSuccess();
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request,
                                     @RequestParam("password") String password,
                                     @RequestParam("oldPassword") String oldPassword,
                                     @RequestParam("repeatPassword") String repeatPassword,
                                     @RequestParam("sign") String sign) throws Exception {

        // valid
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return ReturnT.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ReturnT.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
        }

        Keypair keypair = SecurityContext.getInstance().findKeypair(sign);
        if (keypair == null) {
            return ReturnT.ofFail(I18nUtil.getString("login_param_unvalid"));
        }
        password = Sm2.doDecrypt(password, keypair.getPrivateKey());
        oldPassword = Sm2.doDecrypt(oldPassword, keypair.getPrivateKey());
        repeatPassword = Sm2.doDecrypt(repeatPassword, keypair.getPrivateKey());
        if(!password.equals(repeatPassword)){
            return ReturnT.ofFail(I18nUtil.getString("repeat_password_not_match"));
        }

        password = password.trim();
        if (!(password.length() >= 4 && password.length() <= 20)) {
            return ReturnT.ofFail(I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }

        // md5 password
        String passwordHash = SecurityContext.getInstance().encodePassword(password);

        // valid old pwd
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        XxlJobUser existUser = xxlJobUserMapper.loadByUserName(loginInfoResponse.getData().getUserName());
        if (!SecurityContext.getInstance().matchPassword(oldPassword,existUser.getPassword())) {
            return ReturnT.ofFail(I18nUtil.getString("change_pwd_field_oldpwd") + I18nUtil.getString("system_unvalid"));
        }

        // write new
        existUser.setPassword(passwordHash);
        xxlJobUserMapper.update(existUser);

        return ReturnT.ofSuccess();
    }

}

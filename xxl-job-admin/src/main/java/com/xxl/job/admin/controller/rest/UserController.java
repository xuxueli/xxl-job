package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.core.biz.model.ReturnT;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Tag(name = "用户管理")
@RestController(value = "restUserController")
@RequestMapping("/rest/users")
public class UserController {

    @Resource
    private XxlJobUserDao xxlJobUserDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;


    @GetMapping("")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<PageInfo<XxlJobUser>> list(@RequestParam(required = false, defaultValue = "1") int current,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                                              @RequestParam(required = false, defaultValue = "") String username,
                                              @RequestParam(required = false, defaultValue = "-1") int role) {

        int start = (current - 1) * pageSize;
        // page list
        List<XxlJobUser> list = xxlJobUserDao.pageList(start, pageSize, username, role);
        int listCount = xxlJobUserDao.pageListCount(start, pageSize, username, role);

        // filter
        if (list != null && list.size() > 0) {
            for (XxlJobUser item : list) {
                item.setPassword(null);
            }
        }

        final PageInfo<XxlJobUser> pageInfo = new PageInfo<>(list, listCount, current, pageSize);
        final ReturnT<PageInfo<XxlJobUser>> ret = new ReturnT<>(pageInfo);

        return ret;
    }

    @PostMapping("")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> create(@RequestBody XxlJobUser xxlJobUser) {

        // valid username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input") + I18nUtil.getString("user_username"));
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length() >= 4 && xxlJobUser.getUsername().length() <= 20)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }
        // valid password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input") + I18nUtil.getString("user_password"));
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length() >= 4 && xxlJobUser.getPassword().length() <= 20)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }
        // md5 password
        xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));

        // check repeat
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(xxlJobUser.getUsername());
        if (existUser != null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("user_username_repeat"));
        }

        // write
        xxlJobUserDao.save(xxlJobUser);
        return new ReturnT<>(String.valueOf(xxlJobUser.getId()));
    }

    @PutMapping("/{id}")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> update(@PathVariable int id, @RequestBody XxlJobUser xxlJobUser) {

        xxlJobUser.setId(id);

        // valid password
        if (StringUtils.hasText(xxlJobUser.getPassword())) {
            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length() >= 4 && xxlJobUser.getPassword().length() <= 20)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
            }
            // md5 password
            xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));
        } else {
            xxlJobUser.setPassword(null);
        }

        // write
        xxlJobUserDao.update(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> delete(@PathVariable int id) {
        xxlJobUserDao.delete(id);
        return ReturnT.SUCCESS;
    }

    @PutMapping("/i/pwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, @RequestParam("value") String password) {

        // valid password
        if (password == null || password.trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }

        password = password.trim();
        if (!(password.length() >= 4 && password.length() <= 20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }

        // md5 password
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // update pwd
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);

        // do write
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(loginUser.getUsername());
        existUser.setPassword(md5Password);
        xxlJobUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

    @GetMapping("/i")
    @ResponseBody
    public ReturnT<XxlJobUser> currentUser(HttpServletRequest request) {
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        loginUser.setPassword(null);
        return new ReturnT<>(loginUser);
    }


}

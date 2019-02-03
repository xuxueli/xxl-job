package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermessionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.CookieUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.service.impl.XxlJobServiceImpl;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author liuyang 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobuser")
public class JobUserController {

    private static Logger logger = LoggerFactory.getLogger(JobUserController.class);

    @Resource
    private XxlJobUserDao xxlJobUserDao;

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping
    public String index(Model model) {

        List<XxlJobGroup> jobGroups = xxlJobGroupDao.findAll();
        model.addAttribute("jobGroups",jobGroups);
        return "jobuser/jobuser.index";
    }


    @RequestMapping("pageList")
    @ResponseBody
    @PermessionLimit(adminuser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username,
                                        int permission) {

        // xxlConfNode in mysql
        List<XxlJobUser> data = xxlJobUserDao.pageList(start, length, username, permission);
        int list_count = xxlJobUserDao.pageListCount(start, length, username, permission);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("data", data);
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        return maps;
    }


    @RequestMapping("/add")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> add(XxlJobUser xxlJobUser){

        // valid
        if (StringUtils.isBlank(xxlJobUser.getUsername())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名不可为空");
        }

        if(xxlJobUser.getUsername().length() < 5 || xxlJobUser.getUsername().length() > 50){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名长度限制为5~50");
        }

        if (StringUtils.isBlank(xxlJobUser.getPassword())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=100)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
        }

        // passowrd md5
        String md5Password = DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes());
        xxlJobUser.setPassword(md5Password);

        int ret = xxlJobUserDao.add(xxlJobUser);
        return ret>0? ReturnT.SUCCESS: ReturnT.FAIL;
    }

    /**
     * delete
     *
     * @return
     */
    @RequestMapping("/delete")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> delete(HttpServletRequest request, String username){

        String indentityInfo = CookieUtil.getValue(request, XxlJobServiceImpl.LOGIN_IDENTITY_KEY);
        String[] split = indentityInfo.split(XxlJobServiceImpl.LOGIN_IDENTITY_SPLIT);

        String name = split[0];
        if (name.equals(username)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "禁止操作当前登录账号");
        }

        xxlJobUserDao.delete(username);
        return ReturnT.SUCCESS;
    }

    /**
     * update
     *
     * @return
     */
    @RequestMapping("/update")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> update(HttpServletRequest request, XxlJobUser xxlJobUser){

        

        // valid
        if (StringUtils.isBlank(xxlJobUser.getUsername())){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名不可为空");
        }

        XxlJobUser existUser = xxlJobUserDao.loadByName(xxlJobUser.getUsername());
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "用户名非法");
        }

        if (StringUtils.isNotBlank(xxlJobUser.getPassword())) {
            if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=50)) {
                return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
            }
            // passowrd md5
            String md5Password = DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes());
            existUser.setPassword(md5Password);
        }
        existUser.setPermission(xxlJobUser.getPermission());

        int ret = xxlJobUserDao.update(existUser);
        return ret>0? ReturnT.SUCCESS: ReturnT.FAIL;
    }


    @RequestMapping("/updatePermissionData")
    @PermessionLimit(adminuser = true)
    @ResponseBody
    public ReturnT<String> updatePermissionData(HttpServletRequest request,
                                                String username,
                                                @RequestParam(required = false) String[] permissionData){

        XxlJobUser existUser = xxlJobUserDao.loadByName(username);
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "参数非法");
        }

        String permissionDataArrStr = permissionData!=null?StringUtils.join(permissionData, ","):"";
        existUser.setPermissionData(permissionDataArrStr);
        xxlJobUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

}

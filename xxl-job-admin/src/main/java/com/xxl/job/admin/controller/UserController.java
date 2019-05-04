package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private XxlJobUserDao xxlJobUserDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping
    public String index(Model model) {

        // 执行器列表
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username) {

        // page list
        List<XxlJobUser> list = xxlJobUserDao.pageList(start, length, username);
        int list_count = xxlJobUserDao.pageListCount(start, length, username);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(XxlJobUser xxlJobUser) {

        // valid username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length()>=4 && xxlJobUser.getUsername().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // valid password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // md5 password
        xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));

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
    public ReturnT<String> update(XxlJobUser xxlJobUser) {

        // valid password
        if (StringUtils.hasText(xxlJobUser.getPassword())) {
            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
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

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        xxlJobUserDao.delete(id);
        return ReturnT.SUCCESS;
    }

}

package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.admin.service.JobUserService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Sha256Tool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
@RequestMapping("/user")
public class JobUserController {

    @Resource
    private JobUserService jobUserService;
    @Resource
    private JobGroupService jobGroupService;

    @RequestMapping
    @XxlSso(role = Consts.ADMIN_ROLE)
    public String index(Model model) {
        List<XxlJobGroup> groupList = jobGroupService.findAll();
        model.addAttribute("groupList", groupList);
        return "biz/user.list";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<PageModel<XxlJobUser>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                    @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                    @RequestParam String username,
                                                    @RequestParam int role) {
        PageModel<XxlJobUser> pageModel = jobUserService.pageList(offset, pagesize, username, role);
        return Response.ofSuccess(pageModel);
    }

    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> insert(XxlJobUser xxlJobUser) {
        // valid username
        if (StringTool.isBlank(xxlJobUser.getUsername())) {
            return Response.ofFail(I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length()>=4 && xxlJobUser.getUsername().length()<=20)) {
            return Response.ofFail(I18nUtil.getString("system_length_limit")+"[4-20]" );
        }
        // valid password
        if (StringTool.isBlank(xxlJobUser.getPassword())) {
            return Response.ofFail(I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
            return Response.ofFail(I18nUtil.getString("system_length_limit")+"[4-20]" );
        }
        // md5 password
        String passwordHash = Sha256Tool.sha256(xxlJobUser.getPassword());
        xxlJobUser.setPassword(passwordHash);

        int ret = jobUserService.add(xxlJobUser, 0);
        return ret>0?Response.ofSuccess():Response.ofFail();
    }

    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(HttpServletRequest request, XxlJobUser xxlJobUser) {
        // avoid opt login self
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (loginInfoResponse.getData().getUserName().equals(xxlJobUser.getUsername())) {
            return Response.ofFail(I18nUtil.getString("user_update_loginuser_limit"));
        }

        // valid password
        if (StringTool.isNotBlank(xxlJobUser.getPassword())) {
            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
                return Response.ofFail(I18nUtil.getString("system_length_limit")+"[4-20]" );
            }
            // md5 password
            String passwordHash = Sha256Tool.sha256(xxlJobUser.getPassword());
            xxlJobUser.setPassword(passwordHash);
        } else {
            xxlJobUser.setPassword(null);
        }

        boolean ret = jobUserService.update(xxlJobUser, 0);
        return ret?Response.ofSuccess():Response.ofFail();
    }

    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {
        if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
            return Response.ofFail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
        }

        // avoid opt login self
        Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
        if (ids.contains(Integer.parseInt(loginInfoResponse.getData().getUserId()))) {
            return Response.ofFail(I18nUtil.getString("user_update_loginuser_limit"));
        }

        boolean ret = jobUserService.remove(ids.get(0), 0);
        return ret?Response.ofSuccess():Response.ofFail();
    }

}
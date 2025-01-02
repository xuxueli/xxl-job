package com.xxl.job.admin.controller;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.controller.interceptor.PermissionInterceptor;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.ModelUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
	                                    @RequestParam(required = false, defaultValue = "10") int length,
	                                    String username, int role) {

		// page list
		List<XxlJobUser> list = xxlJobUserDao.pageList(start, length, username, role);
		int totalCount = ModelUtil.calcTotalCount(list, start, length);
		if (totalCount == -1) {
			totalCount = xxlJobUserDao.pageListCount(start, length, username, role);
		}

		// filter
		if (!list.isEmpty()) { // MyBatis 返回的 List 不会为 null
			for (XxlJobUser item : list) {
				item.setPassword(null);
			}
		}

		return ModelUtil.pageListResult(list, totalCount);
	}

	@RequestMapping("/add")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> add(XxlJobUser xxlJobUser) {
		// valid username
		String username = xxlJobUser.getUsername();
		if (!StringUtils.hasText(username)) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input") + I18nUtil.getString("user_username"));
		}
		xxlJobUser.setUsername(username = username.trim());
		int usernameLength = username.length();
		if (!(usernameLength >= 4 && usernameLength <= 20)) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
		}
		// valid password
		String password = xxlJobUser.getPassword();
		if (!StringUtils.hasText(password)) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input") + I18nUtil.getString("user_password"));
		}
		xxlJobUser.setPassword(password = password.trim());
		if (!(password.length() >= 4 && password.length() <= 20)) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
		}
		// md5 password
		xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

		// check repeat
		XxlJobUser existUser = xxlJobUserDao.loadByUserName(username);
		if (existUser != null) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("user_username_repeat"));
		}

		// write
		xxlJobUserDao.save(xxlJobUser);
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/update")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> update(HttpServletRequest request, XxlJobUser xxlJobUser) {

		// avoid opt login seft
		XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
		if (loginUser.getUsername().equals(xxlJobUser.getUsername())) {
			return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
		}

		// valid password
		String password = xxlJobUser.getPassword();
		if (StringUtils.hasText(password)) {
			xxlJobUser.setPassword(password = password.trim());
			if (!(password.length() >= 4 && password.length() <= 20)) {
				return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
			}
			// md5 password
			xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
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
	public ReturnT<String> remove(HttpServletRequest request, int id) {

		// avoid opt login seft
		XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
		if (loginUser.getId() == id) {
			return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
		}

		xxlJobUserDao.delete(id);
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/updatePwd")
	@ResponseBody
	public ReturnT<String> updatePwd(HttpServletRequest request, String password, String oldPassword) {
		// valid
		if (!StringUtils.hasText(oldPassword)) {
			return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
		}
		if (!StringUtils.hasText(password)) {
			return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
		}
		password = password.trim();
		if (!(password.length() >= 4 && password.length() <= 20)) {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
		}

		// md5 password
		String md5OldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

		// valid old pwd
		XxlJobUser loginUser = PermissionInterceptor.getLoginUser(request);
		XxlJobUser existUser = xxlJobUserDao.loadByUserName(loginUser.getUsername());
		if (!md5OldPassword.equals(existUser.getPassword())) {
			return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("change_pwd_field_oldpwd") + I18nUtil.getString("system_unvalid"));
		}

		// write new
		existUser.setPassword(md5Password);
		xxlJobUserDao.update(existUser);

		return ReturnT.SUCCESS;
	}

}
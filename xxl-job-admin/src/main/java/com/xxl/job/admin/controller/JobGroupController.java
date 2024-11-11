package com.xxl.job.admin.controller;

import java.util.*;
import javax.annotation.Resource;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.ModelUtil;
import com.xxl.job.admin.dao.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * job group controller
 *
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobRegistryDao xxlJobRegistryDao;

	@RequestMapping
	@PermissionLimit(adminuser = true)
	public String index() {
		return "jobgroup/jobgroup.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
	                                    @RequestParam(required = false, defaultValue = "10") int length,
	                                    String appname, String title) {

		// page query
		List<XxlJobGroup> list = xxlJobGroupDao.pageList(start, length, appname, title);
		int totalCount = ModelUtil.calcTotalCount(list, start, length);
		if (totalCount == -1) {
			totalCount = xxlJobGroupDao.pageListCount(start, length, appname, title);
		}

		return ModelUtil.pageListResult(list, totalCount);
	}

	@RequestMapping("/save")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> save(XxlJobGroup xxlJobGroup) {
		// valid
		if (!StringUtils.hasText(xxlJobGroup.getAppname())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + "AppName"));
		}
		int nameLength = xxlJobGroup.getAppname().length();
		if (nameLength < 4 || nameLength > 64) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_appname_length"));
		}
		if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
			return new ReturnT<>(500, "AppName" + I18nUtil.getString("system_unvalid"));
		}
		if (!StringUtils.hasText(xxlJobGroup.getTitle())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
		}
		if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
		}
		if (xxlJobGroup.getAddressType() != 0) {
			if (!StringUtils.hasText(xxlJobGroup.getAddressList())) {
				return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
			}
			if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
				return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
			}

			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item : addresss) {
				if (!StringUtils.hasText(item)) {
					return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
				}
			}
		}

		// process
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupDao.save(xxlJobGroup);
		return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> update(XxlJobGroup xxlJobGroup) {
		// valid
		if (!StringUtils.hasText(xxlJobGroup.getAppname())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + "AppName"));
		}
		int nameLength = xxlJobGroup.getAppname().length();
		if (nameLength < 4 || nameLength > 64) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_appname_length"));
		}
		if (!StringUtils.hasText(xxlJobGroup.getTitle())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
		}
		if (xxlJobGroup.getAddressType() == 0) {
			// 0=自动注册
			final TreeSet<String> registries = findRegistryByAppName(xxlJobGroup.getAppname());
			String addressListStr = null;
			if (registries != null && !registries.isEmpty()) {
				addressListStr = String.join(",", registries);
			}
			xxlJobGroup.setAddressList(addressListStr);
		} else {
			// 1=手动录入
			if (!StringUtils.hasText(xxlJobGroup.getAddressList())) {
				return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
			}
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item : addresss) {
				if (!StringUtils.hasText(item)) {
					return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
				}
			}
		}

		// process
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupDao.update(xxlJobGroup);
		return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	private TreeSet<String> findRegistryByAppName(String appnameParam) {
		List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
		if (!list.isEmpty()) {
			final TreeSet<String> nodes = new TreeSet<>();
			for (XxlJobRegistry t : list) {
				if (appnameParam.equals(t.getRegistryKey()) && RegistryConfig.RegistType.EXECUTOR.name().equals(t.getRegistryGroup())) {
					nodes.add(t.getRegistryValue());
				}
			}
			return nodes;
		}
		return null;
	}

	@RequestMapping("/remove")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> remove(int id) {

		// valid
		int count = xxlJobInfoDao.pageListCount(0, 10, id, -1, null, null, null);
		if (count > 0) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_del_limit_0"));
		}

		List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_del_limit_1"));
		}
		int ret = xxlJobGroupDao.remove(id);
		return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	@RequestMapping("/loadById")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<XxlJobGroup> loadById(int id) {
		XxlJobGroup jobGroup = xxlJobGroupDao.load(id);
		return jobGroup != null ? new ReturnT<>(jobGroup) : new ReturnT<>(ReturnT.FAIL_CODE, null);
	}

}
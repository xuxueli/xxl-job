package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.ReturnT;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.thread.JobRegistryHelper;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.core.registry.RegistHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * job group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	public IXxlJobInfoDao xxlJobInfoDao;
	@Resource
	public IXxlJobGroupDao xxlJobGroupDao;

	@RequestMapping
	public String index(Model model) {

		// job admin
		List<String> adminAddressList = JobRegistryHelper.discover(RegistHelper.RegistType.ADMIN.name(), RegistHelper.RegistType.ADMIN.name());

		// job group (executor)
		List<XxlJobGroup> list = xxlJobGroupDao.findAll();

		if (CollectionUtils.isNotEmpty(list)) {
			for (XxlJobGroup group: list) {
				List<String> registryList = JobRegistryHelper.discover(RegistHelper.RegistType.EXECUTOR.name(), group.getAppName());
				group.setRegistryList(registryList);
			}
		}

		model.addAttribute("adminAddressList", adminAddressList);
		model.addAttribute("list", list);
		return "jobgroup/jobgroup.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(XxlJobGroup xxlJobGroup){

		// valid
		if (xxlJobGroup.getAppName()==null || StringUtils.isBlank(xxlJobGroup.getAppName())) {
			return new ReturnT<String>(500, "请输入AppName");
		}
		if (xxlJobGroup.getAppName().length()>64) {
			return new ReturnT<String>(500, "AppName长度限制为4~64");
		}
		if (xxlJobGroup.getTitle()==null || StringUtils.isBlank(xxlJobGroup.getTitle())) {
			return new ReturnT<String>(500, "请输入名称");
		}

		int ret = xxlJobGroupDao.save(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobGroup xxlJobGroup){
		// valid
		if (xxlJobGroup.getAppName()==null || StringUtils.isBlank(xxlJobGroup.getAppName())) {
			return new ReturnT<String>(500, "请输入AppName");
		}
		if (xxlJobGroup.getAppName().length()>64) {
			return new ReturnT<String>(500, "AppName长度限制为4~64");
		}
		if (xxlJobGroup.getTitle()==null || StringUtils.isBlank(xxlJobGroup.getTitle())) {
			return new ReturnT<String>(500, "请输入名称");
		}

		int ret = xxlJobGroupDao.update(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id){

		// valid
		int count = xxlJobInfoDao.pageListCount(0, 10, id, null);
		if (count > 0) {
			return new ReturnT<String>(500, "该分组使用中, 不可删除");
		}

		List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<String>(500, "删除失败, 系统需要至少预留一个默认分组");
		}

		int ret = xxlJobGroupDao.remove(id);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}

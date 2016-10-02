package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.ReturnT;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
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
		List<XxlJobGroup> list = xxlJobGroupDao.findAll();
		model.addAttribute("list", list);
		return "jobgroup/jobgroup.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(XxlJobGroup xxlJobGroup){

		// valid
		if (xxlJobGroup.getGroupName()==null || StringUtils.isBlank(xxlJobGroup.getGroupName())) {
			return new ReturnT<String>(500, "请输入分组");
		}
		if (xxlJobGroup.getGroupDesc()==null || StringUtils.isBlank(xxlJobGroup.getGroupDesc())) {
			return new ReturnT<String>(500, "请输入描述");
		}

		// check repeat
		XxlJobGroup group = xxlJobGroupDao.load(xxlJobGroup.getGroupName());
		if (group!=null) {
			return new ReturnT<String>(500, "分组已存在, 请勿重复添加");
		}

		int ret = xxlJobGroupDao.save(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobGroup xxlJobGroup){
		int ret = xxlJobGroupDao.update(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String groupName){

		// valid
		int count = xxlJobInfoDao.pageListCount(0, 10, groupName, null);
		if (count > 0) {
			return new ReturnT<String>(500, "该分组使用中, 不可删除");
		}

		int ret = xxlJobGroupDao.remove(groupName);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}

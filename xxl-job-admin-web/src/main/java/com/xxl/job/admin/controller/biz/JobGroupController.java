package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * job group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	private JobGroupService jobGroupService;

	@RequestMapping
	@XxlSso(role = Consts.ADMIN_ROLE)
	public String index(Model model) {
		return "biz/group.list";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<PageModel<XxlJobGroup>> pageList(
		@RequestParam(required = false, defaultValue = "0") int offset,
		@RequestParam(required = false, defaultValue = "10") int pagesize,
		String appname, String title) {
		return Response.ofSuccess(jobGroupService.pageList(offset, pagesize, appname, title));
	}

	@RequestMapping("/insert")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<String> insert(XxlJobGroup xxlJobGroup) {
		int ret = jobGroupService.save(xxlJobGroup);
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/update")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<String> update(XxlJobGroup xxlJobGroup) {
		int ret = jobGroupService.update(xxlJobGroup);
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/delete")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<String> delete(@RequestParam("ids[]") List<Integer> ids) {
		int ret = jobGroupService.remove(ids);
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/loadById")
	@ResponseBody
	public Response<XxlJobGroup> loadById(@RequestParam("id") int id) {
		XxlJobGroup jobGroup = jobGroupService.load(id);
		return jobGroup != null ? Response.ofSuccess(jobGroup) : Response.ofFail();
	}

}
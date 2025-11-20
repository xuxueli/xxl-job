package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobRegistry;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobRegistryMapper;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.constant.RegistType;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * job group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	public XxlJobInfoMapper xxlJobInfoMapper;
	@Resource
	public XxlJobGroupMapper xxlJobGroupMapper;
	@Resource
	private XxlJobRegistryMapper xxlJobRegistryMapper;

	@RequestMapping
	@XxlSso(role = Consts.ADMIN_ROLE)
	public String index(Model model) {
		return "biz/group.list";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<PageModel<XxlJobGroup>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
													 @RequestParam(required = false, defaultValue = "10") int pagesize,
													 String appname,
													 String title) {

		// page query
		List<XxlJobGroup> list = xxlJobGroupMapper.pageList(offset, pagesize, appname, title);
		int list_count = xxlJobGroupMapper.pageListCount(offset, pagesize, appname, title);

		// package result
		PageModel<XxlJobGroup> pageModel = new PageModel<>();
		pageModel.setData(list);
		pageModel.setTotal(list_count);

		return Response.ofSuccess(pageModel);
	}

	@RequestMapping("/insert")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<String> insert(XxlJobGroup xxlJobGroup){

		// valid
		if (StringTool.isBlank(xxlJobGroup.getAppname())) {
			return Response.ofFail((I18nUtil.getString("system_please_input")+"AppName") );
		}
		if (xxlJobGroup.getAppname().length()<4 || xxlJobGroup.getAppname().length()>64) {
			return Response.ofFail( I18nUtil.getString("jobgroup_field_appname_length") );
		}
		if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
			return Response.ofFail( "AppName"+I18nUtil.getString("system_unvalid") );
		}
		if (StringTool.isBlank(xxlJobGroup.getTitle())) {
			return Response.ofFail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
		}
		if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
			return Response.ofFail(I18nUtil.getString("jobgroup_field_title")+I18nUtil.getString("system_unvalid") );
		}
		if (xxlJobGroup.getAddressType()!=0) {
			if (StringTool.isBlank(xxlJobGroup.getAddressList())) {
				return Response.ofFail( I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
				return Response.ofFail(I18nUtil.getString("jobgroup_field_registryList")+I18nUtil.getString("system_unvalid") );
			}

			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (StringTool.isBlank(item)) {
					return Response.ofFail( I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		// process
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupMapper.save(xxlJobGroup);
		return (ret>0)?Response.ofSuccess():Response.ofFail();
	}

	@RequestMapping("/update")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<String> update(XxlJobGroup xxlJobGroup){
		// valid
		if (StringTool.isBlank(xxlJobGroup.getAppname())) {
			return Response.ofFail((I18nUtil.getString("system_please_input")+"AppName") );
		}
		if (xxlJobGroup.getAppname().length()<4 || xxlJobGroup.getAppname().length()>64) {
			return Response.ofFail( I18nUtil.getString("jobgroup_field_appname_length") );
		}
		if (StringTool.isBlank(xxlJobGroup.getTitle())) {
			return Response.ofFail( (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
		}
		if (xxlJobGroup.getAddressType() == 0) {
			// 0=自动注册
			List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());
			String addressListStr = null;
			if (CollectionTool.isNotEmpty(registryList)) {
				Collections.sort(registryList);
				addressListStr = String.join(",", registryList);
			}
			xxlJobGroup.setAddressList(addressListStr);
		} else {
			// 1=手动录入
			if (StringTool.isBlank(xxlJobGroup.getAddressList())) {
				return Response.ofFail( I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (StringTool.isBlank(item)) {
					return Response.ofFail(I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		// process
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupMapper.update(xxlJobGroup);
		return (ret>0)?Response.ofSuccess():Response.ofFail();
	}

	private List<String> findRegistryByAppName(String appnameParam){
		HashMap<String, List<String>> appAddressMap = new HashMap<>();
		List<XxlJobRegistry> list = xxlJobRegistryMapper.findAll(Const.DEAD_TIMEOUT, new Date());
		if (CollectionTool.isNotEmpty(list)) {
			for (XxlJobRegistry item: list) {
				if (!RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
					continue;
				}

				String appname = item.getRegistryKey();
                List<String> registryList = appAddressMap.computeIfAbsent(appname, k -> new ArrayList<>());

                if (!registryList.contains(item.getRegistryValue())) {
					registryList.add(item.getRegistryValue());
				}
			}
		}
		return appAddressMap.get(appnameParam);
	}

	@RequestMapping("/delete")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){

		// valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			return Response.ofFail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
		}
		int id = ids.get(0);

		// valid
		int count = xxlJobInfoMapper.pageListCount(0, 10, id, -1,  null, null, null);
		if (count > 0) {
			return Response.ofFail( I18nUtil.getString("jobgroup_del_limit_0") );
		}

		List<XxlJobGroup> allList = xxlJobGroupMapper.findAll();
		if (allList.size() == 1) {
			return Response.ofFail( I18nUtil.getString("jobgroup_del_limit_1") );
		}

		int ret = xxlJobGroupMapper.remove(id);
		return (ret>0)?Response.ofSuccess():Response.ofFail();
	}

	@RequestMapping("/loadById")
	@ResponseBody
	@XxlSso(role = Consts.ADMIN_ROLE)
	public Response<XxlJobGroup> loadById(@RequestParam("id") int id){
		XxlJobGroup jobGroup = xxlJobGroupMapper.load(id);
		return jobGroup!=null?Response.ofSuccess(jobGroup):Response.ofFail();
	}

}

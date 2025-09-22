package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobRegistry;
import com.xxl.job.admin.platform.pageable.data.PageDto;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.sso.core.annotation.XxlSso;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * job group controller
 *
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
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                        @RequestParam(value = "length", required = false, defaultValue = "10") int length,
                                        @RequestParam("appname") String appname,
                                        @RequestParam("title") String title) {

        // page query
        PageDto page = PageDto.of(start / length + 1, length);
        List<XxlJobGroup> list = xxlJobGroupMapper.pageList(page, appname, title);
        int list_count = xxlJobGroupMapper.pageListCount(appname, title);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);        // 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping("/save")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<String> save(XxlJobGroup xxlJobGroup) {

        // valid
        if (!StringUtils.hasText(xxlJobGroup.getAppname())) {
            return ReturnT.ofFail((I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
            return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
            return ReturnT.ofFail("AppName" + I18nUtil.getString("system_unvalid"));
        }
        if (!StringUtils.hasText(xxlJobGroup.getTitle())) {
            return ReturnT.ofFail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
            return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
        }
        if (xxlJobGroup.getAddressType() != 0) {
            if (!StringUtils.hasText(xxlJobGroup.getAddressList())) {
                return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
                return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
            }

            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (!StringUtils.hasText(item)) {
                    return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupMapper.save(xxlJobGroup);
        return (ret > 0) ? ReturnT.ofSuccess() : ReturnT.ofFail();
    }

    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<String> update(XxlJobGroup xxlJobGroup) {
        // valid
        if (!StringUtils.hasText(xxlJobGroup.getAppname())) {
            return ReturnT.ofFail((I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
            return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (!StringUtils.hasText(xxlJobGroup.getTitle())) {
            return ReturnT.ofFail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (xxlJobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());
            String addressListStr = null;
            if (registryList!=null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = String.join(",", registryList);
            }
            xxlJobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (!StringUtils.hasText(xxlJobGroup.getAddressList())) {
                return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (!StringUtils.hasText(item)) {
                    return ReturnT.ofFail(I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupMapper.update(xxlJobGroup);
        return (ret > 0) ? ReturnT.ofSuccess() : ReturnT.ofFail();
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<>();
        long ts = System.currentTimeMillis();
        long deadTs = ts - TimeUnit.SECONDS.toMillis(RegistryConfig.DEAD_TIMEOUT);
        Date deadTime = new Date(deadTs);
        List<XxlJobRegistry> list = xxlJobRegistryMapper.findAll(deadTime);
        if (list!=null && !list.isEmpty()) {
            for (XxlJobRegistry item : list) {
                if (!RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
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

    @RequestMapping("/remove")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<String> remove(@RequestParam("id") int id) {

        // valid
        int count = xxlJobInfoMapper.pageListCount( id, -1, null, null, null);
        if (count > 0) {
            return ReturnT.ofFail(I18nUtil.getString("jobgroup_del_limit_0"));
        }

        List<XxlJobGroup> allList = xxlJobGroupMapper.findAll();
        if (allList.size() == 1) {
            return ReturnT.ofFail(I18nUtil.getString("jobgroup_del_limit_1"));
        }

        int ret = xxlJobGroupMapper.remove(id);
        return (ret > 0) ? ReturnT.ofSuccess() : ReturnT.ofFail();
    }

    @RequestMapping("/loadById")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public ReturnT<XxlJobGroup> loadById(@RequestParam("id") int id) {
        XxlJobGroup jobGroup = xxlJobGroupMapper.load(id);
        return jobGroup != null ? ReturnT.ofSuccess(jobGroup) : ReturnT.ofFail();
    }

}

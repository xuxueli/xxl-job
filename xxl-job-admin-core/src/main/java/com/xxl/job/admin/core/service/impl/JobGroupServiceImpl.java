package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.constant.RegistType;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.PageModel;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.*;

@Service
public class JobGroupServiceImpl implements JobGroupService {
    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;

    @Resource
    private XxlJobRegistryMapper xxlJobRegistryMapper;

    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;

    @Override
    public List<XxlJobGroup> findAll() {
        return xxlJobGroupMapper.findAll();
    }

    @Override
    public XxlJobGroup load(int id) {
        return xxlJobGroupMapper.load(id);
    }

    @Override
    public int save(XxlJobGroup jobGroup) {
        // valid
        if (StringTool.isBlank(jobGroup.getAppname())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + "AppName");
        }
        if (jobGroup.getAppname().length() < 4 || jobGroup.getAppname().length() > 64) {
            throw new XxlException(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (jobGroup.getAppname().contains(">") || jobGroup.getAppname().contains("<")) {
            throw new XxlException("AppName" + I18nUtil.getString("system_invalid"));
        }
        if (StringTool.isBlank(jobGroup.getTitle())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
        }
        if (jobGroup.getTitle().contains(">") || jobGroup.getTitle().contains("<")) {
            throw new XxlException(I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_invalid"));
        }
        if (jobGroup.getAddressType() != 0) {
            if (StringTool.isBlank(jobGroup.getAddressList())) {
                throw new XxlException(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            if (jobGroup.getAddressList().contains(">") || jobGroup.getAddressList().contains("<")) {
                throw new XxlException(I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_invalid"));
            }

            String[] addresss = jobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringTool.isBlank(item)) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid"));
                }
                if (!(HttpTool.isHttp(item) || HttpTool.isHttps(item))) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid") + "[2]");
                }
            }
        }

        // process
        jobGroup.setUpdateTime(new Date());

        return xxlJobGroupMapper.save(jobGroup);
    }

    @Override
    public int update(XxlJobGroup jobGroup) {
        // valid
        if (StringTool.isBlank(jobGroup.getAppname())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + "AppName");
        }
        if (jobGroup.getAppname().length() < 4 || jobGroup.getAppname().length() > 64) {
            throw new XxlException(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (StringTool.isBlank(jobGroup.getTitle())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
        }
        if (jobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(jobGroup.getAppname());
            String addressListStr = null;
            if (CollectionTool.isNotEmpty(registryList)) {
                Collections.sort(registryList);
                addressListStr = String.join(",", registryList);
            }
            jobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (StringTool.isBlank(jobGroup.getAddressList())) {
                throw new XxlException(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = jobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringTool.isBlank(item)) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid"));
                }
                if (!(HttpTool.isHttp(item) || HttpTool.isHttps(item))) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid") + "[2]");
                }
            }
        }

        // process
        jobGroup.setUpdateTime(new Date());

        return xxlJobGroupMapper.update(jobGroup);
    }

    @Override
    public int remove(List<Integer> ids) {
        // parse id
		if (CollectionTool.isEmpty(ids) || ids.size() != 1) {
			throw new XxlException(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
		}
		int id = ids.get(0);
        // parse id
        XxlJobGroup xxlJobGroup = xxlJobGroupMapper.load(id);
        if (xxlJobGroup == null) {
            return 1;
        }

        // whether exists job
        int count = xxlJobInfoMapper.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            throw new XxlException(I18nUtil.getString("jobgroup_del_limit_0"));
        }

        // whether only exists one group
        List<XxlJobGroup> allList = xxlJobGroupMapper.findAll();
        if (allList.size() == 1) {
            throw new XxlException(I18nUtil.getString("jobgroup_del_limit_1"));
        }

        // remove group
        int ret = xxlJobGroupMapper.remove(id);
        // remove registry-data
        removeByRegistryByKey(RegistType.EXECUTOR.name(), xxlJobGroup.getAppname());
        return ret;
    }

    @Override
    public PageModel<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title) {
        // page query
        List<XxlJobGroup> list = xxlJobGroupMapper.pageList(offset, pagesize, appname, title);
        int list_count = xxlJobGroupMapper.pageListCount(offset, pagesize, appname, title);

        // package result
        PageModel<XxlJobGroup> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(list_count);

        return pageModel;
    }

    @Override
    public List<String> findRegistryByAppName(String appNameParam) {
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
        return appAddressMap.get(appNameParam);
    }

    @Override
    public void removeByRegistryByKey(String registryGroup, String registryKey) {
        xxlJobRegistryMapper.removeByRegistryGroupAndKey(registryGroup, registryKey);
    }
}
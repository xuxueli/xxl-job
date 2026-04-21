package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.constant.RegistType;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.*;

@Service
public class JobGroupServiceImpl implements JobGroupService {

    private static final Logger logger = LoggerFactory.getLogger(JobGroupServiceImpl.class);

    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;
    @Resource
    private XxlJobRegistryMapper xxlJobRegistryMapper;

    @Override
    public List<XxlJobGroup> findAll() {
        return xxlJobGroupMapper.findAll();
    }

    @Override
    public XxlJobGroup load(int id) {
        return xxlJobGroupMapper.load(id);
    }

    @Override
    public int add(XxlJobGroup jobGroup, int userId) {
        // valid
        validateAppName(jobGroup.getAppname());
        validateTitle(jobGroup.getTitle());
        validateAddressList(jobGroup.getAddressList(), jobGroup.getAddressType());

        // process
        jobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupMapper.save(jobGroup);
        if (ret > 0) {
            logger.info("JobGroup added successfully. userId={}, appname={}, title={}",
                    userId, jobGroup.getAppname(), jobGroup.getTitle());
        }
        return ret;
    }

    @Override
    public boolean update(XxlJobGroup jobGroup, int userId) {
        // valid
        validateAppName(jobGroup.getAppname());
        validateTitle(jobGroup.getTitle());
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
            validateAddressList(jobGroup.getAddressList(), jobGroup.getAddressType());
        }

        // process
        jobGroup.setUpdateTime(new Date());

        boolean success = xxlJobGroupMapper.update(jobGroup) > 0;
        if (success) {
            logger.info("JobGroup updated successfully. userId={}, id={}, appname={}",
                    userId, jobGroup.getId(), jobGroup.getAppname());
        }
        return success;
    }

    @Override
    public boolean remove(int id, int userId) {
        // valid exists
        XxlJobGroup jobGroup = xxlJobGroupMapper.load(id);
        if (jobGroup == null) {
            logger.info("JobGroup already removed. userId={}, id={}", userId, id);
            return true;  // already removed
        }

        // whether exists job
        int count = xxlJobInfoMapper.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            throw new IllegalArgumentException("Cannot remove group: there are jobs associated with this group");
        }

        // whether only exists one group
        List<XxlJobGroup> allList = xxlJobGroupMapper.findAll();
        if (allList.size() == 1) {
            throw new IllegalArgumentException("Cannot remove: this is the last remaining group");
        }

        // remove group
        int ret = xxlJobGroupMapper.remove(id);
        // remove registry-data
        xxlJobRegistryMapper.removeByRegistryGroupAndKey(RegistType.EXECUTOR.name(), jobGroup.getAppname());

        if (ret > 0) {
            logger.info("JobGroup removed successfully. userId={}, id={}, appname={}",
                    userId, id, jobGroup.getAppname());
        }
        return ret > 0;
    }

    @Override
    public PageModel<XxlJobGroup> pageList(int offset, int pagesize, String searchName) {
        // page query
        List<XxlJobGroup> list = xxlJobGroupMapper.pageList(offset, pagesize, searchName, searchName);
        int list_count = xxlJobGroupMapper.pageListCount(offset, pagesize, searchName, searchName);

        // package result
        PageModel<XxlJobGroup> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(list_count);

        return pageModel;
    }

    /**
     * Validate appname: not blank, length 4-64, no invalid characters
     */
    private void validateAppName(String appname) {
        if (StringTool.isBlank(appname)) {
            throw new IllegalArgumentException("AppName cannot be blank");
        }
        if (appname.length() < 4 || appname.length() > 64) {
            throw new IllegalArgumentException("AppName length must be between 4 and 64 characters");
        }
        if (appname.contains(">") || appname.contains("<")) {
            throw new IllegalArgumentException("AppName contains invalid characters");
        }
    }

    /**
     * Validate title: not blank, no invalid characters
     */
    private void validateTitle(String title) {
        if (StringTool.isBlank(title)) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (title.contains(">") || title.contains("<")) {
            throw new IllegalArgumentException("Title contains invalid characters");
        }
    }

    /**
     * Validate addressList when addressType != 0: not blank, no invalid characters, valid HTTP addresses
     */
    private void validateAddressList(String addressList, int addressType) {
        if (addressType != 0) {
            if (StringTool.isBlank(addressList)) {
                throw new IllegalArgumentException("AddressList cannot be blank when addressType is not 0");
            }
            if (addressList.contains(">") || addressList.contains("<")) {
                throw new IllegalArgumentException("AddressList contains invalid characters");
            }

            String[] addresses = addressList.split(",");
            for (String item : addresses) {
                if (StringTool.isBlank(item)) {
                    throw new IllegalArgumentException("AddressList contains invalid addresses");
                }
                if (!(HttpTool.isHttp(item) || HttpTool.isHttps(item))) {
                    throw new IllegalArgumentException("AddressList contains invalid addresses");
                }
            }
        }
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        List<String> result = new ArrayList<>();
        List<XxlJobRegistry> list = xxlJobRegistryMapper.findAll(Const.DEAD_TIMEOUT, new Date());
        if (CollectionTool.isNotEmpty(list)) {
            for (XxlJobRegistry item : list) {
                if (!RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    continue;
                }
                if (!appnameParam.equals(item.getRegistryKey())) {
                    continue;
                }
                String address = item.getRegistryValue();
                if (!result.contains(address)) {
                    result.add(address);
                }
            }
        }
        return result;
    }

}
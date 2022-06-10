package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.thread.LockHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zheng
 */
@RestController
public class GroupNoAuthController {
    @Resource
    public XxlJobGroupDao xxlJobGroupDao;

    @PermissionLimit(limit = false)
    @PostMapping(value = "/group/noAuth/save")
    public ReturnT<String> save(@RequestBody XxlJobGroup xxlJobGroup) {
        if (!LockHelper.tryLockGroup()){
            return ReturnT.FAIL;
        }
        int ret = 0;
        try {
            int i = xxlJobGroupDao.pageListCount(0, 1, xxlJobGroup.getAppname(), "");
            if (i == 1) {
                XxlJobGroup jobGroup = xxlJobGroupDao.pageList(0, 1, xxlJobGroup.getAppname(), "").get(0);
                boolean contains = jobGroup.getAddressList().contains(xxlJobGroup.getAddressList());
                if (!contains) {
                    jobGroup.setAddressList(jobGroup.getAddressList().concat(",").concat(xxlJobGroup.getAddressList()));
                    ret = xxlJobGroupDao.update(jobGroup);
                    return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
                }
                return ReturnT.SUCCESS;
            }
            // valid
            if (xxlJobGroup.getAppname() == null || xxlJobGroup.getAppname().trim().length() == 0) {
                return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + "AppName"));
            }
            if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
                return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length"));
            }
            if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
                return new ReturnT<String>(500, "AppName" + I18nUtil.getString("system_unvalid"));
            }
            if (xxlJobGroup.getTitle() == null || xxlJobGroup.getTitle().trim().length() == 0) {
                return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
            }
            if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
                return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
            }
            if (xxlJobGroup.getAddressType() != 0) {
                if (xxlJobGroup.getAddressList() == null || xxlJobGroup.getAddressList().trim().length() == 0) {
                    return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
                }
                if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
                    return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
                }

                String[] addresss = xxlJobGroup.getAddressList().split(",");
                for (String item : addresss) {
                    if (item == null || item.trim().length() == 0) {
                        return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                    }
                }
            }

            // process
            xxlJobGroup.setUpdateTime(new Date());
            ret = xxlJobGroupDao.save(xxlJobGroup);
            return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
        } finally {
            LockHelper.unLockGroup();
        }
    }

    @PermissionLimit(limit = false)
    @PostMapping(value = "/group/noAuth/query")
    public XxlJobGroup pageList(String appname) {
        // page query
        List<XxlJobGroup> list = xxlJobGroupDao.pageList(0, 1, appname, "");
        return list.get(0);
    }
}
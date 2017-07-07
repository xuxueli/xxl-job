package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.thread.JobRegistryMonitorHelper;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * job group controller
 *
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobGroupController {

    private final IXxlJobInfoDao xxlJobInfoDao;
    private final IXxlJobGroupDao xxlJobGroupDao;
    private final JobRegistryMonitorHelper jobRegistryMonitorHelper;

    @RequestMapping
    public String index(Model model) {

        // job group (executor)
        List<XxlJobGroup> list = xxlJobGroupDao.findAll();

        if (CollectionUtils.isNotEmpty(list)) {
            for (XxlJobGroup group : list) {
                List<String> registryList = null;
                if (group.getAddressType() == 0) {
                    registryList = jobRegistryMonitorHelper.discover(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppName());
                } else {
                    if (StringUtils.isNotBlank(group.getAddressList())) {
                        registryList = Arrays.asList(group.getAddressList().split(","));
                    }
                }
                group.setRegistryList(registryList);
            }
        }

        model.addAttribute("list", list);
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(XxlJobGroup xxlJobGroup) {
        ReturnT<String> checkRet = _checkRequest(xxlJobGroup);
        if (checkRet.getCode() == ReturnT.SUCCESS_CODE) {
            int ret = xxlJobGroupDao.save(xxlJobGroup);
            return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
        } else {
            return checkRet;
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(XxlJobGroup xxlJobGroup) {
        // valid
        ReturnT<String> checkRet = _checkRequest(xxlJobGroup);
        if (checkRet.getCode() == ReturnT.SUCCESS_CODE) {
            int ret = xxlJobGroupDao.update(xxlJobGroup);
            return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
        } else {
            return checkRet;
        }
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {

        // valid
        int count = xxlJobInfoDao.pageListCount(0, 10, id, null);
        if (count > 0) {
            return ReturnT.error("该分组使用中, 不可删除");
        }

        List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
        if (allList.size() == 1) {
            return ReturnT.error("删除失败, 系统需要至少预留一个默认分组");
        }

        int ret = xxlJobGroupDao.remove(id);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    private ReturnT<String> _checkRequest(XxlJobGroup xxlJobGroup) {
        // valid
        if (xxlJobGroup.getAppName() == null || StringUtils.isBlank(xxlJobGroup.getAppName())) {
            return ReturnT.error("请输入AppName");
        }
        if (xxlJobGroup.getAppName().length() > 64) {
            return ReturnT.error("AppName长度限制为4~64");
        }
        if (xxlJobGroup.getTitle() == null || StringUtils.isBlank(xxlJobGroup.getTitle())) {
            return ReturnT.error("请输入名称");
        }
        if (xxlJobGroup.getAddressType() != 0) {
            if (StringUtils.isBlank(xxlJobGroup.getAddressList())) {
                return ReturnT.error("手动录入注册方式，机器地址不可为空");
            }
            String[] address = xxlJobGroup.getAddressList().split(",");
            for (String item : address) {
                if (StringUtils.isBlank(item)) {
                    return ReturnT.error("机器地址非法");
                }
            }
        }
        return new ReturnT<>("验证正确");
    }

}

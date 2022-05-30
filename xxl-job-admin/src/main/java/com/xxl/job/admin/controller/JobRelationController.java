package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Controller
@RequestMapping
public class JobRelationController {
    public static final Logger LOGGER = LoggerFactory.getLogger(JobRelationController.class);

    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @RequestMapping(value = "/jobrelation")
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobInfoId) {
        List<XxlJobInfo> allInfo = xxlJobInfoDao.findAll();
        List<XxlJobInfo> JobInfoList = filterJobInfoByRole(request, allInfo);
        if (JobInfoList==null || JobInfoList.size()==0) {
            throw new XxlJobException(I18nUtil.getString("jobinfo_empty"));
        }

        model.addAttribute("JobInfoList", JobInfoList);
        model.addAttribute("jobInfoId", jobInfoId);

        return "jobrelation/jobrelation.index";
    }

    public static List<XxlJobInfo> filterJobInfoByRole(HttpServletRequest request, List<XxlJobInfo> jobInfoList_all){
        List<XxlJobInfo> jobInfoList = new ArrayList<>();
            XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
            if (loginUser.getRole() == 1) {
                jobInfoList = jobInfoList_all;
            } else {
                List<String> infoIdStrs = new ArrayList<>();
                if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
                    infoIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
                }
                for (XxlJobInfo infoItem : jobInfoList_all) {
                    if (infoIdStrs.contains(String.valueOf(infoItem.getId()))) {
                        jobInfoList.add(infoItem);
                    }
                }
            }
        return jobInfoList;
    }

    /**
     * 新增job顺序关系，实质是更新现有job的 child_jobid字段
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/jobrelation/update")
    public ReturnT<String> addRelation(@RequestParam Map<String, String> map){
        List<ReturnT<String>> result = new ArrayList<>();
        map.forEach((parentKey, childKey)->{
            ReturnT<String> update = xxlJobService.updateRelation(parentKey, childKey);
            result.add(update);
            LOGGER.info("更新任务依赖顺序{}，current job id = {}， child job id = {}", update, parentKey, childKey);
        });
        return result.stream().allMatch(ReturnT.SUCCESS :: equals) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @PermissionLimit(limit = false)
    @ResponseBody
    @RequestMapping(value = "/jobrelation/findAllRelation")
    public Map<String, Object> findAllRelation(@RequestParam(required = false, defaultValue = "-1") int jobInfoId) {
        return xxlJobService.findAllRelation(jobInfoId);
    }

}

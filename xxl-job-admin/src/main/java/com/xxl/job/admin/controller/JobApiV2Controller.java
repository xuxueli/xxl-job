package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api/v2")
public class JobApiV2Controller {
    @Resource
    private JobGroupController jobGroupController;

    @Resource
    private JobInfoController jobInfoController;

    @Resource
    private XxlJobService xxlJobService;

    /**
     * api
     *
     * @param uri
     * @param data
     * @return
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<? extends Object> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<Object>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<Object>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length() > 0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<Object>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        // services mapping
        if ("jobGroupPageList".equals(uri)) {
            JobGroupParam jobGroupParam = GsonTool.fromJson(data, JobGroupParam.class);
            Map<String, Object> map = jobGroupController.
                    pageList(null, jobGroupParam.getStart(), jobGroupParam.getLength(), jobGroupParam.getAppname(), jobGroupParam.title);
            return new ReturnT<Object>(map);
        } else if ("jobGroupSave".equals(uri)) {
            XxlJobGroup xxlJobGroup = GsonTool.fromJson(data, XxlJobGroup.class);
            return jobGroupController.save(xxlJobGroup);
        } else if ("jobGroupUpdate".equals(uri)) {
            XxlJobGroup xxlJobGroup = GsonTool.fromJson(data, XxlJobGroup.class);
            return jobGroupController.update(xxlJobGroup);
        } else if ("jobGroupRemove".equals(uri)) {
            XxlJobGroup xxlJobGroup = GsonTool.fromJson(data, XxlJobGroup.class);
            return jobGroupController.remove(xxlJobGroup.getId());
        } else if ("jobGroupLoadById".equals(uri)) {
            XxlJobGroup xxlJobGroup = GsonTool.fromJson(data, XxlJobGroup.class);
            return jobGroupController.loadById(xxlJobGroup.getId());
        } else if ("jobInfoPageList".equals(uri)) {
            JobInfoParam jobInfoParam = GsonTool.fromJson(data, JobInfoParam.class);
            Map<String, Object> map = jobInfoController.pageList(jobInfoParam.getStart(), jobInfoParam.getLength(), jobInfoParam.getJobGroup(), jobInfoParam.getTriggerStatus(), jobInfoParam.getJobDesc(), jobInfoParam.getExecutorHandler(), jobInfoParam.getAuthor());
            return new ReturnT<Object>(map);
        } else if ("jobInfoAdd".equals(uri)) {
            XxlJobInfo xxlJobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
            return xxlJobService.add(xxlJobInfo);
        } else if ("jobInfoUpdate".equals(uri)) {
            XxlJobInfo xxlJobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
            return xxlJobService.update(xxlJobInfo);
        } else if ("jobInfoRemove".equals(uri)) {
            XxlJobInfo xxlJobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
            return jobInfoController.remove(xxlJobInfo.getId());
        } else if ("jobInfoStart".equals(uri)) {
            XxlJobInfo xxlJobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
            return jobInfoController.start(xxlJobInfo.getId());
        } else if ("jobInfoPause".equals(uri)) {
            XxlJobInfo xxlJobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
            return jobInfoController.pause(xxlJobInfo.getId());
        } else if ("jobInfoTrigger".equals(uri)) {
            JobInfoTrigger jobInfoTrigger = GsonTool.fromJson(data, JobInfoTrigger.class);
            return xxlJobService.trigger(jobInfoTrigger.getId(), jobInfoTrigger.getExecutorParam(), jobInfoTrigger.getAddressList());
        } else {
            return new ReturnT<Object>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }

    }

    public static class JobInfoTrigger {

        private int id;

        private String executorParam;

        private String addressList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getExecutorParam() {
            return executorParam;
        }

        public void setExecutorParam(String executorParam) {
            this.executorParam = executorParam;
        }

        public String getAddressList() {
            return addressList;
        }

        public void setAddressList(String addressList) {
            this.addressList = addressList;
        }
    }

    public static class JobInfoParam {

        private int start;

        private int length;

        private int jobGroup;

        private int triggerStatus;

        private String jobDesc;

        private String executorHandler;

        private String author;

        public int getJobGroup() {
            return jobGroup;
        }

        public void setJobGroup(int jobGroup) {
            this.jobGroup = jobGroup;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getTriggerStatus() {
            return triggerStatus;
        }

        public void setTriggerStatus(int triggerStatus) {
            this.triggerStatus = triggerStatus;
        }

        public String getJobDesc() {
            return jobDesc;
        }

        public void setJobDesc(String jobDesc) {
            this.jobDesc = jobDesc;
        }

        public String getExecutorHandler() {
            return executorHandler;
        }

        public void setExecutorHandler(String executorHandler) {
            this.executorHandler = executorHandler;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    public static class JobGroupParam {

        private Integer start;

        private Integer length;

        private String appname;

        private String title;

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}

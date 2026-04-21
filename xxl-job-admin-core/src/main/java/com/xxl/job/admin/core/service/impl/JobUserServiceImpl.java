package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.mapper.XxlJobUserMapper;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.service.JobUserService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Sha256Tool;
import com.xxl.tool.response.PageModel;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JobUser service implementation for xxl-job core module.
 * Refactored from JobUserController to remove web-layer dependencies.
 *
 * @author xuxueli 2019-05-04
 */
@Service
public class JobUserServiceImpl implements JobUserService {
    private static final Logger logger = LoggerFactory.getLogger(JobUserServiceImpl.class);

    @Resource
    private XxlJobUserMapper xxlJobUserMapper;

    @Override
    public XxlJobUser loadByUserName(String userName) {
        return xxlJobUserMapper.loadByUserName(userName);
    }

    @Override
    public int add(XxlJobUser jobUser, int userId) {
        // valid username
        if (StringTool.isBlank(jobUser.getUsername())) {
            return 0;
        }
        jobUser.setUsername(jobUser.getUsername().trim());
        if (!(jobUser.getUsername().length() >= 4 && jobUser.getUsername().length() <= 20)) {
            return 0;
        }

        // valid password
        if (StringTool.isBlank(jobUser.getPassword())) {
            return 0;
        }
        jobUser.setPassword(jobUser.getPassword().trim());
        if (!(jobUser.getPassword().length() >= 4 && jobUser.getPassword().length() <= 20)) {
            return 0;
        }

        // hash password
        String passwordHash = Sha256Tool.sha256(jobUser.getPassword());
        jobUser.setPassword(passwordHash);

        // check if username already exists
        XxlJobUser existUser = xxlJobUserMapper.loadByUserName(jobUser.getUsername());
        if (existUser != null) {
            return 0;
        }

        // save user
        int ret = xxlJobUserMapper.save(jobUser);
        if (ret < 1) {
            return 0;
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "user-add", jobUser.getUsername());

        return jobUser.getId();
    }

    @Override
    public boolean update(XxlJobUser jobUser, int userId) {
        // valid password
        if (StringTool.isNotBlank(jobUser.getPassword())) {
            jobUser.setPassword(jobUser.getPassword().trim());
            if (!(jobUser.getPassword().length() >= 4 && jobUser.getPassword().length() <= 20)) {
                return false;
            }
            // hash password
            String passwordHash = Sha256Tool.sha256(jobUser.getPassword());
            jobUser.setPassword(passwordHash);
        } else {
            jobUser.setPassword(null);
        }

        // update user
        int ret = xxlJobUserMapper.update(jobUser);
        if (ret < 1) {
            return false;
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "user-update", jobUser.getId());

        return true;
    }

    @Override
    public boolean remove(int id, int userId) {
        int ret = xxlJobUserMapper.delete(id);
        if (ret < 1) {
            return false;
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "user-remove", id);

        return true;
    }

    @Override
    public PageModel<XxlJobUser> pageList(int offset, int pagesize, String searchName) {
        // page list (use -1 for role to indicate all roles)
        List<XxlJobUser> list = xxlJobUserMapper.pageList(offset, pagesize, searchName, -1);
        int listCount = xxlJobUserMapper.pageListCount(offset, pagesize, searchName, -1);

        // filter password from results
        if (list != null && !list.isEmpty()) {
            for (XxlJobUser item : list) {
                item.setPassword(null);
            }
        }

        // package result
        PageModel<XxlJobUser> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(listCount);

        return pageModel;
    }

    @Override
    public boolean updatePassword(int userId, String oldPassword, String password) {
        // load user
        XxlJobUser existUser = xxlJobUserMapper.loadById(userId);
        if (existUser == null) {
            return false;
        }

        // verify old password
        String oldPasswordHash = Sha256Tool.sha256(oldPassword);
        if (!oldPasswordHash.equals(existUser.getPassword())) {
            return false;
        }

        // hash new password
        String passwordHash = Sha256Tool.sha256(password);
        existUser.setPassword(passwordHash);

        // update user
        int ret = xxlJobUserMapper.update(existUser);
        if (ret < 1) {
            return false;
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "user-update-password", userId);

        return true;
    }
}
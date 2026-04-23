package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.mapper.XxlJobUserMapper;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.service.JobUserService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Sha256Tool;
import com.xxl.tool.response.PageModel;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JobUser service implementation for xxl-job admin web module.
 *
 * @author xuxueli 2019-05-04
 */
@Service
public class JobUserServiceImpl implements JobUserService {
    private static final Logger logger = LoggerFactory.getLogger(JobUserServiceImpl.class);

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MAX_PASSWORD_LENGTH = 20;

    @Resource
    private XxlJobUserMapper xxlJobUserMapper;

    @Override
    public XxlJobUser loadByUserName(String userName) {
        return xxlJobUserMapper.loadByUserName(userName);
    }

    @Override
    public int add(XxlJobUser jobUser, int userId) {
        // valid username
        String username = jobUser.getUsername();
        if (!validateUsername(username)) {
            return 0;
        }
        username = username.trim();
        if (!validateUsernameLength(username)) {
            return 0;
        }

        // valid password
        String password = jobUser.getPassword();
        if (!validatePassword(password)) {
            return 0;
        }
        password = password.trim();
        if (!validatePasswordLength(password)) {
            return 0;
        }

        // hash password
        String passwordHash = Sha256Tool.sha256(password);

        // check if username already exists
        XxlJobUser existUser = xxlJobUserMapper.loadByUserName(username);
        if (existUser != null) {
            return 0;
        }

        // set processed values
        jobUser.setUsername(username);
        jobUser.setPassword(passwordHash);

        // save user
        int ret = xxlJobUserMapper.save(jobUser);
        if (ret < 1) {
            return 0;
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "user-add", username);

        return jobUser.getId();
    }

    @Override
    public boolean update(XxlJobUser jobUser, int userId) {
        // valid password
        String password = jobUser.getPassword();
        if (StringTool.isNotBlank(password)) {
            password = password.trim();
            if (!validatePasswordLength(password)) {
                return false;
            }
            // hash password
            String passwordHash = Sha256Tool.sha256(password);
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
    public PageModel<XxlJobUser> pageList(int offset, int pagesize, String searchName, int role) {
        // page list
        List<XxlJobUser> list = xxlJobUserMapper.pageList(offset, pagesize, searchName, role);
        int listCount = xxlJobUserMapper.pageListCount(offset, pagesize, searchName, role);

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

    private boolean validateUsername(String username) {
        return StringTool.isNotBlank(username);
    }

    private boolean validateUsernameLength(String username) {
        int length = username.length();
        return length >= MIN_USERNAME_LENGTH && length <= MAX_USERNAME_LENGTH;
    }

    private boolean validatePassword(String password) {
        return StringTool.isNotBlank(password);
    }

    private boolean validatePasswordLength(String password) {
        int length = password.length();
        return length >= MIN_PASSWORD_LENGTH && length <= MAX_PASSWORD_LENGTH;
    }
}
package com.xxl.job.admin.service;

import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.tool.response.PageModel;

public interface JobUserService {

    /**
     * Load user by username
     */
    XxlJobUser loadByUserName(String userName);

    /**
     * Add new user
     * @return new user id, 0 if failed
     */
    int add(XxlJobUser jobUser, int userId);

    /**
     * Update user
     * @return true if success
     */
    boolean update(XxlJobUser jobUser, int userId);

    /**
     * Remove user
     * @return true if success
     */
    boolean remove(int id, int userId);

    /**
     * Page list query
     */
    PageModel<XxlJobUser> pageList(int offset, int pagesize, String searchName, int role);

    /**
     * Update password for a user
     * @return true if success
     */
    boolean updatePassword(int userId, String oldPassword, String password);
}
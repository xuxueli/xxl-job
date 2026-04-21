package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.tool.response.PageModel;

import java.util.List;

public interface JobGroupService {

    /**
     * Find all job groups
     */
    List<XxlJobGroup> findAll();

    /**
     * Load by id
     */
    XxlJobGroup load(int id);

    /**
     * Add job group
     * @return new group id, 0 if failed
     */
    int add(XxlJobGroup jobGroup, int userId);

    /**
     * Update job group
     * @return true if success
     */
    boolean update(XxlJobGroup jobGroup, int userId);

    /**
     * Remove job group
     * @return true if success
     */
    boolean remove(int id, int userId);

    /**
     * Page list query
     */
    PageModel<XxlJobGroup> pageList(int offset, int pagesize, String searchName);
}
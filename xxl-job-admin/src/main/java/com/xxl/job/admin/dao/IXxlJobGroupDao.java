package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobGroup;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface IXxlJobGroupDao {

    public List<XxlJobGroup> findAll();

    public int save(XxlJobGroup xxlJobGroup);

    public int update(XxlJobGroup xxlJobGroup);

    public int remove(int id);

    public XxlJobGroup load(int id);
}

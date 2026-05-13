package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.tool.response.PageModel;

public interface JobUserService extends IService<XxlJobUser> {

    PageModel<XxlJobUser> pageList(int offset, int pagesize, String searchName, int role);

    XxlJobUser loadByUserName(String username);

    XxlJobUser loadById(int id);

    boolean updateToken(int id, String token);

    boolean updateUser(XxlJobUser xxlJobUser);
}
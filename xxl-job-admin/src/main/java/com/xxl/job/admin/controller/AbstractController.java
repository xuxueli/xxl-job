package com.xxl.job.admin.controller;

import com.xxl.job.admin.common.utils.AuthUtils;

/**
 * 基本控制器
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
public class AbstractController {

    protected String getAccount(){
        return AuthUtils.getCurrentUser();
    }



}

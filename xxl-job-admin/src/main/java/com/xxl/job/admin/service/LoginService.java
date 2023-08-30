package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.LoginDTO;
import com.xxl.job.admin.common.pojo.dto.RegisterDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 登录服务
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param loginDTO 登录DTO
     * @param session session对象
     */
    void login(LoginDTO loginDTO, HttpSession session);

    /**
     * 注册
     *
     * @param registerDTO 注册DTO
     */
    void register(RegisterDTO registerDTO);

    /**
     * 注销
     *
     * @param request 请求
     */
    void logout(HttpServletRequest request);


}

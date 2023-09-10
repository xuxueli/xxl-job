package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.common.pojo.entity.LoginToken;

/**
 * 登录令牌 接口
 * @author Rong.Jia
 * @date 2023/09/09
 */
public interface LoginTokenService extends IService<LoginToken> {

    /**
     * 保存登录令牌
     *
     * @param account 账号
     * @param token token
     * @param duration 有效时长,单位：秒
     */
    void saveLoginToken(String account, String token, Integer duration);

    /**
     * 查询登录令牌根据token
     *
     * @param token token
     * @return {@link LoginToken}
     */
    LoginToken findLoginTokenByToken(String token);

    /**
     * 更新登录令牌
     * @param token token
     */
    void updateLoginTokenByToken(String token);

    /**
     * 删除登录令牌
     * @param token token
     */
    void deleteLoginTokenByToken(String token);







}

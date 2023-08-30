package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.PwdDTO;
import com.xxl.job.admin.common.pojo.dto.UserInfoDTO;
import com.xxl.job.admin.common.pojo.entity.UserInfo;
import com.xxl.job.admin.common.pojo.vo.UserInfoVO;
import com.xxl.job.admin.service.base.BaseService;

import java.util.List;

/**
 * 用户信息服务
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
public interface UserInfoService extends BaseService<UserInfo, UserInfo, UserInfoVO> {

    /**
     * 保存用户
     *
     * @param userInfoDTO 用户DTO
     */
    void saveUserInfo(UserInfoDTO userInfoDTO);

    /**
     * 修改用户
     *
     * @param userInfoDTO 用户DTO
     */
    void updateUserInfo(UserInfoDTO userInfoDTO);

    /**
     * 删除用户
     *
     * @param ids id
     */
    void deleteUserInfo(List<Long> ids);

    /**
     *  验证密码
     * @param pwdDTO 密码参数
     */
    void verifyPwd(PwdDTO pwdDTO);

    /**
     *  修改密码
     * @param pwdDTO 密码参数
     */
    void modifyPwd(PwdDTO pwdDTO);

    /**
     *  重置用户密码
     * @param account 用户账号
     * @return String 重置后密码
     */
    String resetPwd(String account);

    /**
     *  禁用/启用账号
     * @param account 账号
     */
    void disableUserInfo(String account);

    /**
     * 查询用户信息通过账户
     *
     * @param account 账户
     * @return {@link UserInfoVO}
     */
    UserInfoVO findUserInfoByAccount(String account);























}

package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.entity.UserInfo;
import com.xxl.job.admin.common.pojo.query.UserInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息映射器
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 查询用户信息通过账户
     *
     * @param account 账户
     * @return {@link UserInfo}
     */
    UserInfo findUserInfoByAccount(@Param("account") String account);

    /**
     * 查询用户信息
     *
     * @param query 查询
     * @return {@link List}<{@link UserInfo}>
     */
    List<UserInfo> findUserInfo(UserInfoQuery query);

    /**
     * 更新密码通过id
     *
     * @param id       id
     * @param password 密码
     */
    void updatePasswordById(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新状态通过id
     *
     * @param id       id
     * @param status 账号状态, (0->已过期，1->启用，-1->禁用 )
     */
    void updateStatusById(@Param("id") Long id, @Param("status") Integer status);










}
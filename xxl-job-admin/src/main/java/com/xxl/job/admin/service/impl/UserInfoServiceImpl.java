package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.xxl.job.admin.common.constants.AuthConstant;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.constants.RegularVerifyConstant;
import com.xxl.job.admin.common.exceptions.XxlJobAdminException;
import com.xxl.job.admin.common.pojo.dto.PageDTO;
import com.xxl.job.admin.common.pojo.dto.PwdDTO;
import com.xxl.job.admin.common.pojo.dto.UserInfoDTO;
import com.xxl.job.admin.common.pojo.entity.UserInfo;
import com.xxl.job.admin.common.pojo.query.UserInfoQuery;
import com.xxl.job.admin.common.pojo.vo.UserInfoVO;
import com.xxl.job.admin.common.utils.AuthUtils;
import com.xxl.job.admin.mapper.UserInfoMapper;
import com.xxl.job.admin.service.UserInfoService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息服务实现类
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfoMapper, UserInfo, UserInfo, UserInfoVO> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean delete(Serializable id) {

        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        UserInfo userInfo = this.getById(id);
        Assert.notNull(userInfo, ResponseEnum.THE_USER_DOES_NOT_EXIST.getMessage());

        // 判断是否管理员
        if (AuthConstant.ADMINISTRATOR.equals(userInfo.getAccount())) {
            log.error("deleteUserInfoById()  System administrator cannot delete");
            throw new XxlJobAdminException(ResponseEnum.SYSTEM_ADMINISTRATOR_CANNOT_DELETE);
        }

        String currentUser = AuthUtils.getCurrentUser();

        if (ObjectUtil.equals(Convert.toLong(id), userInfoMapper.findUserInfoByAccount(currentUser).getId())) {
            log.error("deleteUserInfoById() The current logged-in user cannot be deleted");
            throw new XxlJobAdminException(ResponseEnum.THE_CURRENT_LOGIN_USER_CANNOT_BE_DELETED);
        }

        return super.delete(id);
    }

    @Override
    public List<UserInfo> queryList(PageDTO pageDTO) {
        UserInfoQuery query = new UserInfoQuery();
        BeanUtil.copyProperties(pageDTO, query);
        return userInfoMapper.findUserInfo(query);
    }

    @Override
    public UserInfoVO queryById(Serializable id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        return this.objectConversion(this.getById(id));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveUserInfo(UserInfoDTO userInfoDTO) {
        Assert.isTrue(ReUtil.isMatch(RegularVerifyConstant.PWD_REG, userInfoDTO.getPassword()),
                ResponseEnum.THE_PASSWORD_FORMAT_IS_INCORRECT.getMessage());

        UserInfo userInfo = userInfoMapper.findUserInfoByAccount(userInfoDTO.getAccount());
        Assert.isNull(userInfo, ResponseEnum.THE_USER_ALREADY_EXISTS.getMessage());
        userInfo = new UserInfo();

        CopyOptions copyOptions = CopyOptions.create();
        copyOptions.setIgnoreNullValue(Boolean.TRUE);
        copyOptions.setIgnoreError(Boolean.TRUE);

        BeanUtil.copyProperties(userInfoDTO, userInfo, copyOptions);
        userInfo.setCreatedTime(DateUtil.date());
        userInfo.setPassword(DigestUtil.bcrypt(userInfoDTO.getPassword()));
        userInfo.setStatus(NumberConstant.ONE);
        this.saveOrUpdate(userInfo);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateUserInfo(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = this.getById(userInfoDTO.getId());
        Assert.notNull(userInfo, ResponseEnum.THE_USER_DOES_NOT_EXIST.getMessage());

        if (!StrUtil.equals(userInfo.getAccount(), userInfoDTO.getAccount())) {
            Assert.isNull(userInfoMapper.findUserInfoByAccount(userInfoDTO.getAccount()),
                    ResponseEnum.THE_USER_ALREADY_EXISTS.getMessage());
        }

        BeanUtil.copyProperties(userInfoDTO, userInfo);
        userInfo.setUpdatedTime(DateUtil.date());
        this.saveOrUpdate(userInfo);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteUserInfo(List<Long> ids) {
        Assert.notEmpty(ids, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        ids.forEach(this::delete);
    }

    @Override
    public void verifyPwd(PwdDTO pwdDTO) {

        Assert.isTrue(ReUtil.isMatch(RegularVerifyConstant.PWD_REG, pwdDTO.getNewPwd()),
                ResponseEnum.THE_PASSWORD_FORMAT_IS_INCORRECT.getMessage());

        UserInfo userInfo = userInfoMapper.findUserInfoByAccount(pwdDTO.getAccount());
        Assert.notNull(userInfo, ResponseEnum.THE_USER_DOES_NOT_EXIST.getMessage());

        // 校验原密码
        Assert.isTrue(DigestUtil.bcryptCheck(pwdDTO.getOldPwd(), userInfo.getPassword()),
                ResponseEnum.THE_OLD_PASSWORD_IS_INCORRECT.getMessage());

        // 校验新密码与旧密码是否相同
        Assert.isFalse(DigestUtil.bcryptCheck(pwdDTO.getNewPwd(), userInfo.getPassword()),
                ResponseEnum.THE_NEW_PASSWORD_IS_THE_SAME_AS_THE_OLD_PASSWORD.getMessage());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void modifyPwd(PwdDTO pwdDTO) {
        verifyPwd(pwdDTO);
        UserInfo userInfo = userInfoMapper.findUserInfoByAccount(pwdDTO.getAccount());
        userInfoMapper.updatePasswordById(userInfo.getId(), DigestUtil.bcrypt(pwdDTO.getNewPwd()));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disableUserInfo(String account) {
        Assert.notBlank(account, ResponseEnum.THE_ACCOUNT_CANNOT_BE_EMPTY.getMessage());

        UserInfo userInfo = userInfoMapper.findUserInfoByAccount(account);
        Assert.notNull(userInfo, ResponseEnum.THE_USER_DOES_NOT_EXIST.getMessage());

        // 判断是否管理员
        Assert.isFalse(StrUtil.equals(AuthConstant.ADMINISTRATOR, account), ResponseEnum.SYSTEM_ADMINISTRATOR_CANNOT_DISABLE.getMessage());

        // 判断禁用用户是否是当前登录用户
        Assert.isFalse(StrUtil.equals(AuthUtils.getCurrentUser(), userInfo.getAccount()), ResponseEnum.CURRENT_USER_CANNOT_DISABLE.getMessage());

        if (Validator.equal(NumberConstant.A_NEGATIVE, userInfo.getStatus())) {
            userInfoMapper.updateStatusById(userInfo.getId(), NumberConstant.ONE);
        } else if (Validator.equal(NumberConstant.ONE, userInfo.getStatus())) {
            userInfoMapper.updateStatusById(userInfo.getId(), NumberConstant.A_NEGATIVE);
        } else {
            log.error("disableUserInfo() Invalid specified state");
            throw new XxlJobAdminException(ResponseEnum.INVALID_SPECIFIED_STATE);
        }
    }

    @Override
    public UserInfoVO findUserInfoByAccount(String account) {
        Assert.notBlank(account, ResponseEnum.THE_ACCOUNT_CANNOT_BE_EMPTY.getMessage());
        UserInfo userInfo = userInfoMapper.findUserInfoByAccount(account);
        return this.objectConversion(userInfo);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String resetPwd(String account) {
        Assert.notBlank(account, ResponseEnum.THE_ACCOUNT_CANNOT_BE_EMPTY.getMessage());
        UserInfo userInfo = userInfoMapper.findUserInfoByAccount(account);

        Assert.notNull(userInfo, ResponseEnum.THE_USER_DOES_NOT_EXIST.getMessage());
        userInfoMapper.updatePasswordById(userInfo.getId(), DigestUtil.bcrypt(AuthConstant.DEFAULT_PASSWORD));
        return AuthConstant.DEFAULT_PASSWORD;
    }













}

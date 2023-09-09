package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.jwt.JWTUtil;
import com.xxl.job.admin.common.constants.AuthConstant;
import com.xxl.job.admin.common.pojo.dto.LoginDTO;
import com.xxl.job.admin.common.pojo.dto.RegisterDTO;
import com.xxl.job.admin.common.pojo.dto.UserInfoDTO;
import com.xxl.job.admin.common.pojo.vo.UserInfoVO;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.LoginTokenService;
import com.xxl.job.admin.service.UserInfoService;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 登录服务实现类
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LoginTokenService loginTokenService;

    @Override
    public void login(LoginDTO loginDTO, HttpServletResponse response) {
        Assert.notBlank(loginDTO.getAccount(), ResponseEnum.THE_ACCOUNT_CANNOT_BE_EMPTY.getMessage());
        UserInfoVO userInfoVO = userInfoService.findUserInfoByAccount(loginDTO.getAccount());
        Assert.notNull(userInfoVO, ResponseEnum.THE_ACCOUNT_DOES_NOT_EXIST_PLEASE_CHANGE_THE_ACCOUNT_TO_LOGIN.getMessage());

        Assert.isTrue(DigestUtil.bcryptCheck(loginDTO.getPassword(), userInfoVO.getPassword()),
                ResponseEnum.THE_ACCOUNT_OR_PASSWORD_IS_INCORRECT.getMessage());

        Map<String, Object> payload = MapUtil.newHashMap();
        payload.put("account", userInfoVO.getAccount());
        payload.put("id", userInfoVO.getId());

        String token = JWTUtil.createToken(payload, Base64.encode(IdUtil.fastUUID()).getBytes(StandardCharsets.UTF_8));

        loginTokenService.saveLoginToken(userInfoVO.getAccount(), token, 3600);
        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Headers", AuthConstant.AUTHORIZATION_HEADER);
        response.addHeader(AuthConstant.AUTHORIZATION_HEADER, token);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void register(RegisterDTO registerDTO) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtil.copyProperties(registerDTO, userInfoDTO);
        userInfoDTO.setCreatedUser(registerDTO.getAccount());
        userInfoService.saveUserInfo(userInfoDTO);
    }

    @Override
    public void logout(HttpServletRequest request) {
        request.getSession().removeAttribute("uid");
        request.getSession().removeAttribute("account");
    }
}

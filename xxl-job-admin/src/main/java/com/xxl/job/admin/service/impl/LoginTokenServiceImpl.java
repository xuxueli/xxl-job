package com.xxl.job.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.common.pojo.entity.LoginToken;
import com.xxl.job.admin.mapper.LoginTokenMapper;
import com.xxl.job.admin.service.LoginTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登录令牌 接口实现类
 *
 * @author Rong.Jia
 * @date 2023/09/09
 */
@Slf4j
@Service
public class LoginTokenServiceImpl extends ServiceImpl<LoginTokenMapper, LoginToken> implements LoginTokenService {

    @Autowired
    private LoginTokenMapper loginTokenMapper;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveLoginToken(String account, String token, Integer duration) {
        LoginToken tokenByToken = loginTokenMapper.findLoginTokenByToken(token);
        if (ObjectUtil.isNull(tokenByToken)) tokenByToken = new LoginToken();
        tokenByToken.setToken(token);
        tokenByToken.setAccount(account);
        tokenByToken.setEffectiveDuration(duration);
        tokenByToken.setLoginTime(DateUtil.current());
        tokenByToken.setUpdatedTime(DateUtil.current());
        this.save(tokenByToken);
    }

    @Override
    public LoginToken findLoginTokenByToken(String token) {
        return loginTokenMapper.findLoginTokenByToken(token);
    }

    @Override
    public void updateLoginTokenByToken(String token) {
        try {
            LoginToken loginToken = loginTokenMapper.findLoginTokenByToken(token);
            if (ObjectUtil.isNotNull(loginToken)) {
                loginTokenMapper.updateLoginTokenByToken(token, DateUtil.current());
            }
        }catch (Exception e) {
            log.error("【{}】更新时间异常, 跳过", token, e);
        }
    }
}

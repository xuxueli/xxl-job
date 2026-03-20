package com.xxl.job.writing.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * 默认用户验证服务
 * 在没有提供具体实现时使用，始终返回用户有效
 * 生产环境应提供具体的用户验证实现
 */
@Slf4j
@Service
@ConditionalOnMissingBean(UserValidationService.class)
public class DefaultUserValidationService implements UserValidationService {

    @Override
    public boolean isValidUser(Long userId, Integer userType) {
        log.debug("Default user validation - assuming user is valid: userId={}, userType={}", userId, userType);
        return true;
    }

    @Override
    public boolean isUserLocked(Long userId) {
        log.debug("Default user validation - assuming user is not locked: userId={}", userId);
        return false;
    }

    @Override
    public Integer getUserStatus(Long userId) {
        log.debug("Default user validation - returning null status for userId={}", userId);
        return null;
    }
}
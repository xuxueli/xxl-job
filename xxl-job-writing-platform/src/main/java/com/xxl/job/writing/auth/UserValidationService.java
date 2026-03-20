package com.xxl.job.writing.auth;

/**
 * 用户验证服务接口
 * 用于在JWT令牌验证后检查用户状态和权限
 */
public interface UserValidationService {

    /**
     * 验证用户状态是否有效
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 如果用户有效返回true，否则返回false
     */
    boolean isValidUser(Long userId, Integer userType);

    /**
     * 检查用户是否被锁定或禁用
     * @param userId 用户ID
     * @return 如果用户被锁定返回true，否则返回false
     */
    boolean isUserLocked(Long userId);

    /**
     * 获取用户状态
     * @param userId 用户ID
     * @return 用户状态代码
     */
    Integer getUserStatus(Long userId);
}
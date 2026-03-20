package com.xxl.job.writing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类（基于Redis）
 */
@Slf4j
@Component
public class DistributedLock {
    private final RedisTemplate<String, String> redisTemplate;

    public DistributedLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey    锁的key
     * @param requestId  请求标识（用于释放锁时验证）
     * @param expireTime 锁的过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(
                    lockKey,
                    requestId,
                    expireTime,
                    TimeUnit.SECONDS
            );
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("获取分布式锁失败，lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁的key
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            // 使用Lua脚本确保原子性操作
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else " +
                    "return 0 " +
                    "end";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);

            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("释放分布式锁失败，lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 获取任务接单锁的key
     *
     * @param taskId 任务ID
     * @return 锁的key
     */
    public static String getTaskAcceptLockKey(Long taskId) {
        return "writing:task:accept:lock:" + taskId;
    }

    /**
     * 获取任务支付锁的key
     *
     * @param taskId 任务ID
     * @return 锁的key
     */
    public static String getTaskPayLockKey(Long taskId) {
        return "writing:task:pay:lock:" + taskId;
    }

    /**
     * 获取用户并发操作锁的key
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @return 锁的key
     */
    public static String getUserActionLockKey(Long userId, String action) {
        return "writing:user:action:lock:" + userId + ":" + action;
    }
}
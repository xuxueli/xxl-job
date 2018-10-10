package com.xxl.job.admin.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisCacheTemplate implements CacheTemplate {

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public void set(Object key, Object value) {
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public <T> T get(Object key, Class<T> cls) {
        return (T)redisTemplate.opsForValue().get(key);
    }
}

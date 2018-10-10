package com.xxl.job.admin.service.impl;

public interface CacheTemplate {

    void set(Object key, Object value);

    <T> T get(Object key,Class<T> cls);
}

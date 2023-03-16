package com.xxl.job.core.converter;

/**
 * @Author xmz
 * @Description
 * @Date 2023/03/16 22:00
 **/
public interface Converter<T> {
    T convertTo(String s);
}

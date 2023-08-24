package com.xxl.job.core.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.enums.IUrlEnum;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * url工具类
 *
 * @author Rong.Jia
 * @date 2023/05/14
 */
public class UrlUtils {

    public static final String HTTP_PROTOCOL = "http://";

    /**
     * 获取url
     *
     * @param urlEnum url枚举
     * @return {@link List}<{@link String}>
     */
    public static List<String> getUrl(List<String> addresses, IUrlEnum urlEnum) {
        if (CollectionUtil.isEmpty(addresses)) return Collections.emptyList();
        return addresses.stream().map(a -> getUrl(a, urlEnum)).collect(Collectors.toList());
    }

    /**
     * 获取url
     *
     * @param address 地址
     * @param urlEnum url枚举
     * @return {@link String}
     */
    public static String getUrl(String address, IUrlEnum urlEnum) {
        if (StrUtil.isBlank(address)) return StrUtil.EMPTY;
        return getUrl(address, urlEnum.getValue());
    }

    /**
     * 获取url
     *
     * @param addresses 地址
     * @param path      路径
     * @return {@link List}<{@link String}>
     */
    public static List<String> getUrl(List<String> addresses, String path) {
        if (CollectionUtil.isEmpty(addresses)) return Collections.emptyList();
        return addresses.stream().map(a -> getUrl(a, path)).collect(Collectors.toList());
    }

    /**
     * 获取url
     *
     * @param address 地址
     * @param path    路径
     * @return {@link String}
     */
    public static String getUrl(String address, String path) {
        if (StrUtil.isBlank(address)) return StrUtil.EMPTY;
        String url = address;
        if (StrUtil.endWith(address, StrUtil.SLASH)) {
            url = StrUtil.removeSuffix(url, StrUtil.SLASH);
        }
        return StrUtil.startWith(url, HTTP_PROTOCOL)
                ? url + path : HTTP_PROTOCOL + url + path;
    }



}

package com.xxl.job.admin.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 版本工具类
 *
 * @author Rong.Jia
 * @date 2023/08/28
 */
public class VersionUtils {

    /**
     * 自动升级版本号，版本号+1
     *
     * @param version 版本
     * @return {@link String}
     */
    public static String autoUpgradeVersion(String version) {
      return autoUpgradeVersion(version, "1.0");
    }

    /**
     * 自动升级版本号，版本号+1
     *
     * @param version 版本
     * @return {@link String}
     */
    public static String autoUpgradeVersion(String version, String defaultVersion) {
        if (StrUtil.isBlank(version)) return defaultVersion;
        //将版本号拆解成整数数组
        String[] arr = version.split("\\.");
        int[] ints = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ints[i] = Integer.parseInt(arr[i]);
        }

        //递归调用
        autoUpgradeVersion(ints, ints.length - 1);

        //数组转字符串
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ints.length; i++) {
            sb.append(ints[i]);
            if ((i + 1) != ints.length) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * 自动升级版本号，版本号+1
     *
     * @param ints  整数
     * @param index 指数
     */
    private static void autoUpgradeVersion(int[] ints, int index) {
        if (index == 0) {
            ints[0] = ints[0] + 1;
        } else {
            int value = ints[index] + 1;
            if (value < 10) {
                ints[index] = value;
            } else {
                ints[index] = 0;
                autoUpgradeVersion(ints, index - 1);
            }
        }
    }

    /**
     * 获取最大版本号
     *
     * @param versionNumbers 版本号集合
     * @return {@link String} 最大版本号
     */
    public static String maxVersion(List<String> versionNumbers) {
        return maxVersion(versionNumbers, "1.0");
    }

    /**
     * 获取最大版本号
     *
     * @param versionNumbers 版本号集合
     * @param defaultVersion 默认版本
     * @return {@link String} 最大版本号
     */
    public static String maxVersion(List<String> versionNumbers, String defaultVersion) {

        if (CollectionUtil.isEmpty(versionNumbers)) {
            return defaultVersion;
        }

        return versionNumbers.stream().filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(defaultVersion);
    }


}

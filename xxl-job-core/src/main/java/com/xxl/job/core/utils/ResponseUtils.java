package com.xxl.job.core.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.pojo.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应工具类
 *
 * @author Rong.Jia
 * @date 2021/08/03 17:36:07
 */
@Slf4j
public class ResponseUtils {

    /**
     * 获取响应
     *
     * @param response 响应
     * @return {@link T}
     */
    public static <T> T getResponse(ResponseVO<T> response) {
        if (Validator.equal(0, response.getCode())) {
            return response.getData();
        }
        return null;
    }

    /**
     * 获取响应
     *
     * @param response 响应
     * @param clazz    clazz 类型
     * @return {@link T}
     */
    public static <T> T getResponse(ResponseVO<T> response, Class<T> clazz) {
        if (Validator.equal(0, response.getCode())) {
            T data = response.getData();
            if (ObjectUtil.isNotNull(data)) {
                return JSON.parseObject(JSON.toJSONString(data), clazz);
            }
            return data;
        }

        return null;
    }

    /**
     * 获取响应
     *
     * @param response      响应
     * @param typeReference 引用类型
     * @return {@link T}
     */
    public static <T> T getResponse(ResponseVO<T> response, TypeReference<T> typeReference) {
        if (Validator.equal(0, response.getCode())) {
            T data = response.getData();
            if (ObjectUtil.isNotNull(data)) {
                return JSON.parseObject(JSON.toJSONString(data), typeReference);
            }
            return data;
        }

        return null;
    }

    /**
     * 响应成功
     *
     * @param response 响应
     * @return {@link Boolean}
     */
    public static Boolean isSuccess(ResponseVO<?> response) {
       return Validator.equal(ResponseEnum.SUCCESS.getCode(), response.getCode());
    }


















}

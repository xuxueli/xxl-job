package com.xxl.job.core.converter;

import com.xxl.job.core.util.GsonTool;

/**
 * @Author xmz
 * @Description
 * @Date 2023/03/16 22:01
 **/
public class JsonConverter<T> implements Converter<T> {
    private Class<T> toType;

    public JsonConverter() {
    }

    public JsonConverter(Class<T> toType) {
        this.toType = toType;
    }

    public Class<T> getToType() {
        return this.toType;
    }

    public void setToType(Class<T> toType) {
        this.toType = toType;
    }

    public T convertTo(String value) {
        return GsonTool.fromJson(value, this.toType);
    }
}

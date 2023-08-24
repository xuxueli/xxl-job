package com.xxl.job.admin.common.config.escape;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.StringCodec;

import java.lang.reflect.Type;

/**
 * String类型的内容统一做trim操作
 *
 * @author Rong.Jia
 * @date 2023/05/19
 */
public class StringTrimDeserializer implements ObjectDeserializer {

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        // JSON String反序列化的逻辑比较复杂，在StringCodec的基础上，对其结果调用trim方法
        Object obj = StringCodec.instance.deserialze(parser, type, fieldName);
        if (obj instanceof String) {
            String str = (String) obj;
            return (T) str.trim();
        }else {
            return (T)stringTrim(obj);
        }
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

    private static Object stringTrim(Object obj) {

        if (obj instanceof JSONArray) {
            JSONArray jsonArray = new JSONArray();
            for (Object o1 : ((JSONArray) obj)) {
                jsonArray.add(stringTrim(o1));
            }
            return jsonArray;
        } else if (obj instanceof JSONObject) {
            JSONObject jsonObject = new JSONObject();
            ((JSONObject)obj).forEach((key, value) -> jsonObject.put(key, stringTrim(value)));
            return jsonObject;
        } else {
            if (obj instanceof String) {
                obj = obj.toString().trim();
            }
        }
        return obj;
    }

}

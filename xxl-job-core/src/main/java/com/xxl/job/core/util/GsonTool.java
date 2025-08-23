package com.xxl.job.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * gson tool （From https://github.com/xuxueli/xxl-tool ）
 *
 * @author xuxueli 2020-04-11 20:56:31
 */
public class GsonTool {

    private static Gson gson = null;
    static {
        gson= new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping().create();
    }

    /**
     * Object 转成 json
     *
     * <pre>
     *     String json = GsonTool.toJson(new Demo());
     * </pre>
     *
     * @param src
     * @return String
     */
    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    /**
     * json 转成 特定的cls的Object
     *
     * <pre>
     *     Demo demo = GsonTool.fromJson(json, Demo.class);
     * </pre>
     *
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * json 转成 特定的 rawClass<classOfT> 的Object
     *
     * <pre>
     *     Response<Demo> response = GsonTool.fromJson(json, Response.class, Demo.class);
     * </pre>
     *
     * @param json
     * @param classOfT
     * @param argClassOfT
     * @return
     */
    /*public static <T> T fromJson(String json, Class<T> classOfT, Class argClassOfT) {
        Type type = new ParameterizedType4ReturnT(classOfT, new Class[]{argClassOfT});
        return gson.fromJson(json, type);
    }
    public static class ParameterizedType4ReturnT implements ParameterizedType {
        private final Class raw;
        private final Type[] args;
        public ParameterizedType4ReturnT(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }
        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }
        @Override
        public Type getRawType() {
            return raw;
        }
        @Override
        public Type getOwnerType() {return null;}
    }*/

    /**
     * json 转成 特定的 Type 的Object
     *
     * @param json
     * @param typeOfT
     * @return
     * @param <T>
     */
    public static <T> T  fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    /**
     * json 转成 特定的 Type 的Object
     *
     * <pre>
     *     Response<Demo> response = GsonTool.fromJson(json, Response.class, Demo.class);
     * </pre>
     *
     * @param json
     * @param rawType
     * @param typeArguments
     * @return
     */
    public static <T> T  fromJson(String json, Type rawType, Type... typeArguments) {
        Type type = TypeToken.getParameterized(rawType, typeArguments).getType();
        return gson.fromJson(json, type);
    }

    /**
     * json 转成 特定的cls的 ArrayList
     *
     * <pre>
     *     List<Demo> demoList = GsonTool.fromJsonList(json, Demo.class);
     * </pre>
     *
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> ArrayList<T> fromJsonList(String json, Class<T> classOfT) {
        Type type = TypeToken.getParameterized(ArrayList.class, classOfT).getType();
        return gson.fromJson(json, type);
    }

    /**
     * json 转成 特定的cls的 HashMap
     *
     * <pre>
     *     HashMap<String, Demo> map = GsonTool.fromJsonMap(json, String.class, Demo.class);
     * </pre>
     *
     * @param json
     * @param keyClass
     * @param valueClass
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> HashMap<K, V> fromJsonMap(String json, Class<K> keyClass, Class<V> valueClass) {
        Type type = TypeToken.getParameterized(HashMap.class, keyClass, valueClass).getType();
        return gson.fromJson(json, type);
    }

    // ---------------------------------

    /**
     * Object 转成 JsonElement
     *
     * @param src
     * @return
     */
    public static JsonElement toJsonElement(Object src) {
        return gson.toJsonTree(src);
    }

    /**
     * JsonElement 转成 特定的cls的Object
     *
     * @param json
     * @param classOfT
     * @return
     * @param <T>
     */
    public static <T> T fromJsonElement(JsonElement json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * JsonElement 转成 特定的 rawClass<classOfT> 的Object
     *
     * @param json
     * @param typeOfT
     * @return
     * @param <T>
     */
    public static <T> T fromJsonElement(JsonElement json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    /**
     * JsonElement 转成 特定的 Type 的 Object
     *
     * @param json
     * @param rawType
     * @param typeArguments
     * @return
     * @param <T>
     */
    public static <T> T fromJsonElement(JsonElement json, Type rawType, Type... typeArguments) {
        Type typeOfT = TypeToken.getParameterized(rawType, typeArguments).getType();
        return gson.fromJson(json, typeOfT);
    }

}

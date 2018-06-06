package com.xxl.job.core.annotationtask.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class ReflectUtil {

    public static void objectToMap(Map<String,Object> tempParams,Object params){
        for(Class<?> clazz = params.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f :fields) {
                    f.setAccessible(true);
                    if(f.get(params)==null){
                        continue;
                    }
                    tempParams.put(f.getName(),f.get(params));
                    f.setAccessible(false);
                }
            } catch (Exception e) {
            }
        }
    }


    public static boolean isMapOrCollection(Class clzz){
        return Map.class.isAssignableFrom(clzz)|| Collection.class.isAssignableFrom(clzz);
    }



    public static boolean isString(Class clzz){
        return String.class.isAssignableFrom(clzz);
    }


    public static boolean isWrapClass(Class clzz){
        try {
            return ((Class) clzz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPrimitive(Class clzz){
        return int.class.isAssignableFrom(clzz)||
                double.class.isAssignableFrom(clzz)||
                boolean.class.isAssignableFrom(clzz)||
                long.class.isAssignableFrom(clzz);
    }

    public static boolean isDate(Class clzz){
        return Date.class.isAssignableFrom(clzz);
    }

    public static boolean isObjValue(Class clzz){
        return !isString(clzz)||!isPrimitive(clzz)||!isWrapClass(clzz)||!isMapOrCollection(clzz)||!isDate(clzz);
    }

}

package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author xuxueli 2020-04-12 0:14:00
 */
public class JdkSerializeTool {
    private static Logger logger = LoggerFactory.getLogger(JdkSerializeTool.class);


    // ------------------------ serialize and unserialize ------------------------

    /**
     * 将对象-->byte[] (由于jedis中不支持直接存储object所以转换成byte[]存入)
     *
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }


    /**
     * 将byte[] -->Object
     *
     * @param bytes
     * @return
     */
    public static  <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        ObjectInputStream ois = null;
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                ois.close();
                bais.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }


}

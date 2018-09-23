package com.xxl.job.core.util;

/**
 * Function:custom expire time ,unit minutes
 *
 * @author crossoverJie
 *         Date: 15/03/2018 15:24
 * @since JDK 1.8
 */
public class ExpireTimeHolder {

    private static int EXPIRE = 3 ;

    public static void setExpire(int time){
        EXPIRE = time;
    }

    public static int getExpire(){
        return EXPIRE ;
    }
}

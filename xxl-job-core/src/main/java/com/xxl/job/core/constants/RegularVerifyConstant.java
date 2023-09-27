package com.xxl.job.core.constants;

/**
 *
 *  正则校验常数
 * @author Rong.Jia
 * @date 2019/02/27 10:13:22
 */
public class RegularVerifyConstant {

    // 密码匹配正则 长度8~16  （大小写字母、数字、特殊符号 四选三）
    //public static final String PWD_REG = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,16}$";

    /**
     * 密码匹配正则  长度8~16  （大小写字母、数字、特殊符号 四选二）
     */
    public static final String PWD_REG = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)[a-zA-Z0-9\\W_]{8,16}$";

    /**
     * 电话正则匹配
     */
    public static final String MOBILE_REG = "^0?(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$";

    /**
     * 邮箱正则
     */
    public static final String EMAIL_REG = "^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";





}

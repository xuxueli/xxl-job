package com.xxl.job.admin.common.exceptions;

import com.xxl.job.core.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 项目自定义异常
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@SuppressWarnings("all")
@EqualsAndHashCode(callSuper = true)
@Data
public class XxlJobAdminException extends RuntimeException  implements Serializable {

    private static final long serialVersionUID = -1501020198729282518L;

    /**
     *  异常code 码
     */
    private Integer code;

    /**
     * 异常详细信息
     */
    private String message;

    public XxlJobAdminException(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public XxlJobAdminException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public XxlJobAdminException(ResponseEnum responseEnum, String message) {
        super(message);
        this.code = responseEnum.getCode();
        this.message = message;
    }

    public XxlJobAdminException(Integer code, String message, Throwable t) {
        super(message, t);
        this.code = code;
        this.message = message;
    }

    public XxlJobAdminException(ResponseEnum responseEnum, Throwable t) {
        super(responseEnum.getMessage(), t);
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }
    public static void throwException(String msg) {
        throw new XxlJobAdminException(ResponseEnum.ERROR, msg);
    }

}

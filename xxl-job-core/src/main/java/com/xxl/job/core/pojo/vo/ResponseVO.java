package com.xxl.job.core.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xxl.job.core.enums.ResponseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据格式返回统一
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@SuppressWarnings("ALL")
@Data
@ApiModel("返回对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 3681838956784534606L;

    /**
     * 异常码
     */
    @ApiModelProperty("异常码")
    private Integer code;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String message;

    /**
     * 数据
     */
    @ApiModelProperty("数据")
    private T data;

    public ResponseVO() {}

    public ResponseVO(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public ResponseVO(Integer code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public ResponseVO(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public ResponseVO(ResponseEnum responseEnum, T data) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
        this.data = data;
    }

    public static <T> ResponseVO<T> success(){
        return new ResponseVO<>(ResponseEnum.SUCCESS);
    }

    public static <T> ResponseVO<T> success(T data){
        return new ResponseVO<>(ResponseEnum.SUCCESS, data);
    }

    public static <T> ResponseVO<T> success(String message){
        return new ResponseVO<>(ResponseEnum.SUCCESS.getCode(), message);
    }

    public static <T> ResponseVO<T> error(T data){
        return new ResponseVO<>(ResponseEnum.ERROR, data);
    }

    public static <T> ResponseVO<T> error(String message){
        return new ResponseVO<>(ResponseEnum.ERROR.getCode(), message);
    }

    public static <T> ResponseVO<T> error(){
        return new ResponseVO<>(ResponseEnum.ERROR);
    }

    public static <T> ResponseVO<T> success(int code, String msg){
        return new ResponseVO<>(code, msg);
    }

    public static <T> ResponseVO<T> error(int code, String msg){
        return new ResponseVO<>(code, msg);
    }

    public static <T> ResponseVO<T> error(ResponseEnum responseEnum){
        return new ResponseVO<>(responseEnum);
    }

    public static ResponseVO<?> error(ResponseEnum responseEnum, Object data){
        return new ResponseVO<>(responseEnum, data);
    }


}

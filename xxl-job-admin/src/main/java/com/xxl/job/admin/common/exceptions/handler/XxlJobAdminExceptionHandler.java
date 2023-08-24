package com.xxl.job.admin.common.exceptions.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.xxl.job.admin.common.exceptions.XxlJobAdminException;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.pojo.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedList;
import java.util.List;

/**
 * 异常控制处理器
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@SuppressWarnings("ALL")
@RestControllerAdvice
public class XxlJobAdminExceptionHandler {

    /**
     * 捕获自定义异常，并返回异常数据
     */
    @ExceptionHandler(value = XxlJobAdminException.class)
    public ResponseVO xxlJobAdminExceptionHandler(XxlJobAdminException e) {
        log.error("xxlJobAdminExceptionHandler  {}", e.getMessage());
        return ResponseVO.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获缺失参数异常，并返回异常数据
     *
     * @param e e 异常
     * @return {@link ResponseVO}
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseVO missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("missingServletRequestParameterException {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.LACK_OF_PARAMETER);
    }


    /**
     * 捕获不合法的参数异常，并返回异常数据
     *
     * @param e e
     * @return {@link ResponseVO}
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseVO illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("illegalArgumentExceptionHandler  {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.PARAMETER_ERROR.getCode(), e.getMessage());
    }

    /**
     * 捕捉404异常
     *
     * @param e 404 异常
     * @return {@link ResponseVO}
     * @date 2019/04/17 09:46:22
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseVO noHandlerFoundHandle(NoHandlerFoundException e) {
        log.error("noHandlerFoundHandle {}", ExceptionUtil.stacktraceToString(e));
        return new ResponseVO(ResponseEnum.NOT_FOUND);
    }

    /**
     * 处理
     * 字段验证异常处理
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVO handle(MethodArgumentNotValidException e) {
        List<String> errorList = new LinkedList<>();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        for (ObjectError error : errors) {
            log.error("MethodArgumentNotValidException : {} - {}", error.getObjectName(), error.getDefaultMessage());
            errorList.add(error.getDefaultMessage());
        }
        log.error("MethodArgumentNotValidExceptionHandle :{}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.PARAMETER_ERROR.getCode(), errorList.get(0));
    }

    /**
     * 字段验证异常处理
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseVO handle(BindException e) {
        List<String> errorList = new LinkedList<>();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        for (ObjectError error : errors) {
            log.error("BindException : {} - {}", error.getObjectName(), error.getDefaultMessage());
            errorList.add(error.getDefaultMessage());
        }
        log.error("BindException :{}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.PARAMETER_ERROR.getCode(), errorList.get(0));
    }

    /**
     * 参数类型不正确异常
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseVO methodArgumentTypeMismatchExceptionException(Exception e) {
        log.error("methodArgumentTypeMismatchExceptionException : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.THE_PARAMETER_TYPE_IS_INCORRECT);
    }

    /**
     * 不支持当前请求方法
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseVO handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.REQUEST_MODE_ERROR);
    }

    /**
     * 不支持当前媒体类型
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseVO handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("HttpMediaTypeNotSupportedException : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.MEDIA_TYPE_NOT_SUPPORTED);
    }

    /**
     * 默认异常处理
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO defaultErrorHandler(Exception e) {

        if (e.getCause() instanceof XxlJobAdminException) {
            xxlJobAdminExceptionHandler((XxlJobAdminException) e);
        }

        if (e.getCause() instanceof IllegalArgumentException) {
            illegalArgumentExceptionHandler((IllegalArgumentException) e.getCause());
        }

        if (e.getCause() instanceof MethodArgumentTypeMismatchException) {
            methodArgumentTypeMismatchExceptionException((MethodArgumentTypeMismatchException) e.getCause());
        }

        if (e.getCause() instanceof MissingServletRequestParameterException) {
            missingServletRequestParameterExceptionHandler((MissingServletRequestParameterException) e.getCause());
        }
        log.error("defaultErrorHandler : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 默认运行异常处理
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ResponseVO runtimeErrorHandler(RuntimeException e) {

        if (e.getCause() instanceof XxlJobAdminException) {
            xxlJobAdminExceptionHandler((XxlJobAdminException) e);
        }

        if (e.getCause() instanceof HttpMessageNotReadableException) {
            httpMessageNotReadableHandler((HttpMessageNotReadableException) e.getCause());
        }

        if (e.getCause() instanceof MultipartException) {
            multipartExceptionHandler((MultipartException) e.getCause());
        }

        if (e.getCause() instanceof IllegalArgumentException) {
            illegalArgumentExceptionHandler((IllegalArgumentException) e.getCause());
        }

        if (e.getCause() instanceof MethodArgumentTypeMismatchException) {
            methodArgumentTypeMismatchExceptionException((MethodArgumentTypeMismatchException) e.getCause());
        }
        log.error("runtimeErrorHandler : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 文件上传异常处理
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseVO maxUploadHandler(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.FILE_LIMIT_EXCEEDED);
    }

    /**
     * 使用反射或者代理造成的异常需要根据异常类型单独处理
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = UndeclaredThrowableException.class)
    @ResponseBody
    public ResponseVO undeclaredThrowableException(HttpServletRequest req, UndeclaredThrowableException e) {

        //密文解密失败异常
        if (e.getCause() instanceof BadPaddingException) {

            log.error("BadPaddingException : {}", ExceptionUtil.stacktraceToString(e));
            return ResponseVO.error(ResponseEnum.SYSTEM_ERROR);
        }

        log.error("UndeclaredThrowableException : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.SYSTEM_ERROR);
    }

    /**
     * http 请求参数格式错误
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseVO httpMessageNotReadableHandler(HttpMessageNotReadableException e) {
        log.error("httpMessageNotReadableHandler : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.REQUEST_PARAMETER_FORMAT_IS_INCORRECT);
    }

    /**
     * 超出最大限制捕获异常
     *
     * @param e 异常信息
     * @return ResponseVO 返回异常信息
     */
    @ExceptionHandler(value = MultipartException.class)
    @ResponseBody
    public ResponseVO multipartExceptionHandler(MultipartException e) {
        log.error("multipartExceptionHandler : {}", ExceptionUtil.stacktraceToString(e));
        return ResponseVO.error(ResponseEnum.FILE_LIMIT_EXCEEDED);

    }




}

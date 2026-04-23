package com.xxl.job.admin.web.exception;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.tool.response.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Global exception handler for xxl-job web module
 *
 * @author xuxueli 2026-04-22
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(XxlException.class)
    @ResponseBody
    public Response<String> handleXxlException(XxlException e) {
        return Response.ofFail(e.getMessage());
    }

}
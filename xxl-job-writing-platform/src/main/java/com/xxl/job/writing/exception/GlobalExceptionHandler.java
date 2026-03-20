package com.xxl.job.writing.exception;

import com.xxl.job.writing.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERIC_OPERATION_FAILED_MESSAGE = "Operation failed, please try again later";

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);

        // 对于认证失败，返回统一的错误消息，避免信息泄露
        String responseMessage = e.getMessage();
        if (e.getCode() == BusinessException.UNAUTHORIZED) {
            responseMessage = "Authentication failed";
        }

        return ResponseEntity.status(resolveHttpStatus(e.getCode()))
                .body(Result.error(e.getCode(), responseMessage));
    }

    /**
     * 处理参数校验异常（MethodArgumentNotValidException）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {}", errorMessage);
        return ResponseEntity.badRequest().body(Result.error(400, "Request validation failed"));
    }

    /**
     * 处理参数校验异常（BindException）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常: {}", errorMessage);
        return ResponseEntity.badRequest().body(Result.error(400, "Request binding failed"));
    }

    /**
     * 处理参数校验异常（ConstraintViolationException）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("参数约束异常: {}", errorMessage);
        return ResponseEntity.badRequest().body(Result.error(400, "Request constraint validation failed"));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<?>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, GENERIC_OPERATION_FAILED_MESSAGE));
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, GENERIC_OPERATION_FAILED_MESSAGE));
    }

    private HttpStatus resolveHttpStatus(int code) {
        if (code == BusinessException.UNAUTHORIZED) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (code == BusinessException.NOT_IMPLEMENTED) {
            return HttpStatus.NOT_IMPLEMENTED;
        }
        if (code >= 400 && code < 500) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

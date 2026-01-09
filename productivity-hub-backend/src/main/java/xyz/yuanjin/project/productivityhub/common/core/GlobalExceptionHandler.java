package xyz.yuanjin.project.productivityhub.common.core;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 10:10</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理自定义业务异常
    @ExceptionHandler(ApiException.class)
    public R<Object> handleApiException(ApiException e, HttpServletRequest request) {
        log.error("请求地址: {}, 系统业务异常: {}", request.getRequestURI(), e.getMessage());
        return R.failed(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("请求地址: {}, 请求参数格式错误: {}", request.getRequestURI(), e.getMessage());
        return R.validateFailed("无法读取或反序列化请求体");
    }

    // 处理 Spring Validation 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Object> handleValidException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return R.validateFailed(message);
    }

    // 处理其他未知异常
    @ExceptionHandler(Exception.class)
    public R<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("请求地址: {}, 系统未知异常: {}", request.getRequestURI(), e.getMessage(), e);
        return R.failed(ResultCode.ERROR);
    }
}

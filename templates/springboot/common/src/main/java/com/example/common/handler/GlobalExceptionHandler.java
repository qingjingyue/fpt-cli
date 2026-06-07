package com.example.common.handler;

import com.example.common.constants.MessageConstant;
import com.example.common.exceptions.BizException;
import com.example.common.exceptions.DbException;
import com.example.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，处理项目中抛出的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 校验失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("请求参数校验异常: {}", msg);
        return Result.error(msg);
    }


    /**
     * 处理 @Validated 校验失败异常 (spring对@Valid的加强)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .collect(Collectors.joining(", "));

        log.error("请求参数异常: {}", msg);
        return Result.error(msg);
    }


    /**
     * 捕获业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handlerBizException(BizException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 捕获数据库异常
     */
    @ExceptionHandler(DbException.class)
    public Result<Void> handlerDbException(DbException e) {
        log.error("数据库异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 捕获数据库唯一索引异常
     * 例如：用户名重复
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> exceptionHandler(DuplicateKeyException e) {
        // Duplicate entry 'username' for key 'user.username'
        // username重复
        log.error("数据库唯一索引异常: ", e);
        String message = e.getMessage();
        if (message.contains("Duplicate entry ")) {
            return Result.error("该用户名已存在");
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /// 捕获http解析异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> exceptionHandler(HttpMessageNotReadableException e) {
        // JSON parse error: Cannot construct instance of `com.example.common.enums.AuthType`, problem: 不支持的认证方式
        log.error("http解析异常: ", e);
        String message = e.getMessage();
        if (message.contains("problem")) {
            String problem = message.split("problem: ")[1];
            return Result.error(problem);
        }
        return Result.error("请求参数错误");
    }

    /// 捕获请求路径不存在异常
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> exceptionHandler(NoResourceFoundException e) {
        log.error("请求路径不存在: ", e);
        return Result.error("请求路径不存在");
    }


    /**
     * 保底异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> exceptionHandler(Exception e) {
        log.error("未知异常: ", e);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}

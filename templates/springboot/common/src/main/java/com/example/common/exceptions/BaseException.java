package com.example.common.exceptions;

/**
 * 项目基础异常
 */
public abstract class BaseException extends RuntimeException {


    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}

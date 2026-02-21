package com.example.common.exceptions;

import com.example.common.constants.MessageConstant;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 数据库异常
 */
public class DbException extends BaseException {
    public DbException(String message) {
        super(message);
    }

    public static DbException of(SQLIntegrityConstraintViolationException e) {
        // Duplicate entry 'username' for key 'employee.idx_username'
        // username重复
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            String username = message.split("'")[1];
            return new DbException("用户名：" + username + "已存在");
        }
        return new DbException(MessageConstant.UNKNOWN_ERROR);
    }
}

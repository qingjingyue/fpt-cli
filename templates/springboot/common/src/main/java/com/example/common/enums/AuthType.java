package com.example.common.enums;

import com.example.common.exceptions.BizException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/// 登录认证类型
public enum AuthType {

    /// 账号
    ACCOUNT("account"),
    /// 手机号
    PHONE("phone"),
    /// 邮箱
    EMAIL("email");

    private final String value;

    AuthType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AuthType fromString(String value) {
        try {
            return AuthType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BizException("不支持的认证方式");
        }
    }
}

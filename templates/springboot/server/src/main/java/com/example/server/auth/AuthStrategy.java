package com.example.server.auth;

import com.example.common.enums.AuthType;
import com.example.domain.dto.LoginDTO;
import com.example.domain.po.User;


/**
 * 登录认证策略接口
 */
public interface AuthStrategy {

    /// 获取认证类型
    AuthType getType();

    /// 进行身份验证
    User authenticate(LoginDTO loginDTO);

    /// 获取验证码
    void getVerifyCode(LoginDTO loginDTO);
}
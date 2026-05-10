package com.example.server.auth;

import com.example.common.enums.AuthType;
import com.example.common.exceptions.BizException;
import com.example.domain.dto.LoginDTO;
import com.example.domain.po.User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthManager {
    private final Map<AuthType, AuthStrategy> strategyMap;

    public AuthManager(List<AuthStrategy> strategies) {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(AuthStrategy::getType, s -> s));
    }

    /// 认证
    public User doAuth(LoginDTO loginDTO) {
        AuthStrategy strategy = strategyMap.get(loginDTO.getAuthType());
        if (strategy == null) {
            throw new BizException("不支持的认证方式");
        }
        return strategy.authenticate(loginDTO);
    }

    /// 获取验证码
    public void getVerifyCode(LoginDTO loginDTO) {
        AuthStrategy strategy = strategyMap.get(loginDTO.getAuthType());
        if (strategy == null) {
            throw new BizException("不支持的认证方式");
        }
        strategy.getVerifyCode(loginDTO);
    }

    /// 获取登录方式
    public Collection<AuthType> getLoginWay() {
        return strategyMap.keySet();
    }
}
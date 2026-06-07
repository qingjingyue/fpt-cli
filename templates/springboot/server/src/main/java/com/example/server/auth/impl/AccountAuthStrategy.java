package com.example.server.auth.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.example.common.constants.RegexConstant;
import com.example.common.enums.AuthType;
import com.example.common.exceptions.BizException;
import com.example.domain.dto.LoginDTO;
import com.example.domain.po.User;
import com.example.server.auth.AuthStrategy;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "example.login.way.account", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class AccountAuthStrategy implements AuthStrategy {

    private final UserService userService;
    private final ImageCaptchaApplication imageCaptchaApplication;

    /// 获取认证类型
    @Override
    public AuthType getType() {
        return AuthType.ACCOUNT;
    }

    /// 进行身份验证
    @Override
    public User authenticate(LoginDTO loginDTO) {
        // 行为验证码二次验证
        boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(loginDTO.getBehaviorCaptchaId());
        if (!valid) {
            throw new BizException("验证码验证失败");
        }
        // 参数效验
        if (loginDTO.getAccount() == null) {
            throw new BizException("用户名不能为空");
        }
        if (loginDTO.getCredential() == null) {
            throw new BizException("密码不能为空");
        }
        if (!ReUtil.isMatch(RegexConstant.USERNAME_PATTERN, loginDTO.getAccount())) {
            throw new BizException("用户名格式错误");
        }
        if (!ReUtil.isMatch(RegexConstant.PASSWORD_PATTERN, loginDTO.getCredential())) {
            throw new BizException("密码格式错误");
        }
        // 根据用户名查询
        User user = userService.lambdaQuery()
                .eq(User::getUsername, loginDTO.getAccount())
                .one();

        // 判断用户是登录还是注册
        if (loginDTO.isLogin()) {
            // 判断用户是否存在
            if (user == null) {
                throw new BizException("用户名或密码错误");
            }
            // 判断密码是否正确
            if (!DigestUtil.bcryptCheck(loginDTO.getCredential(), user.getPassword())) {
                throw new BizException("用户名或密码错误");
            }
        } else {
            // 判断用户是否存在
            if (user != null) {
                throw new BizException("用户名重复");
            }
            // 不存在则创建用户
            user = new User();
            user.setUsername(loginDTO.getAccount());
            // 密码加密
            String password = DigestUtil.bcrypt(loginDTO.getCredential());
            user.setPassword(password);
            userService.save(user);
        }
        // 返回用户信息
        return user;

    }

    /// 获取验证码
    @Override
    public void getVerifyCode(LoginDTO loginDTO) {
        throw new BizException("当前登录方式不支持获取验证码");
    }
}

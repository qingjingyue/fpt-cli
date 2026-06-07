package com.example.server.auth.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.hutool.core.util.ReUtil;
import com.example.common.constants.RegexConstant;
import com.example.common.enums.AuthType;
import com.example.common.exceptions.BizException;
import com.example.domain.dto.LoginDTO;
import com.example.domain.po.User;
import com.example.server.auth.AuthStrategy;
import com.example.server.mq.event.SendEmailVerifyCodeEvent;
import com.example.server.service.SmsService;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "example.login.way.email", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class EmailAuthStrategy implements AuthStrategy {

    private final UserService userService;
    private final SmsService smsService;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageCaptchaApplication imageCaptchaApplication;

    /// 获取认证类型
    @Override
    public AuthType getType() {
        return AuthType.EMAIL;
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
            throw new BizException("邮箱不能为空");
        }
        if (loginDTO.getCredential() == null) {
            throw new BizException("验证码不能为空");
        }
        if (!ReUtil.isMatch(RegexConstant.EMAIL_PATTERN, loginDTO.getAccount())) {
            throw new BizException("邮箱格式错误");
        }
        if (!ReUtil.isMatch(RegexConstant.VERIFY_CODE_PATTERN, loginDTO.getCredential())) {
            throw new BizException("验证码格式错误");
        }
        // 判断验证码是否正确
        boolean verifyResult = smsService.CheckEmailVerifyCode(loginDTO.getAccount(), loginDTO.getCredential());
        if (!verifyResult) {
            throw new BizException("邮箱或验证码错误");
        }
        // 根据邮箱查询用户
        User user = userService.lambdaQuery()
                .eq(User::getEmail, loginDTO.getAccount())
                .one();
        // 判断用户是否存在
        if (user == null) {
            // 不存在则创建用户
            user = new User();
            user.setEmail(loginDTO.getAccount());
            // TODO: 应该生成一个随机的用户名
            String username = loginDTO.getAccount().substring(0, loginDTO.getAccount().indexOf("@"));
            user.setUsername(username);
            userService.save(user);
        }
        // 返回用户信息
        return user;
    }

    /// 获取验证码
    @Override
    public void getVerifyCode(LoginDTO loginDTO) {
        // 效验邮箱格式
        if (!ReUtil.isMatch(RegexConstant.EMAIL_PATTERN, loginDTO.getAccount())) {
            throw new BizException("邮箱格式错误");
        }
        // 发送MQ消息
        eventPublisher.publishEvent(new SendEmailVerifyCodeEvent(loginDTO.getAccount()));
    }
}

package com.example.server.auth.impl;

import cn.hutool.core.util.ReUtil;
import com.example.common.constants.RegexConstant;
import com.example.common.enums.AuthType;
import com.example.common.exceptions.BizException;
import com.example.domain.dto.LoginDTO;
import com.example.domain.po.User;
import com.example.server.auth.AuthStrategy;
import com.example.server.mq.event.SendPhoneVerifyCodeEvent;
import com.example.server.service.SmsService;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "example.login.way.phone", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class PhoneAuthStrategy implements AuthStrategy {

    private final UserService userService;
    private final SmsService smsService;
    private final ApplicationEventPublisher eventPublisher;

    /// 获取认证类型
    @Override
    public AuthType getType() {
        return AuthType.PHONE;
    }

    /// 进行身份验证
    @Override
    public User authenticate(LoginDTO loginDTO) {
        // 参数效验
        if (loginDTO.getAccount() == null) {
            throw new BizException("手机号不能为空");
        }
        if (loginDTO.getCredential() == null) {
            throw new BizException("验证码不能为空");
        }
        if (!ReUtil.isMatch(RegexConstant.PHONE_PATTERN, loginDTO.getAccount())) {
            throw new BizException("手机号格式错误");
        }
        if (!ReUtil.isMatch(RegexConstant.VERIFY_CODE_PATTERN, loginDTO.getCredential())) {
            throw new BizException("验证码格式错误");
        }
        // 判断验证码是否正确
        boolean verifyResult = smsService.CheckPhoneVerifyCode(loginDTO.getAccount(), loginDTO.getCredential());
        if (!verifyResult) {
            throw new BizException("手机号或验证码错误");
        }
        // 根据手机号查询用户
        User user = userService.lambdaQuery()
                .eq(User::getPhone, loginDTO.getAccount())
                .one();
        // 判断用户是否存在
        if (user == null) {
            // 不存在则创建用户
            user = new User();
            user.setPhone(loginDTO.getAccount());
            // TODO: 应该生成一个随机的用户名
            user.setUsername(loginDTO.getAccount());
            userService.save(user);
        }
        // 返回用户信息
        return user;
    }

    /// 获取验证码
    @Override
    public void getVerifyCode(LoginDTO loginDTO) {
        // 效验手机号格式
        if (!ReUtil.isMatch(RegexConstant.PHONE_PATTERN, loginDTO.getAccount())) {
            throw new BizException("手机号格式错误");
        }
        // 发送MQ消息
        eventPublisher.publishEvent(new SendPhoneVerifyCodeEvent(loginDTO.getAccount()));
    }
}

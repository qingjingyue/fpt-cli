package com.example.server.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constants.RegexConstant;
import com.example.common.exceptions.BizException;
import com.example.common.properties.JwtProperties;
import com.example.common.utils.JwtUtil;
import com.example.domain.dto.AccountLoginDTO;
import com.example.domain.dto.EmailLoginDTO;
import com.example.domain.dto.PhoneLoginDTO;
import com.example.domain.po.User;
import com.example.domain.vo.UserInfoVO;
import com.example.server.mapper.UserMapper;
import com.example.server.service.SmsService;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtProperties jwtProperties;
    // private final RabbitTemplate rabbitTemplate;
    private final SmsService smsService;

    @Override
    public UserInfoVO loginByPhone(PhoneLoginDTO phoneLoginDTO) {
        // 判断验证码是否正确
        boolean verifyResult = smsService.CheckPhoneVerifyCode(phoneLoginDTO.getPhone(), phoneLoginDTO.getCode());
        if (!verifyResult) {
            throw new BizException("手机号或验证码错误");
        }
        // 根据手机号查询用户
        User user = lambdaQuery()
                .eq(User::getPhone, phoneLoginDTO.getPhone())
                .one();
        // 判断用户是否存在
        if (user == null) {
            // 不存在则创建用户
            user = new User();
            user.setPhone(phoneLoginDTO.getPhone());
            user.setUsername(phoneLoginDTO.getPhone());
            save(user);
        }
        // 生成JWT令牌
        String token = JwtUtil.createJWT(user.getId(), jwtProperties);
        // 返回用户信息
        return UserInfoVO.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    @Override
    public void sendVerifyCode(String type, String value) {
        switch (type) {
            case "phone":
                // 效验手机号格式
                if (!ReUtil.isMatch(RegexConstant.PHONE_PATTERN, value)) {
                    throw new BizException("手机号格式错误");
                }
                // 发送MQ消息
                // rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_PHONE_CODE_KEY, value);
                // 发送短信验证码
                smsService.sendPhoneVerifyCode(value);
                break;
            case "email":
                // 效验邮箱格式
                if (!ReUtil.isMatch(RegexConstant.EMAIL_PATTERN, value)) {
                    throw new BizException("邮箱格式错误");
                }
                // 发送MQ消息
                // rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_EMAIL_CODE_KEY, value);
                // 发送邮箱验证码
                smsService.sendEmailVerifyCode(value);
                break;
            default:
                throw new BizException("不支持的验证码类型");
        }
    }

    @Override
    public UserInfoVO loginByEmail(EmailLoginDTO emailLoginDTO) {
        // 判断验证码是否正确
        boolean verifyResult = smsService.CheckEmailVerifyCode(emailLoginDTO.getEmail(), emailLoginDTO.getCode());
        if (!verifyResult) {
            throw new BizException("邮箱或验证码错误");
        }
        // 根据邮箱查询用户
        User user = lambdaQuery()
                .eq(User::getEmail, emailLoginDTO.getEmail())
                .one();
        // 判断用户是否存在
        if (user == null) {
            // 不存在则创建用户
            user = new User();
            user.setEmail(emailLoginDTO.getEmail());
            user.setUsername(emailLoginDTO.getEmail().substring(0, emailLoginDTO.getEmail().indexOf("@")));
            save(user);
        }
        // 生成JWT令牌
        String token = JwtUtil.createJWT(user.getId(), jwtProperties);
        // 返回用户信息
        return UserInfoVO.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    @Override
    public UserInfoVO loginByAccount(AccountLoginDTO accountLoginDTO) {
        // 根据用户名查询
        User user = lambdaQuery()
                .eq(User::getUsername, accountLoginDTO.getAccount())
                .one();
        // 判断用户是否存在
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        // 判断密码是否正确
        if (!DigestUtil.bcryptCheck(accountLoginDTO.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        // 生成JWT令牌
        String token = JwtUtil.createJWT(user.getId(), jwtProperties);
        // 返回用户信息
        return UserInfoVO.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    @Override
    public void registerByAccount(AccountLoginDTO accountLoginDTO) {
        // 根据用户名查询
        User user = lambdaQuery()
                .eq(User::getUsername, accountLoginDTO.getAccount())
                .one();
        // 判断用户是否存在
        if (user != null) {
            throw new BizException("用户名重复");
        }
        // 不存在则创建用户
        user = new User();
        user.setUsername(accountLoginDTO.getAccount());
        // 密码加密
        String password = DigestUtil.bcrypt(accountLoginDTO.getPassword());
        user.setPassword(password);
        save(user);
    }
}

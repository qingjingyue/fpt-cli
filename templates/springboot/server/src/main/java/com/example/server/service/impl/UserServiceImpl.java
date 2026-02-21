package com.example.server.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constants.RedisConstant;
import com.example.common.constants.RegexConstant;
import com.example.common.exceptions.BizException;
import com.example.common.properties.JwtProperties;
import com.example.common.utils.JwtUtil;
import com.example.domain.dto.AccountLoginDTO;
import com.example.domain.dto.PhoneLoginDTO;
import com.example.domain.po.User;
import com.example.domain.vo.UserInfoVO;
import com.example.server.mapper.UserMapper;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.common.constants.MqConstant.Exchange.USER_EXCHANGE;
import static com.example.common.constants.MqConstant.Key.USER_PHONE_CODE_KEY;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtProperties jwtProperties;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;


    @Override
    public void register(AccountLoginDTO userLoginDTO) {
        User user = new User();
        user.setUsername(userLoginDTO.getUsername());
        // 密码加密,使用hutool的BCrypt加密（推荐使用强度 10）
        String pw = BCrypt.hashpw(userLoginDTO.getPassword(), BCrypt.gensalt(10));
        user.setPassword(pw);
        save(user);
    }

    @Override
    public UserInfoVO loginByAccount(AccountLoginDTO userLoginDTO) {
        // 根据用户名查询用户
        User user = lambdaQuery()
                .eq(User::getUsername, userLoginDTO.getUsername())
                .one();
        // 判断用户是否存在
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        // 判断密码是否正确
        if (!BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword())) {
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
    public UserInfoVO loginByPhone(PhoneLoginDTO phoneLoginDTO) {
        // 判断验证码是否正确
        String key = RedisConstant.KeyPrefix.USER_PHONE_CODE_KEY_PREFIX + phoneLoginDTO.getPhone();
        String code = redisTemplate.opsForValue().get(key);
        if (code == null || !code.equals(phoneLoginDTO.getCode())) {
            throw new BizException("手机号或验证码错误");
        }
        // 清除验证码
        redisTemplate.delete(key);
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
    public void sendPhoneCode(String phone) {
        // 效验手机号格式
        if (!ReUtil.isMatch(RegexConstant.PHONE_PATTERN, phone)) {
            throw new BizException("手机号格式错误");
        }
        // 发送MQ消息
        rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_PHONE_CODE_KEY, phone);
    }
}

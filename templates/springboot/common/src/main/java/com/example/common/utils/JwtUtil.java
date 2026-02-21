package com.example.common.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.example.common.exceptions.BizException;
import com.example.common.properties.JwtProperties;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成JWT令牌
     *
     * @param userId 用户ID
     * @return JWT令牌
     */
    public static String createJWT(Long userId, JwtProperties jwtProperties) {
        // 构建Payload (载荷)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusSeconds(jwtProperties.getTtl()); // 设置过期时间
        Map<String, Object> payload = new HashMap<>();
        // 标准字段，建议使用JWTPayload中的常量
        payload.put(JWTPayload.ISSUED_AT, now);          // 签发时间 (iat)
        payload.put(JWTPayload.EXPIRES_AT, expireTime);  // 过期时间 (exp)
        payload.put(JWTPayload.NOT_BEFORE, now);         // 生效时间 (nbf)
        // 自定义字段，存放用户信息（注意：不要存放敏感信息如密码）
        payload.put("userId", userId);
        // 密钥，用于签名（建议从配置文件中读取，不硬编码）
        byte[] secretKey = jwtProperties.getSecretKey().getBytes();
        // 生成JWT令牌
        return JWTUtil.createToken(payload, secretKey);
    }


    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 解析后的userId
     */
    public static Long parseJWT(String token, JwtProperties jwtProperties) {
        // 密钥，用于验证签名（建议从配置文件中读取，不硬编码）
        byte[] secretKey = jwtProperties.getSecretKey().getBytes();
        // 验证Token签名是否有效
        boolean isValid = JWTUtil.verify(token, secretKey);
        if (!isValid) {
            throw new BizException("请重新登录");
        }
        // 解析JWT令牌
        JWT jwt = JWTUtil.parseToken(token);
        // 验证Token是否过期
        boolean isNotExpired = jwt.validate(0);
        if (!isNotExpired) {
            throw new BizException("请重新登录");
        }
        // 从Payload中提取userId
        return (Long) jwt.getPayload("userId");
    }

}

package com.example.common.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/// 登录配置
@Data
@ConfigurationProperties(prefix = "example.login")
public class LoginProperties {

    /// 登录方式配置
    @Data
    @ConfigurationProperties(prefix = "example.login.way")
    public static class WayProperties {
        /// 账号密码登录
        private boolean account = false;
        /// 手机号登录
        private boolean phone = false;
        /// 邮箱登录
        private boolean email = false;
    }


    ///  验证码配置
    @Data
    @ConfigurationProperties(prefix = "example.login.verify-code")
    public static class VerifyCodeProperties {
        /// 验证码有效期 , 单位: 分钟
        private int ttl = 5;
    }

}

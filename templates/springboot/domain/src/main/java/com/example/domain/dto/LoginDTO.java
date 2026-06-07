package com.example.domain.dto;

import com.example.common.enums.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "登录请求参数")
public class LoginDTO {

    /// 登录/注册
    private boolean login = true;

    @NotNull(message = "认证类型不能为空")
    private AuthType authType;

    /// 账号: 用户名/手机/邮箱
    private String account;

    /// 凭证: 密码/验证码
    private String credential;

    /// 行为验证码二次验证id
    private String behaviorCaptchaId;
}

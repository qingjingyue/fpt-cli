package com.example.domain.dto;


import com.example.common.constants.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "账号登录请求参数")
public class AccountLoginDTO {

    @Schema(description = "账号")
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = RegexConstant.USERNAME_PATTERN, message = "账号格式错误")
    private String account;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = RegexConstant.PASSWORD_PATTERN, message = "密码格式错误")
    private String password;
}

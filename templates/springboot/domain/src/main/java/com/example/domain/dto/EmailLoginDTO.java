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
@Schema(name = "邮箱登录请求参数")
public class EmailLoginDTO {

    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = RegexConstant.EMAIL_PATTERN, message = "邮箱格式错误")
    private String email;

    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = RegexConstant.VERIFY_CODE_PATTERN, message = "验证码格式错误")
    private String code;
}

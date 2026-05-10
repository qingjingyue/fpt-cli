package com.example.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "登录响应数据")
public class LoginVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "JWT令牌")
    private String token;
}

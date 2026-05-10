package com.example.server.controller;


import cn.hutool.core.bean.BeanUtil;
import com.example.common.enums.AuthType;
import com.example.common.properties.JwtProperties;
import com.example.common.result.Result;
import com.example.common.utils.JwtUtil;
import com.example.domain.dto.LoginDTO;
import com.example.domain.po.User;
import com.example.domain.vo.UserInfoVO;
import com.example.server.auth.AuthManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Tag(name = "认证模块")
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthManager authManager;
    private final JwtProperties jwtProperties;

    @Operation(summary = "获取登录方式")
    @GetMapping("/login")
    public Result<Collection<AuthType>> login() {
        return Result.success(authManager.getLoginWay());
    }


    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserInfoVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = authManager.doAuth(loginDTO);
        String token = JwtUtil.createJWT(user.getId(), jwtProperties);
        UserInfoVO vo = BeanUtil.copyProperties(user, UserInfoVO.class);
        vo.setToken(token);
        return Result.success(vo);
    }

    @Operation(summary = "获取验证码")
    @PostMapping("/code")
    public Result<Void> code(@Valid @RequestBody LoginDTO loginDTO) {
        authManager.getVerifyCode(loginDTO);
        return Result.success();
    }
}

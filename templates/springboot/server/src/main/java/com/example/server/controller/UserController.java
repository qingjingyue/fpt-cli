package com.example.server.controller;


import com.example.common.result.Result;
import com.example.domain.dto.AccountLoginDTO;
import com.example.domain.dto.EmailLoginDTO;
import com.example.domain.dto.PhoneLoginDTO;
import com.example.domain.vo.UserInfoVO;
import com.example.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户模块")
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "手机号验证码登录")
    @PostMapping("/login/phone")
    public Result<UserInfoVO> loginByPhone(@Valid @RequestBody PhoneLoginDTO phoneLoginDTO) {
        UserInfoVO userInfoVO = userService.loginByPhone(phoneLoginDTO);
        return Result.success(userInfoVO);
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/login/code")
    public Result<Void> getVerifyCode(@RequestParam("type") String type, @RequestParam("value") String value) {
        userService.sendVerifyCode(type, value);
        return Result.success();
    }

    @Operation(summary = "邮箱验证码登录")
    @PostMapping("/login/email")
    public Result<UserInfoVO> loginByEmail(@Valid @RequestBody EmailLoginDTO emailLoginDTO) {
        UserInfoVO userInfoVO = userService.loginByEmail(emailLoginDTO);
        return Result.success(userInfoVO);
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login/account")
    public Result<UserInfoVO> loginByAccount(@Valid @RequestBody AccountLoginDTO accountLoginDTO) {
        UserInfoVO userInfoVO = userService.loginByAccount(accountLoginDTO);
        return Result.success(userInfoVO);
    }

    @Operation(summary = "账号密码注册")
    @PostMapping("/register/account")
    public Result<Void> registerByAccount(@Valid @RequestBody AccountLoginDTO accountLoginDTO) {
        userService.registerByAccount(accountLoginDTO);
        return Result.success();
    }
}
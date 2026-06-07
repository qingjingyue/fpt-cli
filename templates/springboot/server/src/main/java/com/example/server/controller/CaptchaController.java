package com.example.server.controller;


import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@Tag(name = "验证码模块")
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final ImageCaptchaApplication imageCaptchaApplication;

    @RequestMapping("/generate")
    public ApiResponse<ImageCaptchaVO> genCaptcha() {
        String type = switch (ThreadLocalRandom.current().nextInt(0, 4)) {
            case 0 -> CaptchaTypeConstant.SLIDER;
            case 1 -> CaptchaTypeConstant.CONCAT;
            case 2 -> CaptchaTypeConstant.ROTATE;
            default -> CaptchaTypeConstant.WORD_IMAGE_CLICK;
        };
        GenerateParam generateParam = new GenerateParam();
        // 要生成的验证码类型
        generateParam.setType(type);
        return imageCaptchaApplication.generateCaptcha(generateParam);
    }

    @PostMapping("/check")
    public ApiResponse<?> checkCaptcha(@RequestBody Data data) {
        ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), data.getData());
        if (response.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
        }
        return response;
    }

    @lombok.Data
    public static class Data {
        private String id;
        private ImageCaptchaTrack data;
    }

}

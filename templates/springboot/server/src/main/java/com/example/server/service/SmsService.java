package com.example.server.service;

public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    void sendPhoneVerifyCode(String phone);

    /**
     * 校验短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    boolean CheckPhoneVerifyCode(String phone, String code);

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     */
    void sendEmailVerifyCode(String email);

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱
     * @param code  验证码
     */
    boolean CheckEmailVerifyCode(String email, String code);
}

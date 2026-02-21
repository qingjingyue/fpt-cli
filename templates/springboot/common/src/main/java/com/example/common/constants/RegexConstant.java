package com.example.common.constants;

public interface RegexConstant {

    /**
     * 手机号正则
     */
    String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    /**
     * 邮箱正则
     */
    String EMAIL_PATTERN = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 密码正则。6~16位的字母、数字、下划线
     */
    String PASSWORD_PATTERN = "^\\w{6,16}$";

    /**
     * 用户名正则。6~16位的字母、数字、下划线
     */
    String USERNAME_PATTERN = "^\\w{6,16}$";

    /**
     * 验证码正则, 6位数字
     */
    String VERIFY_CODE_PATTERN = "^\\d{6}$";
}

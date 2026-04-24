package com.example.common.constants;

public interface RedisConstant {


    interface Key {


    }

    interface KeyPrefix {

        /**
         * 邮箱验证码缓存key
         * <p>
         * string结构，key：邮箱，value：验证码
         *
         * <table>
         *     <tr>
         *         <th>key(邮箱)</th><th>value(验证码)</th>
         *     </tr>
         *     <tr>
         *         <td>user:email:example@example.com</td><td>123456</td>
         *     </tr>
         * </table>
         */
        String USER_EMAIL_KEY_PREFIX = "user:email:";
    }
}

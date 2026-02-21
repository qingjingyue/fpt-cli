package com.example.common.constants;

public interface RedisConstant {


    interface Key {


    }

    interface KeyPrefix {

        /**
         * 手机号验证码缓存key
         * <p>
         * string结构，key：手机号，value：验证码
         * 过期时间5分钟
         *
         * <table>
         *     <tr>
         *         <th>key(手机号)</th><th>value(验证码)</th>
         *     </tr>
         *     <tr>
         *         <td>user:phone:code:13800000000</td><td>123456</td>
         *     </tr>
         * </table>
         */
        String USER_PHONE_CODE_KEY_PREFIX = "user:phone:code:";
    }
}

package com.example.common.constants;

public interface MqConstant {

    interface Queue {
    }

    interface Exchange {
        // 用户有关的交换机
        String USER_EXCHANGE = "user.topic";
    }

    interface Key {
        // 用户有关的路由键
        String USER_PHONE_CODE_KEY = "user.phone.code";
    }
}

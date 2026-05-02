package com.example.server.mq.event;

public record SendPhoneVerifyCodeEvent(
        String phone
) {
}

package com.example.server.mq.event;

public record SendEmailVerifyCodeEvent(
        String email
) {
}

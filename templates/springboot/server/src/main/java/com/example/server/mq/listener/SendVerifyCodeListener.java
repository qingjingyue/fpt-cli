package com.example.server.mq.listener;

import com.example.server.mq.event.SendEmailVerifyCodeEvent;
import com.example.server.mq.event.SendPhoneVerifyCodeEvent;
import com.example.server.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendVerifyCodeListener {

    private final SmsService smsService;


    // @RabbitListener(bindings = @QueueBinding(
    //         value = @Queue(name = "phone.code.queue", durable = "true"),
    //         exchange = @Exchange(name = MqConstant.Exchange.USER_EXCHANGE, type = ExchangeTypes.TOPIC, durable = "true"),
    //         key = MqConstant.Key.USER_PHONE_CODE_KEY
    // ))
    // public void listenPhoneCode(String phone) {
    //     try {
    //         // 发送短信验证码
    //         smsService.sendPhoneVerifyCode(phone);
    //     } catch (Exception e) {
    //         // 抛出异常，消息将被拒绝并丢弃 (不重新入队)
    //         throw new AmqpRejectAndDontRequeueException(e);
    //     }
    // }
    //
    // @RabbitListener(bindings = @QueueBinding(
    //         value = @Queue(name = "email.code.queue", durable = "true"),
    //         exchange = @Exchange(name = MqConstant.Exchange.USER_EXCHANGE, type = ExchangeTypes.TOPIC, durable = "true"),
    //         key = MqConstant.Key.USER_EMAIL_CODE_KEY
    // ))
    // public void listenEmailCode(String email) {
    //     try {
    //         // 发送邮箱验证码
    //         smsService.sendEmailVerifyCode(email);
    //     } catch (Exception e) {
    //         throw new AmqpRejectAndDontRequeueException(e);
    //     }
    // }

    @Async
    @EventListener(SendPhoneVerifyCodeEvent.class)
    public void listen(SendPhoneVerifyCodeEvent event) {
        try {
            // 发送短信验证码
            smsService.sendPhoneVerifyCode(event.phone());
        } catch (Exception e) {
            log.error("给 {} 发送短信验证码失败", event.phone(), e);
        }
    }

    @Async
    @EventListener(SendEmailVerifyCodeEvent.class)
    public void listen(SendEmailVerifyCodeEvent event) {
        try {
            // 发送邮箱验证码
            smsService.sendEmailVerifyCode(event.email());
        } catch (Exception e) {
            log.error("给 {} 发送邮箱验证码失败", event.email(), e);
        }
    }
}
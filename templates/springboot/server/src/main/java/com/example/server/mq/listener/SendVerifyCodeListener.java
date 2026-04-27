// package com.example.server.mq.listener;
//
// import com.example.common.constants.MqConstant;
// import com.example.server.service.SmsService;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.amqp.AmqpRejectAndDontRequeueException;
// import org.springframework.amqp.core.ExchangeTypes;
// import org.springframework.amqp.rabbit.annotation.Exchange;
// import org.springframework.amqp.rabbit.annotation.Queue;
// import org.springframework.amqp.rabbit.annotation.QueueBinding;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class SendVerifyCodeListener {
//
//     private final SmsService smsService;
//
//     @RabbitListener(bindings = @QueueBinding(
//             value = @Queue(name = "phone.code.queue", durable = "true"),
//             exchange = @Exchange(name = MqConstant.Exchange.USER_EXCHANGE, type = ExchangeTypes.TOPIC, durable = "true"),
//             key = MqConstant.Key.USER_PHONE_CODE_KEY
//     ))
//     public void listenPhoneCode(String phone) {
//         try {
//             // 发送短信验证码
//             smsService.sendPhoneVerifyCode(phone);
//         } catch (Exception e) {
//             // 抛出异常，消息将被拒绝并丢弃 (不重新入队)
//             throw new AmqpRejectAndDontRequeueException(e);
//         }
//     }
//
//     @RabbitListener(bindings = @QueueBinding(
//             value = @Queue(name = "email.code.queue", durable = "true"),
//             exchange = @Exchange(name = MqConstant.Exchange.USER_EXCHANGE, type = ExchangeTypes.TOPIC, durable = "true"),
//             key = MqConstant.Key.USER_EMAIL_CODE_KEY
//     ))
//     public void listenEmailCode(String email) {
//         try {
//             // 发送邮箱验证码
//             smsService.sendEmailVerifyCode(email);
//         } catch (Exception e) {
//             throw new AmqpRejectAndDontRequeueException(e);
//         }
//     }
// }
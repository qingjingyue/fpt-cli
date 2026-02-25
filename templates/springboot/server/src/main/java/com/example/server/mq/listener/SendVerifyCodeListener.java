package com.example.server.mq.listener;

import com.example.common.constants.MqConstant;
import com.example.server.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneCodeListener {

    private final SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "phone.code.queue", durable = "true"),
            exchange = @Exchange(name = MqConstant.Exchange.USER_EXCHANGE, type = ExchangeTypes.TOPIC, durable = "true"),
            key = MqConstant.Key.USER_PHONE_CODE_KEY
    ))
    public void listenPhoneCode(String phone) {
        // // 生成6位随机数字验证码
        // String code = RandomUtil.randomNumbers(6);
        // // 添加到缓存（key：手机号，value：验证码，过期时间5分钟）
        // String key = RedisConstant.KeyPrefix.USER_PHONE_CODE_KEY_PREFIX + phone;
        // redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        // // 发送短信通知（模拟）
        // String message = String.format("您的验证码为：%s，5分钟内有效", code);
        // log.info("发送短信通知：{}", message);
        try {
            smsService.sendSmsVerifyCode(phone);
        } catch (Exception e) {
            // 抛出异常，消息将被拒绝并丢弃 (不重新入队)
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}

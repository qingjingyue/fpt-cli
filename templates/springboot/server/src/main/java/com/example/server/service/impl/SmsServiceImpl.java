package com.example.server.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.example.common.constants.RedisConstant;
import com.example.common.exceptions.BizException;
import com.example.server.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final Client phoneAuthClient;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendPhoneVerifyCode(String phone) {
        // 构造请求 (可参考 https://api.aliyun.com/document/Dypnsapi/2017-05-25/SendSmsVerifyCode)
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                // .setCountryCode("86")// 号码国家编码，默认 86
                .setPhoneNumber(phone)// 短信接收方手机号
                .setSignName("速通互联验证码")// 短信签名名称
                .setTemplateCode("100001")// 短信模板编码
                .setTemplateParam("{\"code\":\"##code##\",\"min\":\"5\"}")// 短信模板参数, "##code##" 为阿里云服务生成的验证码
                .setCodeLength(6L)// 验证码长度，默认 4位
                .setValidTime(300L)// 验证码有效期，默认 300 秒 (5分钟)
                .setDuplicatePolicy(1L)// 重复校验策略，1：覆盖处理（默认），即旧验证码会失效掉。2：保留，即多个验证码都是在有效期内都可以校验通过。
                .setInterval(60L)// 重复校验时间间隔，即多久间隔可以发送一次验证码，用于频控，默认 60 秒。
                .setCodeType(1L)// 验证码类型, 1 纯数字（默认）。
                ;

        try {
            // 规则参考 https://help.aliyun.com/zh/pnvs/user-guide/sms-authentication-service
            // 发送短信验证码 (不用套餐包, 大约0.06元/条, 但可以开通免费试用100条/3个月)
            // 套餐包购买地址 https://common-buy.aliyun.com/?commodityCode=dypns_smsverify_public_cn#buy
            phoneAuthClient.sendSmsVerifyCode(request);
            // 单号码限频：验证码：最多支持1条/分钟，5条/小时，10条/天。
            // 发送记录查询 https://dypns.console.aliyun.com/smsServiceRecord
            log.info("发送短信验证码成功，手机号：{}", phone);
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    @Override
    public boolean CheckPhoneVerifyCode(String phone, String code) {
        // 构造请求 (可参考 https://api.aliyun.com/document/Dypnsapi/2017-05-25/CheckSmsVerifyCode)
        CheckSmsVerifyCodeRequest request = new CheckSmsVerifyCodeRequest()
                // .setCountryCode("86")// 号码国家编码，默认 86
                .setPhoneNumber(phone)
                .setVerifyCode(code);

        try {
            // 校验短信验证码 (校验免费)
            CheckSmsVerifyCodeResponse response = phoneAuthClient.checkSmsVerifyCode(request);
            return response.body.getModel().getVerifyResult().equals("PASS");
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    @Override
    public void sendEmailVerifyCode(String email) {
        // 生成6位随机数字验证码
        String code = RandomUtil.randomNumbers(6);
        // 添加到缓存（key：邮箱，value：验证码，过期时间5分钟）
        String key = RedisConstant.KeyPrefix.USER_EMAIL_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        // 准备邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("验证码");  // 邮件主题
        message.setText(String.format("您的验证码为%s。尊敬的客户，以上验证码%s分钟内有效，请注意保密，切勿告知他人。", code, 5));  // 邮件内容
        // 发送邮箱验证码
        mailSender.send(message);
        log.info("发送邮箱验证码成功，邮箱：{}，验证码：{}", email, code);
    }

    @Override
    public boolean CheckEmailVerifyCode(String email, String code) {
        // 查缓存是否存在该验证码
        String key = RedisConstant.KeyPrefix.USER_EMAIL_KEY_PREFIX + email;
        String cacheCode = redisTemplate.opsForValue().get(key);
        // 校验验证码是否正确
        boolean verifyResult = code != null && code.equals(cacheCode);
        if (verifyResult) {
            // 校验成功后删除缓存
            redisTemplate.delete(key);
        }
        return verifyResult;
    }
}

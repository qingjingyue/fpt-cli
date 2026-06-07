package com.example.common.autoconfig;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.spring.plugins.RedisResourceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CaptchaResourceConfig {

    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Bean
    public ResourceStore resourceStore(StringRedisTemplate redisTemplate) throws IOException {
        // 使用简单的本地内存存储器，实际项目中可以使用数据库等存储
        // LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        RedisResourceStore resourceStore = new RedisResourceStore(redisTemplate);

        org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:bgimages/**/*.jpg");
        List<String> list = Arrays.stream(resources)
                .map(org.springframework.core.io.Resource::getFilename)
                .toList();
        // System.out.println("list = " + list);
        // 添加自定义背景图片
        list.forEach((filename) -> {
            resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/" + filename, "default"));
            resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "bgimages/" + filename, "default"));
            resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "bgimages/" + filename, "default"));
            resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "bgimages/" + filename, "default"));
        });

        return resourceStore;
    }
}

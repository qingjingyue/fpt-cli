package com.example;


import com.example.server.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@SpringBootTest(classes = Application.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisString() {
        redisTemplate.opsForValue().set("test", "hello123去");
        String value = redisTemplate.opsForValue().get("test");
        System.out.println(value);
    }

    @Test
    public void testRedisStream() {
        RecordId id = redisTemplate.opsForStream().add("test:stream", Map.of("key", "value", "key2", "value2", "key3", "value3"));
        System.out.println("ID: " + id);
    }
}

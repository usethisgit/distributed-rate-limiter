package com.upen.rate_limiter_service.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final StringRedisTemplate redisTemplate;

    TestController(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/redis-test")
    public String redisTest() {
        
        redisTemplate.opsForValue().increment("test-counter");

        String value = redisTemplate.opsForValue().get("test-counter");

        return "Counter value: " + value;
    }
}

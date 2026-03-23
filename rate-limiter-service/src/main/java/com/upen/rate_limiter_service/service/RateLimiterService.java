package com.upen.rate_limiter_service.service;

import com.upen.rate_limiter_service.model.RateLimitResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_SIZE = 60;
    public RateLimiterService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

//    public boolean isAllowed(String userId){
//        String key = "rate_limit:"+userId;
//       Long requestCount = redisTemplate.opsForValue().increment(key);
//
//        if (requestCount == 1) {
//            redisTemplate.expire(key, Duration.ofSeconds(Window_SIZE));
//        }
//        return requestCount <= MAX_REQUESTS;
//    }

    public RateLimitResult checkRateLimit(String userId) {

        String key = "rate_limit:" + userId;

        Long requestCount = redisTemplate.opsForValue().increment(key);

        if (requestCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SIZE));
        }
        long remaining = MAX_REQUESTS - requestCount;
        boolean allowed = requestCount <= MAX_REQUESTS;
        return new RateLimitResult(allowed, Math.max(remaining, 0));
    }
}

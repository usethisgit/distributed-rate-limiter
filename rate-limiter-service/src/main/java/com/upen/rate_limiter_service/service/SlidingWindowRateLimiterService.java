//package com.upen.rate_limiter_service.service;
//
//import com.upen.rate_limiter_service.config.RateLimitProperties;
//import com.upen.rate_limiter_service.model.RateLimitResult;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class SlidingWindowRateLimiterService {
//    public final StringRedisTemplate redisTemplate;
//    public final RateLimitProperties rateLimitProperties;
//    private static final int MAX_REQUESTS = 5;
//    private static final int WINDOW_SIZE = 60;
//
//    public SlidingWindowRateLimiterService(StringRedisTemplate redisTemplate, RateLimitProperties rateLimitProperties){
//        this.redisTemplate = redisTemplate;
//        this.rateLimitProperties = rateLimitProperties;
//    }
//
//    public RateLimitResult isAllowed(String apiKey, String path) {
//        String normalizedPath = path.split("\\?")[0].trim();
//
//        if (normalizedPath.endsWith("/")) {
//            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
//        }
//
//        RateLimitProperties.LimitConfig config = null;
//        System.out.println("Incoming PATH: [" + normalizedPath + "]");
//
//        rateLimitProperties.getEndpoints().forEach((k,v) ->
//                System.out.println("Endpoint key: [" + k + "]"));
//        if (rateLimitProperties.getEndpoints() != null) {
//            for (Map.Entry<String, RateLimitProperties.LimitConfig> entry :
//                    rateLimitProperties.getEndpoints().entrySet()) {
//
//                String key = entry.getKey().trim();
//
//                if (normalizedPath.startsWith(key)) {
//                    config = entry.getValue();
//                    System.out.println("Matched key: " + key);
//                    break;
//                }
//            }
//        }
//
//        if (config == null) {
//            config = rateLimitProperties.getDefaultConfig();
//        }
//
//        int MAX_REQUESTS = config.getLimit();
//        int WINDOW_SIZE = config.getWindow();
//
//        String key = "rate_limit_sw:" + apiKey + ":" + path;
//
//        long currentTime = Instant.now().getEpochSecond();
//        long windowStart = currentTime - WINDOW_SIZE;
//
//        // Remove old entries
//        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
//
//        // Count only valid window entries
//        Long currentCount = redisTemplate.opsForZSet().count(key, windowStart, currentTime);
//
//        if (currentCount != null && currentCount >= MAX_REQUESTS) {
//            return new RateLimitResult(false, 0);
//        }
//
//        // Add unique request
//        String uniqueValue = currentTime + "-" + System.nanoTime();
//        redisTemplate.opsForZSet().add(key, uniqueValue, currentTime);
//
//        redisTemplate.expire(key, WINDOW_SIZE, TimeUnit.SECONDS);
//
//        long remaining = MAX_REQUESTS - (currentCount != null ? currentCount : 0) - 1;
//
//        return new RateLimitResult(true, Math.max(remaining, 0));
//    }
//}


package com.upen.rate_limiter_service.service;

import com.upen.rate_limiter_service.config.RateLimitProperties;
import com.upen.rate_limiter_service.model.RateLimitResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SlidingWindowRateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties rateLimitProperties;

    public SlidingWindowRateLimiterService(StringRedisTemplate redisTemplate,
                                           RateLimitProperties rateLimitProperties) {
        this.redisTemplate = redisTemplate;
        this.rateLimitProperties = rateLimitProperties;
    }

    public RateLimitResult isAllowed(String apiKey, String path) {

        // ✅ Normalize path (remove query params + trailing slash)
        String normalizedPath = path.split("\\?")[0].trim();

        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }

        // ✅ Find matching config
        RateLimitProperties.LimitConfig config = null;

        if (rateLimitProperties.getEndpoints() != null) {
            for (Map.Entry<String, RateLimitProperties.LimitConfig> entry :
                    rateLimitProperties.getEndpoints().entrySet()) {

                String key = entry.getKey().trim();
                System.out.println("Checking key: " + key + " against path: " + normalizedPath);
                if (normalizedPath.startsWith(key)) {
                    config = entry.getValue();
                    System.out.println("Matched key: " + key);
                    break;
                }
            }
        }

        // ✅ Fallback to default config
        if (config == null) {
            config = rateLimitProperties.getDefaultConfig();
            System.out.println("Using default config");
        }

        // ❗ Safety check (should never happen if YAML is correct)
        if (config == null) {
            throw new RuntimeException("Rate limit config missing for path: " + normalizedPath);
        }

        int maxRequests = config.getLimit();
        int windowSize = config.getWindow();

        // ✅ Use normalized path in Redis key (VERY IMPORTANT)
        String redisKey = "rate_limit_sw:" + apiKey + ":" + normalizedPath;

        long currentTime = Instant.now().getEpochSecond();
        long windowStart = currentTime - windowSize;

        // 1️⃣ Remove old requests
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);

        // 2️⃣ Count requests in current window
        Long currentCount = redisTemplate.opsForZSet()
                .count(redisKey, windowStart, currentTime);

        if (currentCount != null && currentCount >= maxRequests) {
            return new RateLimitResult(false, 0);
        }

        // 3️⃣ Add current request (unique value)
        String uniqueValue = currentTime + "-" + System.nanoTime();
        redisTemplate.opsForZSet().add(redisKey, uniqueValue, currentTime);

        // 4️⃣ Set TTL for cleanup
        redisTemplate.expire(redisKey, windowSize, TimeUnit.SECONDS);

        long remaining = maxRequests - (currentCount != null ? currentCount : 0) - 1;

        return new RateLimitResult(true, Math.max(remaining, 0));
    }
}
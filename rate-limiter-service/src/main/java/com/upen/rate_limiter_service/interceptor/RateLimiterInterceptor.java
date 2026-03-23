package com.upen.rate_limiter_service.interceptor;

import com.upen.rate_limiter_service.model.RateLimitResult;
import com.upen.rate_limiter_service.service.RateLimiterService;
import com.upen.rate_limiter_service.service.SlidingWindowRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_SIZE = 60;

    private final RateLimiterService rateLimiterService;
    private final SlidingWindowRateLimiterService slidingWindowRateLimiterService;

    public RateLimiterInterceptor(RateLimiterService rateLimiterService,
                                  SlidingWindowRateLimiterService slidingWindowRateLimiterService) {
        this.rateLimiterService = rateLimiterService;
        this.slidingWindowRateLimiterService = slidingWindowRateLimiterService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("Missing API Key");
            return false;
        }

        // ✅ GET PATH HERE
        String path = request.getRequestURI();

        System.out.println("path : "+path);
        // ✅ PASS PATH TO SERVICE
        RateLimitResult result = slidingWindowRateLimiterService.isAllowed(apiKey, path);

        response.setHeader("X-RateLimit-Limit", "...");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemaining()));

        if (!result.isAllowed()) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return false;
        }

        return true;
    }
}

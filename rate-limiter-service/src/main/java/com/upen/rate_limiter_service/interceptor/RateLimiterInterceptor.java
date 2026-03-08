package com.upen.rate_limiter_service.interceptor;

import com.upen.rate_limiter_service.model.RateLimitResult;
import com.upen.rate_limiter_service.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_SIZE = 60;

    private final RateLimiterService rateLimiterService;
    public RateLimiterInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

//        String userIp = request.getRemoteAddr();

        String path = request.getRequestURI();

        RateLimitResult result = rateLimiterService.checkRateLimit(path);

        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader("X-RateLimit-Remaining",String.valueOf(result.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(WINDOW_SIZE));

        if (!result.isAllowed()) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");

            return false;
        }
        return true;
    }
}

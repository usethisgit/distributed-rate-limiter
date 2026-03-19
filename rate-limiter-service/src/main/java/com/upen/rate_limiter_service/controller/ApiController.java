package com.upen.rate_limiter_service.controller;

import com.upen.rate_limiter_service.service.RateLimiterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final RateLimiterService rateLimiterService;

    public ApiController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

//    @GetMapping("/api/test")
//    public String testEndpoint() {
//        String userId = "user1";
//        boolean allowed = rateLimiterService.isAllowed(userId);
//        if (!allowed) {
//            return "429 Too Many Requests";
//        }
//        return "Request Successful";
//    }

    @GetMapping("/api/test")
    public String testEndpoint() {
        return "API response success";
    }

    @GetMapping("/api/payment")
    public String paymentEndPoint() {
        return "Payment API response success";
    }

    @GetMapping("/api/login")
    public String loginEndPoint() {
        return "Login success";
    }
}
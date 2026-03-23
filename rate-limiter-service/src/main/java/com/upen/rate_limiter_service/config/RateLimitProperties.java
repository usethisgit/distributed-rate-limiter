package com.upen.rate_limiter_service.config;

import jdk.jfr.Name;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {
           // This fixes Mapping
    private LimitConfig defaultConfig;   // matches default-config
    private Map<String, LimitConfig> endpoints;

    @Data
    public static class LimitConfig {
        private int limit;
        private int window;
    }
}
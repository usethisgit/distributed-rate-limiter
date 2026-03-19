package com.upen.rate_limiter_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private LimitConfig defaultConfig;
    private Map<String, LimitConfig> endpoints;

    public static class LimitConfig {
        private int limit;
        private int window;

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getWindow() {
            return window;
        }

        public void setWindow(int window) {
            this.window = window;
        }
    }

    public LimitConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(LimitConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Map<String, LimitConfig> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, LimitConfig> endpoints) {
        this.endpoints = endpoints;
    }
}
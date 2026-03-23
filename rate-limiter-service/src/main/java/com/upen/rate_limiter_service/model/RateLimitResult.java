package com.upen.rate_limiter_service.model;

import lombok.Data;

@Data
public class RateLimitResult {

    private final boolean allowed;
    private final long remaining;

    public RateLimitResult(boolean allowed, long remaining) {
        this.allowed = allowed;
        this.remaining = remaining;
    }
}
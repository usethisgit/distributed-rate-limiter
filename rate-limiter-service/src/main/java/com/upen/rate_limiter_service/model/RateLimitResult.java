package com.upen.rate_limiter_service.model;

public class RateLimitResult {

    private final boolean allowed;
    private final long remaining;

    public RateLimitResult(boolean allowed, long remaining) {
        this.allowed = allowed;
        this.remaining = remaining;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRemaining() {
        return remaining;
    }
}
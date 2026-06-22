package com.lld.ratelimiter.impl;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Author  AAgarwal
 * <p>
 * Date   6/21/2026
 */

@AllArgsConstructor
@Data
public abstract class RateLimiter
{
    private RateLimiterConfig rateLimiterConfig;
    private RateLimiterType rateLimiterType;
    public abstract boolean allowRequest(String userId);
}
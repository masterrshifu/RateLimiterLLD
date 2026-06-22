package com.lld.ratelimiter.factory;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;
import com.lld.ratelimiter.impl.FixedWindowRateLimiter;
import com.lld.ratelimiter.impl.RateLimiter;
import com.lld.ratelimiter.impl.SlidingWindowRateLimiter;
import com.lld.ratelimiter.impl.TokenBucketRateLimiter;
/**
 * Author  AAgarwal
 * <p>
 * Date   6/21/2026
 */
public class RateLimiterFactory
{
    public static RateLimiter createRateLimiter(RateLimiterConfig rateLimiterConfig, RateLimiterType rateLimiterType)
    {
        return switch (rateLimiterType) {
            case FIXED_WINDOW -> new FixedWindowRateLimiter(rateLimiterConfig);
            case SLIDING_WINDOW -> new SlidingWindowRateLimiter(rateLimiterConfig);
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(rateLimiterConfig, rateLimiterType);
        };
    }
}

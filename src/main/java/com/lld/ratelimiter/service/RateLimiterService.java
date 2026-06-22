package com.lld.ratelimiter.service;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;
import com.lld.ratelimiter.enums.UserTier;
import com.lld.ratelimiter.factory.RateLimiterFactory;
import com.lld.ratelimiter.impl.RateLimiter;
import com.lld.ratelimiter.model.User;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service layer used by application/client code.
 */
public class RateLimiterService
{
    private final Map<UserTier, RateLimiter> rateLimiters = new EnumMap<>(UserTier.class);

    public RateLimiterService()
    {
        rateLimiters.put(
                UserTier.FREE,
                RateLimiterFactory.createRateLimiter(new RateLimiterConfig(10, 10, 1), RateLimiterType.TOKEN_BUCKET)
        );

        rateLimiters.put(
                UserTier.PREMIUM,
                RateLimiterFactory.createRateLimiter(new RateLimiterConfig(30, 60, 1), RateLimiterType.FIXED_WINDOW)
        );

        rateLimiters.put(
                UserTier.ENTERPRISE,
                RateLimiterFactory.createRateLimiter(new RateLimiterConfig(100, 60, 5), RateLimiterType.SLIDING_WINDOW)
        );
    }

    /**
     * Use this constructor when all users should share the same rate limit rule.
     */
    public RateLimiterService(RateLimiterConfig config, RateLimiterType rateLimiterType)
    {
        RateLimiter rateLimiter = RateLimiterFactory.createRateLimiter(config, rateLimiterType);
        rateLimiters.put(UserTier.FREE, rateLimiter);
        rateLimiters.put(UserTier.PREMIUM, rateLimiter);
        rateLimiters.put(UserTier.ENTERPRISE, rateLimiter);
    }

    public boolean allowRequest(User user)
    {
        RateLimiter rateLimiter = rateLimiters.get(user.getUserTier());

        if(rateLimiter == null) {
            throw new IllegalArgumentException("No rate limiter configured for user tier: " + user.getUserTier());
        }

        return rateLimiter.allowRequest(user.getUserId());
    }

    public boolean allowRequest(String userId)
    {
        return allowRequest(new User(userId, UserTier.FREE));
    }
}

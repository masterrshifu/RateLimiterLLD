package com.lld.ratelimiter.impl;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Author  AAgarwal
 * <p>
 * Date   6/21/2026
 */
public class TokenBucketRateLimiter extends RateLimiter
{
    private final Map<String, Integer> buckets = new ConcurrentHashMap<>();

    private final Map<String, Long> lastRefillTime = new ConcurrentHashMap<>();

    private final RateLimiterConfig config;

    public TokenBucketRateLimiter(RateLimiterConfig config, RateLimiterType rateLimiterType)
    {
        super(config, rateLimiterType);
        this.config = config;
    }

    public boolean allowRequest(String userId)
    {
        AtomicBoolean allowed = new AtomicBoolean(false);

        buckets.compute( userId, (id, availableTokens) -> {

            long now = System.currentTimeMillis();
            int currentTokens = refillTokens(userId, availableTokens, now);
            if(currentTokens > 0) {
                allowed.set(true);
                return currentTokens - 1;
            }

            return currentTokens;

        } );

        return allowed.get();
    }

    private int refillTokens(String userId, Integer availableTokens, long now)
    {
        if(availableTokens == null) {
            lastRefillTime.put(userId, now);
            return config.getMaxRequests();
        }

        long lastRefill = lastRefillTime.getOrDefault(userId, now);
        long elapsedTimeInSeconds = (now - lastRefill) / 1000;

        if(elapsedTimeInSeconds <= 0) {
            return availableTokens;
        }

        int tokensToAdd = (int) elapsedTimeInSeconds * config.getRefillTokensPerSecond();
        int updatedTokens = Math.min(config.getMaxRequests(), availableTokens + tokensToAdd);
        lastRefillTime.put(userId, now);
        return updatedTokens;
    }

}
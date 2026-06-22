package com.lld.ratelimiter.impl;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Counts requests for each user in fixed time windows.
 */
public class FixedWindowRateLimiter extends RateLimiter
{
    private final Map<String, Window> userWindows = new ConcurrentHashMap<>();
    private final RateLimiterConfig config;

    public FixedWindowRateLimiter(RateLimiterConfig config)
    {
        super(config, RateLimiterType.FIXED_WINDOW);
        this.config = config;
    }

    @Override
    public boolean allowRequest(String userId)
    {
        AtomicBoolean allowed = new AtomicBoolean(false);
        long now = System.currentTimeMillis();
        long windowSizeInMillis = config.getWindowSizeInSeconds() * 1000L;

        userWindows.compute(userId, (id, window) -> {
            if(window == null || now - window.startTime >= windowSizeInMillis) {
                allowed.set(true);
                return new Window(now, 1);
            }

            if(window.requestCount < config.getMaxRequests()) {
                allowed.set(true);
                window.requestCount++;
            }

            return window;
        });

        return allowed.get();
    }

    private static class Window
    {
        private final long startTime;
        private int requestCount;

        private Window(long startTime, int requestCount)
        {
            this.startTime = startTime;
            this.requestCount = requestCount;
        }
    }
}

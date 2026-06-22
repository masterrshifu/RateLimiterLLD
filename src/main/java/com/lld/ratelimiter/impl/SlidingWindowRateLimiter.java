package com.lld.ratelimiter.impl;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Keeps request timestamps for each user and allows only maxRequests
 * in the last windowSizeInSeconds seconds.
 */
public class SlidingWindowRateLimiter extends RateLimiter
{
    private final Map<String, Deque<Long>> userRequests = new ConcurrentHashMap<>();
    private final RateLimiterConfig config;

    public SlidingWindowRateLimiter(RateLimiterConfig config)
    {
        super(config, RateLimiterType.SLIDING_WINDOW);
        this.config = config;
    }

    @Override
    public boolean allowRequest(String userId)
    {
        AtomicBoolean allowed = new AtomicBoolean(false);
        long now = System.currentTimeMillis();
        long windowStartTime = now - config.getWindowSizeInSeconds() * 1000L;

        userRequests.compute(userId, (id, requests) -> {
            if(requests == null) {
                requests = new ConcurrentLinkedDeque<>();
            }

            while(!requests.isEmpty() && requests.peekFirst() <= windowStartTime) {
                requests.pollFirst();
            }

            if(requests.size() < config.getMaxRequests()) {
                requests.addLast(now);
                allowed.set(true);
            }

            return requests;
        });

        return allowed.get();
    }
}

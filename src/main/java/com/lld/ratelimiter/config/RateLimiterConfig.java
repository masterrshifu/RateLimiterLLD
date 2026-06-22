package com.lld.ratelimiter.config;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Author  AAgarwal
 * <p>
 * Date   6/21/2026
 */

@Data
@AllArgsConstructor
public class RateLimiterConfig
{
    private int maxRequests = 10;
    private int windowSizeInSeconds = 5;
    private int refillTokensPerSecond = 1;
}

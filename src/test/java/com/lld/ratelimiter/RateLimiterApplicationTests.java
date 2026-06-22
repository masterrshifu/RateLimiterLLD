package com.lld.ratelimiter;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;
import com.lld.ratelimiter.service.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "ratelimiter.demo.enabled=false")
class RateLimiterApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void tokenBucketAllowsOnlyConfiguredRequests() {
		RateLimiterConfig config = new RateLimiterConfig(2, 10, 1);
		RateLimiterService rateLimiterService = new RateLimiterService(config, RateLimiterType.TOKEN_BUCKET);

		assertTrue(rateLimiterService.allowRequest("user-1"));
		assertTrue(rateLimiterService.allowRequest("user-1"));
		assertFalse(rateLimiterService.allowRequest("user-1"));
	}

	@Test
	void slidingWindowAllowsOnlyConfiguredRequestsInsideWindow() {
		RateLimiterConfig config = new RateLimiterConfig(2, 10, 1);
		RateLimiterService rateLimiterService = new RateLimiterService(config, RateLimiterType.SLIDING_WINDOW);

		assertTrue(rateLimiterService.allowRequest("user-1"));
		assertTrue(rateLimiterService.allowRequest("user-1"));
		assertFalse(rateLimiterService.allowRequest("user-1"));
		assertTrue(rateLimiterService.allowRequest("user-2"));
	}

	@Test
	void fixedWindowAllowsOnlyConfiguredRequestsInsideWindow() {
		RateLimiterConfig config = new RateLimiterConfig(2, 10, 1);
		RateLimiterService rateLimiterService = new RateLimiterService(config, RateLimiterType.FIXED_WINDOW);

		assertTrue(rateLimiterService.allowRequest("user-1"));
		assertTrue(rateLimiterService.allowRequest("user-1"));
		assertFalse(rateLimiterService.allowRequest("user-1"));
	}

}

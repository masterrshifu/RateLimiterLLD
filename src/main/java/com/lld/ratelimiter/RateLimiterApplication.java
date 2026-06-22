package com.lld.ratelimiter;

import com.lld.ratelimiter.config.RateLimiterConfig;
import com.lld.ratelimiter.enums.RateLimiterType;
import com.lld.ratelimiter.service.RateLimiterService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimiterApplication {

	public static void main(String[] args) throws InterruptedException {

		RateLimiterService tokenBucketRateLimiter = new RateLimiterService(
				new RateLimiterConfig(2, 10, 1),
				RateLimiterType.TOKEN_BUCKET
		);

		System.out.println("TOKEN BUCKET");
		System.out.println(tokenBucketRateLimiter.allowRequest("user-1")); // true
		System.out.println(tokenBucketRateLimiter.allowRequest("user-1")); // true
		System.out.println(tokenBucketRateLimiter.allowRequest("user-1")); // false

		Thread.sleep(1000);

		System.out.println(tokenBucketRateLimiter.allowRequest("user-1")); // true, 1 token refilled

		RateLimiterService fixedWindowRateLimiter = new RateLimiterService(
				new RateLimiterConfig(2, 5, 1),
				RateLimiterType.FIXED_WINDOW
		);

		System.out.println("FIXED WINDOW");
		System.out.println(fixedWindowRateLimiter.allowRequest("user-2")); // true
		System.out.println(fixedWindowRateLimiter.allowRequest("user-2")); // true
		System.out.println(fixedWindowRateLimiter.allowRequest("user-2")); // false

		Thread.sleep(1000);

		System.out.println(fixedWindowRateLimiter.allowRequest("user-2")); // false, window is still 5 seconds
	}

}

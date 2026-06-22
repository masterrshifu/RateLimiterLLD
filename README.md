# Rate Limiter LLD

This project is a simple Low Level Design for a Rate Limiter.

Goal: decide whether a request from a user should be allowed or blocked.

## Requirements

Functional requirements:

- Limit requests per user.
- Support different rate limiting algorithms.
- Return `true` when request is allowed.
- Return `false` when request is blocked.

Non-functional requirements:

- Should be thread-safe.
- Should be easy to extend with new algorithms.
- Should keep per-user request data in memory.

## Main Classes

### RateLimiterService

Service class used by client/application code.

```java
RateLimiterService service = new RateLimiterService();
boolean allowed = service.allowRequest(user);
```

The service hides factory and algorithm details from the caller.

It keeps a map like this:

```java
Map<UserTier, RateLimiter> rateLimiters;
```

This means different users can have different rate limit rules:

- `FREE`: smaller limit, token bucket.
- `PREMIUM`: higher limit, fixed window.
- `ENTERPRISE`: highest limit, sliding window.

So the caller only passes a `User`, and the service chooses the right limiter based on `user.getUserTier()`.

### RateLimiter

Abstract base class.

```java
public abstract boolean allowRequest(String userId);
```

Every algorithm implements this method.

### RateLimiterConfig

Stores common configuration:

- `maxRequests`: maximum requests allowed.
- `windowSizeInSeconds`: window duration for fixed/sliding window.
- `refillTokensPerSecond`: token refill speed for token bucket.

### RateLimiterFactory

Creates the correct rate limiter based on `RateLimiterType`.

This keeps object creation logic in one place.

### RateLimiterType

Supported algorithms:

- `FIXED_WINDOW`
- `SLIDING_WINDOW`
- `TOKEN_BUCKET`

## Algorithms

### Fixed Window

Idea:

- Divide time into fixed windows.
- Count requests for each user in the current window.
- If count is less than limit, allow request.
- Otherwise block request.

Example:

- Limit = 5 requests per 60 seconds.
- User can make only 5 requests in the current 60 second window.

Pros:

- Very simple.
- Memory efficient.

Cons:

- Can allow bursts at window boundaries.

### Sliding Window

Idea:

- Store timestamps of requests for each user.
- Remove timestamps older than the window.
- Allow request only if remaining timestamps are less than limit.

Example:

- Limit = 5 requests per 60 seconds.
- At any point, only 5 requests are allowed in the last 60 seconds.

Pros:

- More accurate than fixed window.

Cons:

- Stores more data because request timestamps are kept.

### Token Bucket

Idea:

- Each user has a bucket of tokens.
- Every request consumes 1 token.
- Tokens are refilled over time.
- If bucket has tokens, allow request.
- If bucket is empty, block request.

Example:

- Bucket capacity = 10.
- Refill rate = 1 token per second.

Pros:

- Handles bursts well.
- Commonly used in real systems.

Cons:

- Slightly more logic than fixed window.

## Interview Explanation

Start with this simple flow:

1. Client sends request with `userId`.
2. System calls `rateLimiterService.allowRequest(userId)`.
3. Service delegates to the selected rate limiter algorithm.
4. Rate limiter checks user request history or tokens.
5. If request is within limit, return `true`.
6. Otherwise return `false`.

## Why ConcurrentHashMap?

In a real application, multiple requests can come at the same time.

`ConcurrentHashMap` helps us safely store per-user state like:

- request count
- request timestamps
- available tokens

## How To Extend

To add a new algorithm:

1. Create a new class extending `RateLimiter`.
2. Implement `allowRequest(String userId)`.
3. Add the new type in `RateLimiterType`.
4. Add object creation in `RateLimiterFactory`.

## Simple Design Diagram

```text
Client
  |
  v
RateLimiterService
  |
  v
RateLimiterFactory
  |
  v
RateLimiter
  |
  +-- FixedWindowRateLimiter
  +-- SlidingWindowRateLimiter
  +-- TokenBucketRateLimiter
```

## What To Say In Interview

"I created a common `RateLimiter` abstraction and implemented multiple algorithms behind it. The caller only calls `allowRequest(userId)`, so the algorithm can be changed without changing client code. Per-user state is stored in concurrent maps to handle multiple requests safely. For a simple system this can run in memory, and for distributed systems the same logic can be moved to Redis."

## How To Run

Run tests:

```bash
mvn test
```

Run application demo:

```bash
mvn spring-boot:run
```

The application prints sample requests as `ALLOWED` or `BLOCKED`.

## Future Improvements

- Use Redis for distributed rate limiting.
- Add user-tier based limits like FREE, PREMIUM, ENTERPRISE.
- Clean old inactive users from memory.
- Return retry-after time instead of only true or false.

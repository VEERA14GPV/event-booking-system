package com.booking.security.idempotency;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    /*
     * Redis template.
     */
    private final RedisTemplate<String, Object>
            redisTemplate;

    public IdempotencyService(

            RedisTemplate<String, Object>
                    redisTemplate) {

        this.redisTemplate =
                redisTemplate;
    }

    /*
     * Check if key exists.
     */
    public boolean exists(
            String key) {

        return Boolean.TRUE.equals(

                redisTemplate.hasKey(
                        buildKey(key)
                )
        );
    }

    /*
     * Save response in Redis.
     */
    public void saveResponse(

            String key,

            IdempotencyResponse response,

            Duration ttl) {

        redisTemplate.opsForValue()

                .set(

                        buildKey(key),

                        response,

                        ttl
                );
    }

    /*
     * Get cached response.
     */
    public IdempotencyResponse
    getResponse(
            String key) {

        Object response =

                redisTemplate.opsForValue()

                        .get(buildKey(key));

        if (response instanceof
                IdempotencyResponse
                idempotencyResponse) {

            return idempotencyResponse;
        }

        return null;
    }

    /*
     * Delete key.
     */
    public void delete(
            String key) {

        redisTemplate.delete(
                buildKey(key)
        );
    }

    /*
     * Build Redis key.
     */
    private String buildKey(
            String key) {

        return IdempotencyConstants
                .IDEMPOTENCY_PREFIX
                + key;
    }
}

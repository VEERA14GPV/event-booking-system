package com.booking.security.ratelimit;

import io.github.bucket4j.*;

import org.springframework.stereotype.Service;

import java.time.Duration;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRateLimiterService {

    /*
     * Stores bucket per authenticated user.
     *
     * Key:
     * userId
     *
     * Value:
     * Bucket object
     */
    private final Map<Long, Bucket>
            cache = new ConcurrentHashMap<>();

    /*
     * Resolve bucket using userId.
     */
    public Bucket resolveBucket(
            Long userId) {

        return cache.computeIfAbsent(

                userId,

                id -> newBucket()
        );
    }

    /*
     * Create bucket for authenticated user.
     *
     * Limit:
     * 20 requests per minute.
     */
    private Bucket newBucket() {

        Bandwidth limit =

                Bandwidth.builder()

                        .capacity(20)

                        .refillGreedy(

                                20,

                                Duration.ofMinutes(1)
                        )

                        .build();

        return Bucket.builder()

                .addLimit(limit)

                .build();
    }
}

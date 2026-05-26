package com.booking.security.ratelimit;

import io.github.bucket4j.*;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRateLimiterService {

    private final Map<Long, Bucket>
            cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(
            Long userId) {

        return cache.computeIfAbsent(
                userId,
                this::newBucket
        );
    }

    private Bucket newBucket(
            Long userId) {

        Bandwidth limit =
                Bandwidth.builder()
                        .capacity(50)
                        .refillGreedy(
                                50,
                                Duration.ofMinutes(1)
                        )
                        .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
package com.booking.security.ratelimit;

import io.github.bucket4j.*;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IpRateLimiterService {

    private final Map<String, Bucket>
            cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(
            String ipAddress) {

        return cache.computeIfAbsent(
                ipAddress,
                this::newBucket
        );
    }

    private Bucket newBucket(
            String ip) {

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
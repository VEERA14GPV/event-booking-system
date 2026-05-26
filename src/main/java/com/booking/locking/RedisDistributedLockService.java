package com.booking.locking;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisDistributedLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SEAT_LOCK_PREFIX =
            "seat_lock:";

    public RedisDistributedLockService(
            RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public boolean lockSeat(
            Long seatId,
            Long userId) {

        String key = SEAT_LOCK_PREFIX + seatId;

        Boolean success =
                redisTemplate.opsForValue()
                        .setIfAbsent(
                                key,
                                userId,
                                Duration.ofMinutes(5)
                        );

        return Boolean.TRUE.equals(success);
    }

    public void unlockSeat(Long seatId) {

        String key = SEAT_LOCK_PREFIX + seatId;

        redisTemplate.delete(key);
    }

    public boolean isSeatLocked(Long seatId) {

        String key = SEAT_LOCK_PREFIX + seatId;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }
}
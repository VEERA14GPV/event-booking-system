package com.booking.locking;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisDistributedLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisDistributedLockService(
            RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public boolean lockSeat(
            Long showId,
            Long seatId,
            Long userId) {

        String key =
                LockKeyGenerator.generateShowSeatLockKey(
                        showId,
                        seatId
                );

        Boolean success =
                redisTemplate.opsForValue()
                        .setIfAbsent(
                                key,
                                userId,
                                Duration.ofMinutes(5)
                        );

        return Boolean.TRUE.equals(success);
    }

    public boolean isSeatLocked(
            Long showId,
            Long seatId) {

        String key =
                LockKeyGenerator.generateShowSeatLockKey(
                        showId,
                        seatId
                );

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }

    public void unlockSeat(
            Long showId,
            Long seatId) {

        String key =
                LockKeyGenerator.generateShowSeatLockKey(
                        showId,
                        seatId
                );

        redisTemplate.delete(key);
    }

    public Long getRemainingLockTime(
            Long showId,
            Long seatId) {

        String key =
                LockKeyGenerator.generateShowSeatLockKey(
                        showId,
                        seatId
                );

        return redisTemplate.getExpire(key);
    }

    public Object getLockOwner(
            Long showId,
            Long seatId) {

        String key =
                LockKeyGenerator.generateShowSeatLockKey(
                        showId,
                        seatId
                );

        return redisTemplate.opsForValue().get(key);
    }
}
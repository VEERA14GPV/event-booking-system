package com.booking.service.ttl;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SeatLockTtlService {

    private static final long TTL_MINUTES = 5;

    public LocalDateTime getExpiryTime() {

        return LocalDateTime.now()
                .plusMinutes(TTL_MINUTES);
    }

    public boolean isExpired(
            LocalDateTime expiryTime) {

        return LocalDateTime.now()
                .isAfter(expiryTime);
    }
}
package com.booking.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdempotencyUtil {

    /*
     * Generate unique idempotency key
     */
    public String generateKey() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toUpperCase();
    }

    /*
     * Validate idempotency key
     */
    public boolean isValidKey(
            String key) {

        return key != null
                && !key.isBlank()
                && key.length() >= 16;
    }
}
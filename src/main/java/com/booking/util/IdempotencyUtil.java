package com.booking.util;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyUtil {

    private final Set<String> processedKeys =
            ConcurrentHashMap.newKeySet();

    public String generateKey() {

        return UUID.randomUUID().toString();
    }

    public boolean isDuplicate(String key) {

        return processedKeys.contains(key);
    }

    public void markProcessed(String key) {

        processedKeys.add(key);
    }
}
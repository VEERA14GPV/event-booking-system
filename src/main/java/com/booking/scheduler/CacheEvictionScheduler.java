package com.booking.scheduler;

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictionScheduler {

    private final CacheManager cacheManager;

    public CacheEvictionScheduler(
            CacheManager cacheManager) {

        this.cacheManager = cacheManager;
    }

    /*
     * Clears event cache every 30 minutes
     */
    
    @Scheduled(fixedRate = 1800000)
    public void clearEventCache() {

        if (cacheManager.getCache("events")!= null) {

            cacheManager
                    .getCache("events")
                    .clear();
        }
    }

    /*
     * Clears show cache every 30 minutes
     */
    
    @Scheduled(fixedRate = 1800000)
    public void clearShowCache() {

        if (cacheManager.getCache("shows")!= null) {

            cacheManager
                    .getCache("shows")
                    .clear();
        }
    }

    /*
     * Clears seat cache every 10 minutes
     */
    
    @Scheduled(fixedRate = 600000)
    public void clearSeatCache() {

        if (cacheManager.getCache("seats")!= null) {

            cacheManager
                    .getCache("seats")
                    .clear();
        }
    }
}

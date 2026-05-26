package com.booking.service;

import com.booking.locking.RedisDistributedLockService;

import org.springframework.stereotype.Service;

@Service
public class SeatLockService {

    private final RedisDistributedLockService lockService;

    public SeatLockService(
            RedisDistributedLockService lockService) {

        this.lockService = lockService;
    }

    /*
     * Lock seat
     */
    public boolean lockSeat(

            Long showId,

            Long seatId,

            Long userId) {

        return lockService.lockSeat(

                showId,

                seatId,

                userId
        );
    }

    /*
     * Unlock seat
     */
    public void unlockSeat(

            Long showId,

            Long seatId) {

        lockService.unlockSeat(

                showId,

                seatId
        );
    }

    /*
     * Check seat lock
     */
    public boolean isSeatLocked(

            Long showId,

            Long seatId) {

        return lockService.isSeatLocked(

                showId,

                seatId
        );
    }
}
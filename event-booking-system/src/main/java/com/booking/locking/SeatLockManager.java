package com.booking.locking;

import com.booking.entity.Seat;
import com.booking.entity.SeatLock;

import com.booking.enums.SeatStatus;

import com.booking.exception.SeatLockException;

import com.booking.repository.SeatLockRepository;
import com.booking.repository.SeatRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class SeatLockManager {

    private final RedisDistributedLockService redisDistributedLockService;

    private final SeatRepository seatRepository;

    private final SeatLockRepository seatLockRepository;

    private static final long TTL_MINUTES = 5;

    public SeatLockManager(
            RedisDistributedLockService redisDistributedLockService,
            SeatRepository seatRepository,
            SeatLockRepository seatLockRepository) {

        this.redisDistributedLockService = redisDistributedLockService;
        this.seatRepository = seatRepository;
        this.seatLockRepository = seatLockRepository;
    }

    /*
     * Lock a seat: validate, acquire Redis lock, persist LOCKED status,
     * and create a SeatLock DB row so the cleanup scheduler has data to work with.
     */
    @Transactional
    public void lockSeat(Long showId, Long seatId, Long userId) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatLockException("Seat not found: " + seatId));

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new SeatLockException("Seat already booked: " + seatId);
        }

        boolean success = redisDistributedLockService.lockSeat(showId, seatId, userId);

        if (!success) {
            throw new SeatLockException("Seat is already locked: " + seatId);
        }

        seat.setStatus(SeatStatus.LOCKED);
        seatRepository.save(seat);

        /*
         * Persist a SeatLock row so SeatLockCleanupScheduler can clean up
         * if the user never completes payment.
         */
        SeatLock lock = new SeatLock();
        lock.setShowId(showId);
        lock.setSeatId(seatId);
        lock.setUserId(userId);
        lock.setLockedAt(LocalDateTime.now());
        lock.setExpiresAt(LocalDateTime.now().plusMinutes(TTL_MINUTES));
        seatLockRepository.save(lock);
    }

    /*
     * Unlock a seat: release Redis lock, set status AVAILABLE, remove DB row.
     */
    @Transactional
    public void unlockSeat(Long showId, Long seatId) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatLockException("Seat not found: " + seatId));

        redisDistributedLockService.unlockSeat(showId, seatId);

        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);

        seatLockRepository.findByShowIdAndSeatId(showId, seatId)
                .ifPresent(seatLockRepository::delete);
    }

    /*
     * Confirm a seat after successful payment: release Redis lock,
     * set status BOOKED, remove DB lock row.
     */
    @Transactional
    public void confirmSeat(Long showId, Long seatId) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatLockException("Seat not found: " + seatId));

        redisDistributedLockService.unlockSeat(showId, seatId);

        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        seatLockRepository.findByShowIdAndSeatId(showId, seatId)
                .ifPresent(seatLockRepository::delete);
    }

    /*
     * Check whether a seat is currently locked in Redis.
     */
    public boolean isSeatLocked(Long showId, Long seatId) {

        return redisDistributedLockService.isSeatLocked(showId, seatId);
    }
}

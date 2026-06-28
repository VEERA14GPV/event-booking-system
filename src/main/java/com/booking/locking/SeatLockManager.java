package com.booking.locking;

import com.booking.dto.websocket.SeatUpdateMessage;
import com.booking.entity.Seat;
import com.booking.entity.SeatLock;

import com.booking.enums.SeatStatus;

import com.booking.exception.SeatLockException;

import com.booking.repository.SeatLockRepository;
import com.booking.repository.SeatRepository;
import com.booking.service.websocket.SeatBroadcastService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatLockManager {

    private final RedisDistributedLockService redisDistributedLockService;
    private final SeatRepository seatRepository;
    private final SeatLockRepository seatLockRepository;
    private final SeatBroadcastService seatBroadcastService; 

    private static final long TTL_MINUTES = 5;

    public SeatLockManager(
            RedisDistributedLockService redisDistributedLockService,
            SeatRepository seatRepository,
            SeatLockRepository seatLockRepository,
            SeatBroadcastService seatBroadcastService) { 

        this.redisDistributedLockService = redisDistributedLockService;
        this.seatRepository = seatRepository;
        this.seatLockRepository = seatLockRepository;
        this.seatBroadcastService = seatBroadcastService; 
    }

    /*
     * Lock a seat: validate, acquire Redis lock, persist LOCKED status,
     * and create a SeatLock DB row so the cleanup scheduler has data to work with.
     */
    @Transactional
    public void lockSeat(Long showId, Long seatId, Long userId) {

        // ✅ This line declares 'seat' — must be present!
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatLockException("Seat not found: " + seatId));

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new SeatLockException("Seat already booked: " + seatId);
        }

        // Check if THIS user already owns the Redis lock
        Object lockOwner = redisDistributedLockService.getLockOwner(showId, seatId);
        if (lockOwner != null) {
            Long ownerUserId;

            if (lockOwner instanceof Number) {
                ownerUserId = ((Number) lockOwner).longValue();
            } else if (lockOwner instanceof List) {
                ownerUserId = Long.valueOf(
                    ((List<?>) lockOwner).get(1).toString()
                );
            } else {
                ownerUserId = Long.valueOf(lockOwner.toString());
            }

            if (ownerUserId.equals(userId)) {
                return; // Same user — silently succeed
            }
            throw new SeatLockException("Seat is already locked: " + seatId);
        }

        boolean success = redisDistributedLockService.lockSeat(showId, seatId, userId);
        if (!success) {
            throw new SeatLockException("Seat is already locked: " + seatId);
        }

        seat.setStatus(SeatStatus.LOCKED);
        seatRepository.save(seat);

        // Remove any stale rows (e.g. from a previous crashed session) before inserting
        seatLockRepository.deleteAllByShowIdAndSeatId(showId, seatId);

        SeatLock lock = new SeatLock();
        lock.setShowId(showId);
        lock.setSeatId(seatId);
        lock.setUserId(userId);
        lock.setLockedAt(LocalDateTime.now());
        lock.setExpiresAt(LocalDateTime.now().plusMinutes(TTL_MINUTES));
        seatLockRepository.save(lock);

        // ✅ Broadcast to other users
        SeatUpdateMessage msg = new SeatUpdateMessage();
        msg.setShowId(showId);
        msg.setSeatId(seatId);
        msg.setStatus("LOCKED");
        seatBroadcastService.broadcastSeatUpdate(msg);
    }
    
    @Transactional
    public void unlockSeat(Long showId, Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatLockException("Seat not found: " + seatId));

        redisDistributedLockService.unlockSeat(showId, seatId);
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);

        seatLockRepository.deleteAllByShowIdAndSeatId(showId, seatId);

        // ✅ BROADCAST HERE — so other users see the seat free up
        SeatUpdateMessage msg = new SeatUpdateMessage();
        msg.setShowId(showId);
        msg.setSeatId(seatId);
        msg.setStatus("AVAILABLE");
        seatBroadcastService.broadcastSeatUpdate(msg);
    }

    @Transactional
    public void confirmSeat(Long showId, Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatLockException("Seat not found: " + seatId));

        redisDistributedLockService.unlockSeat(showId, seatId);
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        seatLockRepository.deleteAllByShowIdAndSeatId(showId, seatId);

        // ✅ BROADCAST HERE — mark seat as permanently booked for all users
        SeatUpdateMessage msg = new SeatUpdateMessage();
        msg.setShowId(showId);
        msg.setSeatId(seatId);
        msg.setStatus("BOOKED");
        seatBroadcastService.broadcastSeatUpdate(msg);
    }

    /*
     * Check whether a seat is currently locked in Redis.
     */
    public boolean isSeatLocked(Long showId, Long seatId) {

        return redisDistributedLockService.isSeatLocked(showId, seatId);
    }
}

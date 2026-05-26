package com.booking.locking;

import com.booking.entity.Seat;
import com.booking.enums.SeatStatus;
import com.booking.exception.SeatLockException;
import com.booking.repository.SeatRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SeatLockManager {

    private final RedisDistributedLockService  redisDistributedLockService;

    private final SeatRepository seatRepository;

    public SeatLockManager(
            RedisDistributedLockService
                    redisDistributedLockService,
            SeatRepository seatRepository) {

        this.redisDistributedLockService =
                redisDistributedLockService;

        this.seatRepository = seatRepository;
    }

    @Transactional
    public void lockSeat(
            Long showId,
            Long seatId,
            Long userId) {

        Seat seat =
                seatRepository.findById(seatId)
                        .orElseThrow(() ->
                                new SeatLockException(
                                        "Seat not found"
                                ));

        if (seat.getStatus()
                == SeatStatus.BOOKED) {

            throw new SeatLockException(
                    "Seat already booked"
            );
        }

        boolean success =
                redisDistributedLockService
                        .lockSeat(
                                showId,
                                seatId,
                                userId
                        );

        if (!success) {

            throw new SeatLockException(
                    "Seat already locked"
            );
        }

        seat.setStatus(SeatStatus.LOCKED);

        seatRepository.save(seat);
    }

    @Transactional
    public void unlockSeat(
            Long showId,
            Long seatId) {

        Seat seat =
                seatRepository.findById(seatId)
                        .orElseThrow(() ->
                                new SeatLockException(
                                        "Seat not found"
                                ));

        redisDistributedLockService
                .unlockSeat(showId, seatId);

        seat.setStatus(SeatStatus.AVAILABLE);

        seatRepository.save(seat);
    }
}

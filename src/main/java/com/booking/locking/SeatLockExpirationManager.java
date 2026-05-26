package com.booking.locking;

import com.booking.entity.Seat;
import com.booking.enums.SeatStatus;
import com.booking.repository.SeatRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatLockExpirationManager {

    private final SeatRepository seatRepository;

    private final RedisDistributedLockService
            redisDistributedLockService;

    public SeatLockExpirationManager(
            SeatRepository seatRepository,
            RedisDistributedLockService redisDistributedLockService) {

        this.seatRepository = seatRepository;
        this.redisDistributedLockService =
                redisDistributedLockService;
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredLocks() {

        List<Seat> lockedSeats =
                seatRepository.findByStatus(
                        SeatStatus.LOCKED
                );

        for (Seat seat : lockedSeats) {

            boolean locked =
                    redisDistributedLockService
                            .isSeatLocked(
                                    seat.getShow().getId(),
                                    seat.getId()
                            );

            if (!locked) {

                seat.setStatus(
                        SeatStatus.AVAILABLE
                );

                seatRepository.save(seat);
            }
        }
    }
}

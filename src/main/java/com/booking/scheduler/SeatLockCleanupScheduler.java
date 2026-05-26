package com.booking.scheduler;

import com.booking.entity.Seat;
import com.booking.entity.SeatLock;

import com.booking.enums.SeatStatus;

import com.booking.repository.SeatLockRepository;
import com.booking.repository.SeatRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatLockCleanupScheduler {

    private final SeatLockRepository seatLockRepository;

    private final SeatRepository  seatRepository;

    public SeatLockCleanupScheduler( SeatLockRepository seatLockRepository, SeatRepository seatRepository) {

        this.seatLockRepository =seatLockRepository;

        this.seatRepository =seatRepository;
    }

    /*
     * Runs every 1 minute
     *
     * Releases expired seat locks
     */
    
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredSeatLocks() {

        List<SeatLock> expiredLocks =
                seatLockRepository
                        .findByExpiresAtBefore(
                                LocalDateTime.now()
                        );

        for (SeatLock seatLock
                : expiredLocks) {

            Seat seat =
                    seatRepository.findById(
                                    seatLock.getSeatId()
                            )
                            .orElse(null);

            if (seat != null) {

                seat.setStatus(
                        SeatStatus.AVAILABLE
                );

                seatRepository.save(seat);
            }

            seatLockRepository.delete(seatLock);
        }
    }
}

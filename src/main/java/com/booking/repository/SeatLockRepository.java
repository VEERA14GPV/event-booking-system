package com.booking.repository;

import com.booking.entity.SeatLock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface SeatLockRepository  extends JpaRepository<SeatLock, Long> {

    Optional<SeatLock> findByShowIdAndSeatId(
            Long showId,
            Long seatId
    );

    List<SeatLock> findByExpiresAtBefore(
            LocalDateTime time
    );

    void deleteByShowIdAndSeatId(
            Long showId,
            Long seatId
    );
}

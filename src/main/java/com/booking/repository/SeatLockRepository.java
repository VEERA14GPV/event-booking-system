package com.booking.repository;

import com.booking.entity.SeatLock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatLockRepository  extends JpaRepository<SeatLock, Long> {

    Optional<SeatLock> findByShowIdAndSeatId(
            Long showId,
            Long seatId
    );

    List<SeatLock> findByExpiresAtBefore(LocalDateTime time);

    @Transactional
    void deleteAllByShowIdAndSeatId(Long showId, Long seatId);

    @Transactional
    void deleteAllByShowId(Long showId);

}

package com.booking.repository;

import com.booking.entity.SeatLock;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatLockRepository  extends JpaRepository<SeatLock, Long> {

    Optional<SeatLock> findByShowIdAndSeatId(
            Long showId,
            Long seatId
    );

}

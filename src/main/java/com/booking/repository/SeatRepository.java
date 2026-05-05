package com.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShowId(Long showId);
}

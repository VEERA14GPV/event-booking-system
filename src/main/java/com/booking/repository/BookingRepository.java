package com.booking.repository;

import com.booking.entity.Booking;
import com.booking.enums.BookingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<Booking> findByShowId(Long showId);

    List<Booking> findByStatus(BookingStatus status);
}

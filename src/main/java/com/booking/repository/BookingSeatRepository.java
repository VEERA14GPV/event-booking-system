package com.booking.repository;

import com.booking.entity.BookingSeat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(Long bookingId);

    @Transactional
    void deleteAllByBookingId(Long bookingId);

}
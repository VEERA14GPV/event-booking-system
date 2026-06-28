package com.booking.repository;

import com.booking.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Transactional
    void deleteAllByBookingId(Long bookingId);

}
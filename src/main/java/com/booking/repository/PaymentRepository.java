package com.booking.repository;

import com.booking.entity.Payment;

import com.booking.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(
            String transactionId
    );

    List<Payment> findByStatus(
            PaymentStatus status
    );

    Optional<Payment> findByBookingId(
            Long bookingId
    );
}
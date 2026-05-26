package com.booking.entity;

import com.booking.enums.PaymentStatus;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private LocalDateTime paymentTime;

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Booking getBooking() {
        return booking;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
          this.status = status;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public void setPaymentTime( LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
}
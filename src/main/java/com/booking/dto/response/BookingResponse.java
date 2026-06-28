package com.booking.dto.response;

import com.booking.enums.BookingStatus;

import java.time.LocalDateTime;

import java.util.List;

public class BookingResponse {

    private Long bookingId;

    private Long userId;

    private Long showId;

    private List<Long> seatIds;

    private Double totalAmount;

    private BookingStatus bookingStatus;

    private LocalDateTime bookedAt;

    public BookingResponse() {
    }

    public Long getBookingId() {

        return bookingId;
    }

    public void setBookingId(
            Long bookingId) {

        this.bookingId = bookingId;
    }

    public Long getUserId() {

        return userId;
    }

    public void setUserId(
            Long userId) {

        this.userId = userId;
    }

    public Long getShowId() {

        return showId;
    }

    public void setShowId(
            Long showId) {

        this.showId = showId;
    }

    public List<Long> getSeatIds() {

        return seatIds;
    }

    public void setSeatIds(
            List<Long> seatIds) {

        this.seatIds = seatIds;
    }

    public Double getTotalAmount() {

        return totalAmount;
    }

    public void setTotalAmount(
            Double totalAmount) {

        this.totalAmount = totalAmount;
    }

    public BookingStatus getBookingStatus() {

        return bookingStatus;
    }

    public void setBookingStatus(
            BookingStatus bookingStatus) {

        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getBookedAt() {

        return bookedAt;
    }

    public void setBookedAt(
            LocalDateTime bookedAt) {

        this.bookedAt = bookedAt;
    }
}
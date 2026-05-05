package com.booking.dto;

import java.util.List;

public class BookingResponse {

    private Long bookingId;
    private Long userId;
    private Long showId;
    private List<Long> seatIds;
    private String status;

    public BookingResponse(Long bookingId, Long userId, Long showId,
                           List<Long> seatIds, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.showId = showId;
        this.seatIds = seatIds;
        this.status = status;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getShowId() {
        return showId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public String getStatus() {
        return status;
    }
}
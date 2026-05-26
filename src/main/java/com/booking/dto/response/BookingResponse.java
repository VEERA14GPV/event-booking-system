package com.booking.dto.response;

import java.util.List;

public class BookingResponse {

    private Long bookingId;

    private Long userId;

    private Long showId;

    private List<Long> seatIds;

    private String bookingStatus;

    public BookingResponse(
            Long bookingId,
            Long userId,
            Long showId,
            List<Long> seatIds,
            String bookingStatus) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.showId = showId;
        this.seatIds = seatIds;
        this.bookingStatus = bookingStatus;
    }

    public BookingResponse() {
    }

    public Long getBookingId() {

        return bookingId;
    }

    public void setBookingId(Long bookingId) {

        this.bookingId = bookingId;
    }

    public Long getUserId() {

        return userId;
    }

    public void setUserId(Long userId) {

        this.userId = userId;
    }

    public Long getShowId() {

        return showId;
    }

    public void setShowId(Long showId) {

        this.showId = showId;
    }

    public List<Long> getSeatIds() {

        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {

        this.seatIds = seatIds;
    }

    public String getBookingStatus() {

        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {

        this.bookingStatus = bookingStatus;
    }
}
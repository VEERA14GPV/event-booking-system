package com.booking.dto;

import java.util.List;

public class BookingRequest {

    private Long userId;
    private Long showId;
    private List<Long> seatIds;

    public Long getUserId() {
        return userId;
    }

    public Long getShowId() {
        return showId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
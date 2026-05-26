package com.booking.dto.request;

public class SeatLockRequest {

    private Long showId;

    private Long seatId;

    private Long userId;

    public SeatLockRequest() {
    }

    public Long getShowId() {
        return showId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
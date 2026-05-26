package com.booking.dto.websocket;

public class SeatUpdateMessage {

    private Long showId;

    private Long seatId;

    private String status;

    public SeatUpdateMessage() {
    }

    public Long getShowId() {
        return showId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getStatus() {
        return status;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
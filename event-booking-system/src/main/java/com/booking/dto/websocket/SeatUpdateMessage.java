package com.booking.dto.websocket;

/*
 * DTO used for sending real-time seat updates
 * through WebSocket.
 *
 * Example:
 *
 * {
 *   "showId": 1,
 *   "seatId": 101,
 *   "status": "LOCKED"
 * }
 */
public class SeatUpdateMessage {

    /*
     * Movie/Event show ID.
     */
    private Long showId;

    /*
     * Seat ID being updated.
     */
    private Long seatId;

    /*
     * Current seat status.
     *
     * Possible values:
     * - AVAILABLE
     * - LOCKED
     * - BOOKED
     */
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
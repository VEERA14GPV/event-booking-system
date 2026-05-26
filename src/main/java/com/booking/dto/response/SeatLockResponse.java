package com.booking.dto.response;

public class SeatLockResponse {

    private boolean locked;

    private String message;

    public SeatLockResponse() {
    }

    public boolean isLocked() {
        return locked;
    }

    public String getMessage() {
        return message;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
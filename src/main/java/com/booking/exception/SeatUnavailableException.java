package com.booking.exception;

public class SeatUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SeatUnavailableException(String message) {
        super(message);
    }
}
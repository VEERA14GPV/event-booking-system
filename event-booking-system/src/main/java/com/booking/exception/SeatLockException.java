package com.booking.exception;

public class SeatLockException extends RuntimeException {

	    private static final long serialVersionUID = 1L;

	public SeatLockException(String message) {
        super(message);
    }
}

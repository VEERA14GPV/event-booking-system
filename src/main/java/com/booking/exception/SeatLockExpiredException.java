package com.booking.exception;

public class SeatLockExpiredException  extends RuntimeException {

	    private static final long serialVersionUID = 1L;

	public SeatLockExpiredException(String message) {
        super(message);
    }
}
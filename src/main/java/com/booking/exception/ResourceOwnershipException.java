package com.booking.exception;

public class ResourceOwnershipException extends RuntimeException {

	    private static final long serialVersionUID = 1L;

	public ResourceOwnershipException(String message) {
        super(message);
    }
}
package com.booking.security.authorization;

import com.booking.exception.
        ResourceOwnershipException;

import org.springframework.stereotype.Component;

@Component
public class ResourceOwnershipValidator {

    public void validate(
            boolean owner) {

        if (!owner) {

            throw new ResourceOwnershipException(
                    "Access denied for this resource"
            );
        }
    }
}
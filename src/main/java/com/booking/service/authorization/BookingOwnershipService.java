package com.booking.service.authorization;

import com.booking.entity.Booking;

import com.booking.exception.ResourceOwnershipException;

import com.booking.repository.BookingRepository;

import org.springframework.stereotype.Service;

@Service
public class BookingOwnershipService {

    private final BookingRepository bookingRepository;

    public BookingOwnershipService(
            BookingRepository bookingRepository) {

        this.bookingRepository = bookingRepository;
    }

    public void validateBookingOwnership(
            Long bookingId,
            Long userId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceOwnershipException(
                                        "Booking not found"
                                )
                        );

        if (!booking.getUser().getId().equals(userId)) {

            throw new ResourceOwnershipException(
                    "You are not owner of this booking"
            );
        }
    }
}
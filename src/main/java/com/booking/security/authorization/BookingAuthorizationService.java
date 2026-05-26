package com.booking.security.authorization;

import com.booking.entity.Booking;
import com.booking.repository.BookingRepository;

import org.springframework.stereotype.Service;

@Service
public class BookingAuthorizationService {

    private final BookingRepository bookingRepository;

    public BookingAuthorizationService( BookingRepository bookingRepository) {

        this.bookingRepository =bookingRepository;
    }

    public boolean canAccessBooking(
            Long bookingId,
            Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        return booking != null && booking.getUser().getId().equals(userId);
    }
}
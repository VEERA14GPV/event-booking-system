package com.booking.scheduler;

import com.booking.entity.Booking;
import com.booking.enums.BookingStatus;
import com.booking.repository.BookingRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpiredBookingScheduler {

    private final BookingRepository  bookingRepository;

    public ExpiredBookingScheduler(
            BookingRepository bookingRepository) {

        this.bookingRepository =bookingRepository;
    }

    /*
     * Runs every 5 minutes
     *
     * Cancels stale pending bookings
     */
    
    @Scheduled(fixedRate = 300000)
    public void cleanupPendingBookings() {

        List<Booking> pendingBookings =bookingRepository.findByStatus(BookingStatus.PENDING);

        for (Booking booking: pendingBookings) {

            /*
             * In real production:
             * Check booking timeout duration
             */

            booking.setStatus(
                    BookingStatus.CANCELLED
            );

            bookingRepository.save(booking);
        }
    }
}

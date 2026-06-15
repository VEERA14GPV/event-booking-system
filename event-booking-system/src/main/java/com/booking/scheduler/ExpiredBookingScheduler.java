package com.booking.scheduler;

import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;

import com.booking.enums.BookingStatus;

import com.booking.locking.SeatLockManager;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpiredBookingScheduler {

    /*
     * Booking stays in PENDING state for only 5 minutes.
     *
     * If payment is not completed within 5 minutes,
     * scheduler will automatically cancel booking
     * and release all locked seats.
     */
    private static final long BOOKING_TIMEOUT_MINUTES = 5;

    private final BookingRepository bookingRepository;

    private final BookingSeatRepository bookingSeatRepository;

    private final SeatLockManager seatLockManager;

    public ExpiredBookingScheduler(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatLockManager seatLockManager) {

        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatLockManager = seatLockManager;
    }

    /*
     * Runs every 1 minute.
     *
     * FLOW:
     *
     * 1. Find all PENDING bookings
     * 2. Check whether booking expired (older than 5 mins)
     * 3. Unlock all seats
     * 4. Remove Redis lock
     * 5. Delete SeatLock DB row
     * 6. Mark booking as CANCELLED
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupPendingBookings() {

        LocalDateTime cutoff =
                LocalDateTime.now()
                        .minusMinutes(BOOKING_TIMEOUT_MINUTES);

        /*
         * Fetch all bookings currently in PENDING state.
         */
        List<Booking> pendingBookings =
                bookingRepository.findByStatus(
                        BookingStatus.PENDING
                );

        /*
         * Iterate through every pending booking.
         */
        for (Booking booking : pendingBookings) {

            /*
             * Skip booking if:
             *
             * 1. bookingTime is null
             * 2. booking is still within 5 minutes
             */
            if (booking.getBookingTime() == null
                    || booking.getBookingTime().isAfter(cutoff)) {

                continue;
            }

            /*
             * Fetch all seats mapped to this booking.
             */
            List<BookingSeat> bookingSeats =
                    bookingSeatRepository.findByBookingId(
                            booking.getId()
                    );

            /*
             * Unlock every seat linked to this booking.
             */
            for (BookingSeat bookingSeat : bookingSeats) {

                try {

                    /*
                     * unlockSeat() internally:
                     *
                     * 1. Removes Redis lock
                     * 2. Changes seat status to AVAILABLE
                     * 3. Deletes SeatLock DB entry
                     */
                    seatLockManager.unlockSeat(
                            booking.getShow().getId(),
                            bookingSeat.getSeat().getId()
                    );

                } catch (Exception ignored) {

                    /*
                     * Continue processing remaining seats
                     * even if one unlock operation fails.
                     */
                }
            }

            /*
             * Mark expired booking as CANCELLED.
             */
            booking.setStatus(BookingStatus.CANCELLED);

            /*
             * Save updated booking status.
             */
            bookingRepository.save(booking);
        }
    }
}


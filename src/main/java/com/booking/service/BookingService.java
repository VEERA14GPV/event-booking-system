package com.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;
import com.booking.entity.Seat;
import com.booking.entity.Show;
import com.booking.enums.BookingStatus;
import com.booking.enums.SeatStatus;
import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.ShowRepository;

@Service
public class BookingService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private ShowRepository showRepository;

    public Booking createBooking(Long userId, Long showId, List<Long> seatIds) {

        // 1. Fetch show
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        // 2. Fetch seats
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Invalid seat IDs");
        }

        // 3. Validate seat availability
        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat already booked: " + seat.getSeatNumber());
            }
        }

        // 4. Mark seats as BOOKED
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
        }
        seatRepository.saveAll(seats);

        // 5. Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setShow(show);
        booking.setStatus(BookingStatus.CONFIRMED);

        booking = bookingRepository.save(booking);

        // 6. Map booking to seats
        for (Seat seat : seats) {
            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setSeat(seat);
            bookingSeatRepository.save(bs);
        }

        return booking;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
}
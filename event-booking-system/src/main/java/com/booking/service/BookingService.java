package com.booking.service;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;

import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;
import com.booking.entity.Seat;
import com.booking.entity.Show;
import com.booking.entity.User;

import com.booking.enums.BookingStatus;

import com.booking.exception.ResourceNotFoundException;

import com.booking.locking.SeatLockManager;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.ShowRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.UserRepository;

import com.booking.service.websocket.SeatBroadcastService;

import com.booking.dto.websocket.SeatUpdateMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    private final BookingSeatRepository bookingSeatRepository;

    private final SeatRepository seatRepository;

    private final ShowRepository showRepository;

    private final UserRepository userRepository;

    private final SeatLockManager seatLockManager;

    private final SeatBroadcastService seatBroadcastService;

    public BookingService(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository,
            ShowRepository showRepository,
            UserRepository userRepository,
            SeatLockManager seatLockManager,
            SeatBroadcastService seatBroadcastService) {

        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
        this.seatLockManager = seatLockManager;
        this.seatBroadcastService = seatBroadcastService;
    }

    /*
     * Create booking — routes through SeatLockManager (validation + Redis + DB)
     */
    public BookingResponse createBooking(BookingRequest request) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        double totalAmount = 0.0;

        for (Long seatId : request.getSeatIds()) {

            /*
             * SeatLockManager validates status, acquires Redis lock, and
             * persists LOCKED status — all in one transactional call.
             */
            seatLockManager.lockSeat(show.getId(), seatId, user.getId());

            totalAmount += show.getPrice();

            /*
             * Broadcast real-time LOCKED status update via WebSocket.
             *
             *
             * FLOW:
             *
             * USER selects seat
             *        ↓
             * SeatLockManager locks seat
             *        ↓
             * Seat status becomes LOCKED
             *        ↓
             * WebSocket message created
             *        ↓
             * SeatBroadcastService sends update
             *        ↓
             * All frontend users instantly see:
             * "Seat Locked"
             */
            
            SeatUpdateMessage msg = new SeatUpdateMessage();
            msg.setShowId(show.getId());
            msg.setSeatId(seatId);
            msg.setStatus("LOCKED");
            seatBroadcastService.broadcastSeatUpdate(msg);
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        for (Long seatId : request.getSeatIds()) {

            Seat seat = seatRepository.findById(seatId).orElseThrow();

            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(savedBooking);
            bookingSeat.setSeat(seat);
            bookingSeatRepository.save(bookingSeat);
        }

        BookingResponse response = mapToResponse(savedBooking);
        response.setTotalAmount(totalAmount);
        return response;
    }

    /*
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        return mapToResponse(booking);
    }

    /*
     * Get all bookings (admin)
     */
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(Pageable pageable) {

        return bookingRepository.findAll(pageable).map(this::mapToResponse);
    }

    /*
     * Cancel booking — unlocks seats via SeatLockManager and broadcasts updates
     */
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(bookingId);

        for (BookingSeat bookingSeat : bookingSeats) {

            Seat seat = bookingSeat.getSeat();

            seatLockManager.unlockSeat(booking.getShow().getId(), seat.getId());

            SeatUpdateMessage msg = new SeatUpdateMessage();
            msg.setShowId(booking.getShow().getId());
            msg.setSeatId(seat.getId());
            msg.setStatus("AVAILABLE");
            seatBroadcastService.broadcastSeatUpdate(msg);
        }

        bookingRepository.save(booking);
    }

    /*
     * Entity -> DTO
     */
    private BookingResponse mapToResponse(Booking booking) {

        BookingResponse response = new BookingResponse();

        response.setBookingId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setShowId(booking.getShow().getId());
        response.setBookingStatus(booking.getStatus());
        response.setBookedAt(booking.getBookingTime());

        List<Long> seatIds = bookingSeatRepository.findByBookingId(booking.getId())
                .stream()
                .map(bs -> bs.getSeat().getId())
                .toList();

        response.setSeatIds(seatIds);
        response.setTotalAmount(booking.getShow().getPrice() * seatIds.size());

        return response;
    }
}

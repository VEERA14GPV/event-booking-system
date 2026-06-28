package com.booking.service;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;
import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;
import com.booking.entity.Seat;
import com.booking.entity.Show;
import com.booking.entity.User;
import com.booking.enums.BookingStatus;
import com.booking.enums.SeatStatus;
import com.booking.exception.ResourceNotFoundException;
import com.booking.locking.SeatLockManager;
import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.ShowRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.UserRepository;
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

    public BookingService(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository,
            ShowRepository showRepository,
            UserRepository userRepository,
            SeatLockManager seatLockManager) {

        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
        this.seatLockManager = seatLockManager;
    }

    public BookingResponse createBooking(BookingRequest request) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Step 1: Validate all seats are LOCKED before creating booking
        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seat not found: " + seatId));

            if (seat.getStatus() != SeatStatus.LOCKED) {
                throw new IllegalStateException(
                        "Seat " + seatId + " is not locked. Please select it again.");
            }
        }

        // Step 2: Save booking as PENDING
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // Step 3: Link seats to the booking. Seats remain LOCKED until
        // PaymentService.processPayment() confirms (or unlocks) them.
        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId).orElseThrow();

            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(savedBooking);
            bookingSeat.setSeat(seat);
            bookingSeatRepository.save(bookingSeat);
        }

        return mapToResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(bookingId);
        for (BookingSeat bookingSeat : bookingSeats) {
            Seat seat = bookingSeat.getSeat();
            // unlockSeat releases Redis lock + sets AVAILABLE + broadcasts to all users
            seatLockManager.unlockSeat(booking.getShow().getId(), seat.getId());
        }

        bookingRepository.save(booking);
    }

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

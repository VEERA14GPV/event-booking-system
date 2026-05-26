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
import com.booking.exception.SeatUnavailableException;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.ShowRepository;
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

    private final SeatLockService seatLockService;

    public BookingService(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository,
            ShowRepository showRepository,
            UserRepository userRepository,
            SeatLockService seatLockService) {

        this.bookingRepository = bookingRepository;

        this.bookingSeatRepository = bookingSeatRepository;

        this.seatRepository = seatRepository;

        this.showRepository = showRepository;

        this.userRepository = userRepository;

        this.seatLockService = seatLockService;
    }

    /*
     * Create booking
     */
    public BookingResponse createBooking(
            BookingRequest request) {

        Show show =
                showRepository.findById(
                        request.getShowId()
                )
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Show not found"
                        )
                );

        User user =
                userRepository.findById(
                        request.getUserId()
                )
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        double totalAmount = 0.0;

        for (Long seatId : request.getSeatIds()) {

            Seat seat =
                    seatRepository.findById(
                            seatId
                    )
                    .orElseThrow(() ->

                            new ResourceNotFoundException(
                                    "Seat not found"
                            )
                    );

            /*
             * Already booked
             */
            if (seat.getStatus() == SeatStatus.BOOKED) {

                throw new SeatUnavailableException(
                        "Seat already booked"
                );
            }

            /*
             * Redis lock
             */
            boolean locked =
                    seatLockService.lockSeat(

                            show.getId(),
                            seat.getId(),
                            user.getId()
                    );

            if (!locked) {

                throw new SeatUnavailableException(
                        "Seat already locked"
                );
            }

            seat.setStatus(
                    SeatStatus.LOCKED
            );

            seatRepository.save(seat);

            totalAmount += show.getPrice();
        }

        Booking booking = new Booking();

        booking.setUser(user);

        booking.setShow(show);

        booking.setStatus(
                BookingStatus.PENDING
        );

        booking.setBookingTime(
                LocalDateTime.now()
        );

        Booking savedBooking =
                bookingRepository.save(
                        booking
                );

        /*
         * Save booking seats
         */
        for (Long seatId : request.getSeatIds()) {

            Seat seat =
                    seatRepository.findById(
                            seatId
                    )
                    .orElseThrow();

            BookingSeat bookingSeat =
                    new BookingSeat();

            bookingSeat.setBooking(
                    savedBooking
            );

            bookingSeat.setSeat(
                    seat
            );

            bookingSeatRepository.save(
                    bookingSeat
            );
        }

        BookingResponse response =
                mapToResponse(
                        savedBooking
                );

        response.setTotalAmount(
                totalAmount
        );

        return response;
    }

    /*
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(
            Long bookingId) {

        Booking booking =
                bookingRepository.findById(
                        bookingId
                )
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );

        return mapToResponse(
                booking
        );
    }

    /*
     * Get all bookings
     */
    @Transactional(readOnly = true)
    public Page<BookingResponse>
    getAllBookings(
            Pageable pageable) {

        return bookingRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    /*
     * Cancel booking
     */
    public void cancelBooking(
            Long bookingId) {

        Booking booking =
                bookingRepository.findById(
                        bookingId
                )
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        List<BookingSeat> bookingSeats =
                bookingSeatRepository
                        .findByBookingId(
                                bookingId
                        );

        for (BookingSeat bookingSeat : bookingSeats) {

            Seat seat =
                    bookingSeat.getSeat();

            seat.setStatus(
                    SeatStatus.AVAILABLE
            );

            seatRepository.save(seat);

            seatLockService.unlockSeat(

                    booking.getShow()
                            .getId(),

                    seat.getId()
            );
        }

        bookingRepository.save(
                booking
        );
    }

    /*
     * Entity -> DTO
     */
    private BookingResponse mapToResponse(
            Booking booking) {

        BookingResponse response =
                new BookingResponse();

        response.setBookingId(
                booking.getId()
        );

        response.setUserId(
                booking.getUser()
                        .getId()
        );

        response.setShowId(
                booking.getShow()
                        .getId()
        );

        response.setBookingStatus(
                booking.getStatus()
        );

        response.setBookedAt(
                booking.getBookingTime()
        );

        List<Long> seatIds =

                bookingSeatRepository
                        .findByBookingId(
                                booking.getId()
                        )
                        .stream()
                        .map(bs ->

                                bs.getSeat()
                                        .getId()
                        )
                        .toList();

        response.setSeatIds(
                seatIds
        );

        response.setTotalAmount(

                booking.getShow()
                        .getPrice()

                        * seatIds.size()
        );

        return response;
    }
}
package com.booking.service;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;

import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;
import com.booking.entity.Seat;
import com.booking.entity.Show;

import com.booking.enums.BookingStatus;
import com.booking.enums.SeatStatus;

import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.SeatUnavailableException;

import com.booking.locking.RedisDistributedLockService;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.ShowRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private final SeatRepository seatRepository;

    private final ShowRepository showRepository;

    private final BookingSeatRepository bookingSeatRepository;

    private final RedisDistributedLockService lockService;

    public BookingService(
            BookingRepository bookingRepository,
            SeatRepository seatRepository,
            ShowRepository showRepository,
            BookingSeatRepository bookingSeatRepository,
            RedisDistributedLockService lockService) {

        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.lockService = lockService;
    }

    public BookingResponse createBooking(
            BookingRequest request) {

        Show show = showRepository.findById(
                request.getShowId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Show not found"
                )
        );

        for (Long seatId : request.getSeatIds()) {

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Seat not found"
                            )
                    );

            if (seat.getStatus() != SeatStatus.AVAILABLE) {

                throw new SeatUnavailableException(
                        "Seat already booked or locked"
                );
            }

            boolean locked = lockService.lockSeat(
                    seatId,
                    request.getUserId()
            );

            if (!locked) {

                throw new SeatUnavailableException(
                        "Seat is currently locked"
                );
            }

            seat.setStatus(SeatStatus.LOCKED);

            seatRepository.save(seat);
        }

        Booking booking = new Booking();

        booking.setUserId(request.getUserId());
        booking.setShow(show);
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking =
                bookingRepository.save(booking);

        for (Long seatId : request.getSeatIds()) {

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow();

            BookingSeat bookingSeat =
                    new BookingSeat();

            bookingSeat.setBooking(savedBooking);
            bookingSeat.setSeat(seat);

            bookingSeatRepository.save(bookingSeat);
        }

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getUserId(),
                show.getId(),
                request.getSeatIds(),
                savedBooking.getStatus().name()
        );
    }

    public BookingResponse getBooking(
            Long bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"
                                )
                        );

        List<Long> seatIds =
                bookingSeatRepository.findAll()
                        .stream()
                        .filter(bs ->
                                bs.getBooking()
                                        .getId()
                                        .equals(bookingId)
                        )
                        .map(bs ->
                                bs.getSeat().getId()
                        )
                        .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getShow().getId(),
                seatIds,
                booking.getStatus().name()
        );
    }
}
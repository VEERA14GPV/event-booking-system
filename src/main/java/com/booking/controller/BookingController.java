package com.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.entity.Booking;
import com.booking.service.BookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest request) {

        Booking booking = bookingService.createBooking(
                request.getUserId(),
                request.getShowId(),
                request.getSeatIds()
        );

        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getShow().getId(),
                request.getSeatIds(),
                booking.getStatus().name()
        );
    }

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }
}
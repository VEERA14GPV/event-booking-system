package com.booking.controller;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;

import com.booking.service.BookingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService
            bookingService;

    /*
     * Create booking
     */
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> createBooking( @Valid @RequestBody BookingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body( bookingService.createBooking(request));
    }

    /*
     * Get booking by ID
     */
    
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<BookingResponse>
    getBooking( @PathVariable Long bookingId) {

        return ResponseEntity.ok(bookingService.getBookingById( bookingId ) );
    }

    /*
     * Get all bookings
     */
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> getAllBookings(Pageable pageable) {

        return ResponseEntity.ok(bookingService.getAllBookings(pageable ) );
    }

    /*
     * Cancel booking
     */
    
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')" )
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {

        bookingService.cancelBooking(bookingId);

        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
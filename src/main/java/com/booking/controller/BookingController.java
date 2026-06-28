package com.booking.controller;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;

import com.booking.service.BookingService;

import com.booking.util.SecurityUtil;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final SecurityUtil securityUtil;

    /*
     * Allowed sorting fields.
     */
    private static final List<String>
            ALLOWED_SORT_FIELDS = List.of(

                    "bookingTime",

                    "status",

                    "id"
            );

    /*
     * Create booking
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse>
    createBooking(

            @Valid
            @RequestBody
            BookingRequest request) {

        Long currentUserId = securityUtil.getCurrentUserId();
        if (currentUserId != null && !currentUserId.equals(request.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity

                .status(HttpStatus.CREATED)

                .body(

                        bookingService
                                .createBooking(request)
                );
    }

    /*
     * Get my bookings (derived from JWT)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingResponse>>
    getMyBookings() {

        Long currentUserId = securityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                bookingService.getMyBookings(currentUserId)
        );
    }

    /*
     * Get booking by ID
     */
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingResponse>
    getBooking(
            @PathVariable Long bookingId) {

        BookingResponse response =

                bookingService
                        .getBookingById(bookingId);

        Long currentUserId =
                securityUtil.getCurrentUserId();

        /*
         * User can access only own booking.
         */
        if (currentUserId != null

                && !securityUtil.hasRole(
                "ROLE_ADMIN")

                && !response.getUserId()
                .equals(currentUserId)) {

            return ResponseEntity

                    .status(HttpStatus.FORBIDDEN)

                    .build();
        }

        return ResponseEntity.ok(response);
    }

    /*
     * Get all bookings
     *
     * Supports:
     * - Pagination
     * - Server-side sorting
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>>
    getAllBookings(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "bookingTime")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction) {

        /*
         * Validate sorting field.
         */
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {

            throw new IllegalArgumentException(
                    "Invalid sorting field: " + sortBy
            );
        }

        Pageable pageable =

                PageRequest.of(

                        page,

                        size,

                        Sort.by(

                                Sort.Direction.fromString(
                                        direction
                                ),

                                sortBy
                        )
                );

        return ResponseEntity.ok(

                bookingService
                        .getAllBookings(pageable)
        );
    }

    /*
     * Cancel booking
     */
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String>
    cancelBooking(

            @PathVariable Long bookingId) {

        BookingResponse booking =

                bookingService
                        .getBookingById(bookingId);

        Long currentUserId =
                securityUtil.getCurrentUserId();

        /*
         * User can cancel only own booking.
         */
        if (currentUserId != null

                && !securityUtil.hasRole(
                "ROLE_ADMIN")

                && !booking.getUserId()
                .equals(currentUserId)) {

            return ResponseEntity

                    .status(HttpStatus.FORBIDDEN)

                    .body(
                            "You are not the owner of this booking"
                    );
        }

        bookingService.cancelBooking(bookingId);

        return ResponseEntity.ok(
                "Booking cancelled successfully"
        );
    }
}

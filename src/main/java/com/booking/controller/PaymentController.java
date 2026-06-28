package com.booking.controller;

import com.booking.dto.request.PaymentRequest;

import com.booking.dto.response.BookingResponse;
import com.booking.dto.response.PaymentResponse;

import com.booking.service.BookingService;
import com.booking.service.PaymentService;
import com.booking.util.SecurityUtil;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final BookingService bookingService;

    private final SecurityUtil securityUtil;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(request);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {

        PaymentResponse response = paymentService.getPaymentById(paymentId);

        Long currentUserId = securityUtil.getCurrentUserId();
        if (currentUserId != null && !securityUtil.hasRole("ROLE_ADMIN")) {
            BookingResponse booking = bookingService.getBookingById(response.getBookingId());
            if (!booking.getUserId().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(response);
    }
}

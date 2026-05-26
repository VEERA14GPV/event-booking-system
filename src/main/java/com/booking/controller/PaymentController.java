package com.booking.controller;

import com.booking.dto.request.PaymentRequest;

import com.booking.dto.response.PaymentResponse;

import com.booking.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse>
    processPayment(
            @Valid @RequestBody
            PaymentRequest request) {

        return ResponseEntity.ok(

                paymentService.processPayment(
                        request
                )
        );
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PaymentResponse>
    getPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(

                paymentService.getPaymentById(
                        paymentId
                )
        );
    }
}
package com.booking.controller;

import com.booking.dto.request.LoginRequest;
import com.booking.dto.request.RegisterRequest;

import com.booking.dto.response.JwtResponse;

import com.booking.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /*
     * Service layer responsible for
     * authentication business logic.
     *
     * Controller should only handle
     * HTTP request/response handling.
     */
    private final AuthService authService;

    /*
     * Register API
     *
     * Endpoint:
     * POST /auth/register
     *
     * Used for:
     * - Creating new users
     * - Saving encrypted passwords
     * - Assigning roles
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(

            @Valid
            @RequestBody
            RegisterRequest request) {

        /*
         * Delegates registration logic
         * to service layer.
         */
        authService.register(request);

        /*
         * Returns HTTP 201 CREATED
         * after successful registration.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    /*
     * Login API
     *
     * Endpoint:
     * POST /auth/login
     *
     * Used for:
     * - Username/password authentication
     * - JWT token generation
     * - Returning authenticated user details
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(

            @Valid
            @RequestBody
            LoginRequest request) {

        /*
         * AuthService performs:
         * 1. Credential validation
         * 2. Authentication
         * 3. JWT generation
         */
        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}
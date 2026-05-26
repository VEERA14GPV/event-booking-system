package com.booking.service;

import com.booking.dto.request.LoginRequest;
import com.booking.dto.request.RegisterRequest;

import com.booking.dto.response.JwtResponse;

import com.booking.entity.Role;
import com.booking.entity.User;

import com.booking.repository.RoleRepository;
import com.booking.repository.UserRepository;

import com.booking.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    /*
     * Register user
     */
    public String register(
            RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername())) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        Role role = roleRepository
                .findByName(
                        request.getRole()
                )
                .orElseThrow(() ->

                        new RuntimeException(
                                "Role not found"
                        )
                );

        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully";
    }

    /*
     * Login user
     */
    public JwtResponse login(
            LoginRequest request) {

        Authentication authentication =

                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(

                                request.getUsername(),

                                request.getPassword()
                        )
                );

        String token =
                jwtTokenProvider.generateToken(
                        authentication
                );

        User user =
                userRepository.findByUsername(
                        request.getUsername()
                )
                .orElseThrow(() ->

                        new RuntimeException(
                                "User not found"
                        )
                );

        JwtResponse response =
                new JwtResponse();

        response.setToken(token);

        response.setUserId(
                user.getId()
        );

        response.setUsername(
                user.getUsername()
        );

        response.setRole(
                user.getRole()
                        .getName()
                        .name()
        );

        return response;
    }

    /*
     * Get user by username
     */
    public User getUserByUsername(
            String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->

                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    /*
     * Get user by ID
     */
    public User getUserById(
            Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->

                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    /*
     * Check username exists
     */
    public boolean existsByUsername(
            String username) {

        return userRepository
                .existsByUsername(username);
    }

    /*
     * Check email exists
     */
    public boolean existsByEmail(
            String email) {

        return userRepository
                .existsByEmail(email);
    }
}
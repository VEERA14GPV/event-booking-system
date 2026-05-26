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

    public String register(
            RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername())) {

            return "Username already exists";
        }

        if (userRepository.existsByEmail(
                request.getEmail())) {

            return "Email already exists";
        }

        Role role = roleRepository.findByName(
                request.getRole()
        ).orElseThrow(() ->
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
                ).orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        return new JwtResponse(

                token,

                user.getUsername(),

                user.getRole()
                        .getName()
                        .name()
        );
    }
}
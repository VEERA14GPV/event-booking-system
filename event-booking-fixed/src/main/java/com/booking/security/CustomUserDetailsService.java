package com.booking.security;

import com.booking.entity.User;
import com.booking.repository.UserRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    /*
     * Repository for fetching users.
     */
    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    /*
     * Core Spring Security method.
     *
     * Automatically called during:
     * - Login authentication
     * - JWT token validation
     *
     * Flow:
     *
     * Username
     * ->
     * Database lookup
     * ->
     * UserPrincipal creation
     * ->
     * Spring Security context
     */
    @Override
    public UserDetails loadUserByUsername(
            String username)

            throws UsernameNotFoundException {

        User user =
                userRepository.findByUsername(username)

                        .orElseThrow(() ->

                                new UsernameNotFoundException(
                                        "User not found"
                                ));

        /*
         * Converts application User entity
         * into Spring Security compatible object.
         */
        return UserPrincipal.create(user);
    }
}
package com.booking.security;

import com.booking.security.idempotency.IdempotencyFilter;

import com.booking.security.ratelimit.RateLimitingFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /*
     * JWT authentication filter.
     */
    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    /*
     * Rate limiting filter.
     */
    private final RateLimitingFilter
            rateLimitingFilter;

    /*
     * Idempotency filter.
     */
    private final IdempotencyFilter
            idempotencyFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                /*
                 * Disable CSRF.
                 */
                .csrf(csrf ->
                        csrf.disable()
                )

                /*
                 * Stateless authentication.
                 */
                .sessionManagement(session ->

                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Authorization rules.
                 */
                .authorizeHttpRequests(auth ->

                        auth

                                .requestMatchers(

                                        "/auth/**",

                                        "/ws/**",

                                        "/swagger-ui/**",

                                        "/swagger-ui.html",

                                        "/v3/api-docs/**",

                                        "/webjars/**"

                                ).permitAll()

                                .anyRequest()
                                .authenticated()
                )

                /*
                 * JWT filter executes first.
                 *
                 * Authenticates user and stores
                 * UserPrincipal in SecurityContext.
                 */
                .addFilterBefore(

                        jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class
                )

                /*
                 * Rate limiting executes after JWT.
                 *
                 * Uses authenticated user details
                 * from SecurityContext.
                 */
                .addFilterAfter(

                        rateLimitingFilter,

                        JwtAuthenticationFilter.class
                )

                /*
                 * Idempotency filter executes after
                 * rate limiting.
                 *
                 * Prevents duplicate POST request
                 * execution using Redis.
                 */
                .addFilterAfter(

                        idempotencyFilter,

                        RateLimitingFilter.class
                );

        return http.build();
    }

    /*
     * Authentication manager bean.
     */
    @Bean
    public AuthenticationManager
    authenticationManager(

            AuthenticationConfiguration config)

            throws Exception {

        return config
                .getAuthenticationManager();
    }

    /*
     * Password encoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
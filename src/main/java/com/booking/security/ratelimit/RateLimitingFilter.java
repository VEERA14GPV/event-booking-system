package com.booking.security.ratelimit;

import io.github.bucket4j.Bucket;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter
        extends OncePerRequestFilter {

    private final IpRateLimiterService
            ipRateLimiterService;

    public RateLimitingFilter(
            IpRateLimiterService
                    ipRateLimiterService) {

        this.ipRateLimiterService =
                ipRateLimiterService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException,
            IOException {

        String ip =
                request.getRemoteAddr();

        Bucket bucket =
                ipRateLimiterService
                        .resolveBucket(ip);

        if (bucket.tryConsume(1)) {

            filterChain.doFilter(
                    request,
                    response
            );

        } else {

            response.setStatus(429);

            response.getWriter().write(
                    "Too many requests"
            );
        }
    }
}
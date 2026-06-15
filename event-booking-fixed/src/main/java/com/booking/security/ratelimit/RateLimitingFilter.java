package com.booking.security.ratelimit;

import com.booking.security.UserPrincipal;

import io.github.bucket4j.Bucket;

import jakarta.servlet.*;

import jakarta.servlet.http.*;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * User-based rate limiting only.
 *
 * Authenticated Users:
 * - 20 requests per minute per userId
 */
@Component
public class RateLimitingFilter
        extends OncePerRequestFilter {

    /*
     * User-based rate limiter.
     */
    private final UserRateLimiterService
            userRateLimiterService;

    public RateLimitingFilter(

            UserRateLimiterService
                    userRateLimiterService) {

        this.userRateLimiterService =
                userRateLimiterService;
    }

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain)

            throws ServletException,
            IOException {

        Authentication auth =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        /*
         * Apply rate limiting only
         * for authenticated users.
         */
        if (auth != null

                && auth.isAuthenticated()

                && auth.getPrincipal()
                instanceof UserPrincipal principal) {

            /*
             * Get user bucket using userId.
             */
            Bucket bucket =

                    userRateLimiterService
                            .resolveBucket(

                                    principal.getId()
                            );

            /*
             * Consume one token.
             */
            if (!bucket.tryConsume(1)) {

                /*
                 * Request blocked.
                 */
                response.setStatus(429);

                response.getWriter()
                        .write("Too many requests");

                return;
            }
        }

        /*
         * Continue request processing.
         */
        filterChain.doFilter(
                request,
                response
        );
    }
}

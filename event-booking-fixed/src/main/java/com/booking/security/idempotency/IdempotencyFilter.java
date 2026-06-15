package com.booking.security.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.*;

import jakarta.servlet.http.*;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import java.time.Duration;

/*
 * Redis-backed idempotency filter.
 *
 * Prevents duplicate POST request execution.
 */
@Component
public class IdempotencyFilter
        extends OncePerRequestFilter {

    private final IdempotencyService
            idempotencyService;

    public IdempotencyFilter(

            IdempotencyService
                    idempotencyService,

            ObjectMapper objectMapper) {

        this.idempotencyService =
                idempotencyService;
    }
/*
 * This method is executed automatically for
 * every HTTP request because the filter is registered in:
 * 
 * com.booking.security.SecurityConfig
 * 
 * inside :
 * securityFilterChain()
 */
    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain)

            throws ServletException,
            IOException {

        /*
         * Apply idempotency
         * only for POST requests.
         */
        if (!"POST".equalsIgnoreCase(
                request.getMethod())) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        /*
         * Read idempotency key.
         */
        String idempotencyKey =

                request.getHeader(

                        IdempotencyConstants
                                .IDEMPOTENCY_HEADER
                );

        /*
         * Skip if header missing.
         */
        if (idempotencyKey == null
                || idempotencyKey.isBlank()) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        /*
         * Duplicate request detected.
         */
        if (idempotencyService.exists(
                idempotencyKey)) {

            IdempotencyResponse
                    cachedResponse =

                    idempotencyService
                            .getResponse(
                                    idempotencyKey
                            );

            response.setStatus(
                    cachedResponse.getStatus()
            );

            response.setContentType(
                    MediaType.APPLICATION_JSON_VALUE
            );

            response.getWriter()

                    .write(

                            cachedResponse
                                    .getResponseBody()
                    );

            return;
        }

        /*
         * Wrap response.
         */
        ContentCachingResponseWrapper
                responseWrapper =

                new ContentCachingResponseWrapper(
                        response
                );

        /*
         * Continue request.
         */
        filterChain.doFilter(
                request,
                responseWrapper
        );

        /*
         * Read response body.
         */
        String responseBody =

                new String(

                        responseWrapper
                                .getContentAsByteArray(),

                        response.getCharacterEncoding()
                );

        /*
         * Save successful response.
         */
        if (responseWrapper.getStatus() >= 200

                && responseWrapper.getStatus() < 300) {

            IdempotencyResponse
                    idempotencyResponse =

                    IdempotencyResponse
                            .builder()

                            .status(
                                    responseWrapper.getStatus()
                            )

                            .responseBody(
                                    responseBody
                            )

                            .build();

            /*
             * Store in Redis.
             */
            idempotencyService.saveResponse(

                    idempotencyKey,

                    idempotencyResponse,

                    Duration.ofHours(24)
            );
        }

        /*
         * Copy response back.
         */
        responseWrapper.copyBodyToResponse();
    }
}
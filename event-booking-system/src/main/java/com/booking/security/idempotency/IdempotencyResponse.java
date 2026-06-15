package com.booking.security.idempotency;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyResponse
        implements Serializable {

    private static final long
            serialVersionUID = 1L;

    /*
     * HTTP response status.
     */
    private int status;

    /*
     * Cached response body.
     */
    private String responseBody;
}
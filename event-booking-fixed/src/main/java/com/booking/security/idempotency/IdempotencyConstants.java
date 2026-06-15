package com.booking.security.idempotency;

public class IdempotencyConstants {

    /*
     * Redis key prefix.
     */
    public static final String
            IDEMPOTENCY_PREFIX =
            "idempotency:";

    /*
     * Request header name.
     */
    public static final String
            IDEMPOTENCY_HEADER =
            "X-Idempotency-Key";

    private IdempotencyConstants() {
    }
}

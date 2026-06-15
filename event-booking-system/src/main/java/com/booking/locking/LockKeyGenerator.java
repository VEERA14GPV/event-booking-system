package com.booking.locking;

public class LockKeyGenerator {

    private static final String SEAT_LOCK_PREFIX =
            "seat_lock:";

    private static final String SHOW_SEAT_LOCK_PREFIX =
            "show_seat_lock:";

    private LockKeyGenerator() {
    }

    public static String generateSeatLockKey(
            Long seatId) {

        return SEAT_LOCK_PREFIX + seatId;
    }

    public static String generateShowSeatLockKey(
            Long showId,
            Long seatId) {

        return SHOW_SEAT_LOCK_PREFIX
                + showId
                + ":"
                + seatId;
    }
}
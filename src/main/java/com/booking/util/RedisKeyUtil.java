package com.booking.util;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {

    /*
     * Seat lock key
     */
    public String seatLockKey(
            Long showId,
            Long seatId) {

        return "seat_lock:"
                + showId
                + ":"
                + seatId;
    }

    /*
     * Event cache key
     */
    public String eventKey(
            Long eventId) {

        return "event:"
                + eventId;
    }

    /*
     * Show cache key
     */
    public String showKey(
            Long showId) {

        return "show:"
                + showId;
    }

    /*
     * User session key
     */
    public String userSessionKey(
            Long userId) {

        return "user_session:"
                + userId;
    }
}
package com.booking.websocket;

public class WebSocketTopics {

    private WebSocketTopics() {

    }

    /*
     * Seat topic
     */
    public static String seatTopic(
            Long showId) {

        return "/topic/seats/"
                + showId;
    }

    /*
     * Booking topic
     */
    public static String bookingTopic(
            Long bookingId) {

        return "/topic/bookings/"
                + bookingId;
    }

    /*
     * Payment topic
     */
    public static String paymentTopic(
            Long paymentId) {

        return "/topic/payments/"
                + paymentId;
    }
}
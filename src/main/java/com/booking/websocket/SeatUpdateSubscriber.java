package com.booking.websocket;

import com.booking.dto.websocket.SeatUpdateMessage;

import org.springframework.stereotype.Component;

@Component
public class SeatUpdateSubscriber {

    /*
     * Handle incoming seat updates
     */
    public void handleSeatUpdate(
            SeatUpdateMessage message) {

        System.out.println(

                "Seat Update Received -> "

                        + "Show ID: "
                        + message.getShowId()

                        + ", Seat ID: "
                        + message.getSeatId()

                        + ", Status: "
                        + message.getStatus()
        );
    }
}
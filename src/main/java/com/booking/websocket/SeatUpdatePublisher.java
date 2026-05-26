package com.booking.websocket;

import com.booking.dto.websocket.SeatUpdateMessage;

import org.springframework.messaging.simp.
        SimpMessagingTemplate;

import org.springframework.stereotype.Component;

@Component
public class SeatUpdatePublisher {

    private final SimpMessagingTemplate
            messagingTemplate;

    public SeatUpdatePublisher(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate =
                messagingTemplate;
    }

    /*
     * Publish seat updates
     */
    public void publishSeatUpdate(
            SeatUpdateMessage message) {

        messagingTemplate.convertAndSend(

                WebSocketTopics
                        .seatTopic(
                                message.getShowId()
                        ),

                message
        );
    }
}
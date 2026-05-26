package com.booking.service.websocket;

import com.booking.dto.websocket.SeatUpdateMessage;

import org.springframework.messaging.simp.
        SimpMessagingTemplate;

import org.springframework.stereotype.Service;

@Service
public class SeatBroadcastService {

    private final SimpMessagingTemplate
            messagingTemplate;

    public SeatBroadcastService(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate =
                messagingTemplate;
    }

    public void broadcastSeatUpdate(
            SeatUpdateMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/seats/" + message.getShowId(),
                message
        );
    }
}
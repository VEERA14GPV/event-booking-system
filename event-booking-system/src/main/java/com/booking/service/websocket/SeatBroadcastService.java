package com.booking.service.websocket;

import com.booking.dto.websocket.SeatUpdateMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Service;

@Service
public class SeatBroadcastService {

    /*
     * Core Spring WebSocket messaging utility.
     *
     * Used for sending messages to subscribed clients.
     */
    private final SimpMessagingTemplate messagingTemplate;

    public SeatBroadcastService(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    /*
     * Broadcasts seat updates to all subscribed users.
     *
     * Example topic:
     *
     * /topic/seats/1
     *
     * Frontend clients subscribed to this topic
     * instantly receive updates.
     */
    public void broadcastSeatUpdate(
            SeatUpdateMessage message) {

        messagingTemplate.convertAndSend(

                /*
                 * Dynamic topic based on showId.
                 *
                 * Example:
                 * /topic/seats/5
                 */
                "/topic/seats/" + message.getShowId(),

                /*
                 * Actual payload sent to frontend.
                 */
                message
        );
    }
}
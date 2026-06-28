package com.booking.service.websocket;

import com.booking.dto.websocket.SeatUpdateMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Service;

import java.util.Map;

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
                "/topic/seats/" + message.getShowId(),
                message
        );
    }

    /*
     * Notifies all clients subscribed to /topic/show/{showId}
     * that the seat layout has been configured or updated.
     * Clients react by re-fetching seats from the REST API.
     */
    public void broadcastLayoutUpdate(Long showId) {
        messagingTemplate.convertAndSend(
                "/topic/show/" + showId,
                Map.of("showId", showId, "type", "LAYOUT_UPDATED")
        );
    }
}
package com.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
 * WebSocket configuration.
 *
 * Enables:
 * - Real-time communication
 * - STOMP messaging
 * - Seat lock updates
 * - Live booking notifications
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.websocket.path:/ws}")
    private String websocketPath;

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        /*
         * WebSocket endpoint for frontend clients.
         *
         * Example:
         *
         * spring.websocket.path=/ws
         *
         * Frontend:
         *
         * ws://localhost:8082/ws
         */
        registry.addEndpoint(websocketPath)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

        /*
         * Messages published to /topic/**
         * are broadcast to subscribers.
         */
        registry.enableSimpleBroker("/topic");

        /*
         * Client-to-server message prefix.
         *
         * Example:
         * /app/seat.lock
         */
        registry.setApplicationDestinationPrefixes("/app");
    }
}
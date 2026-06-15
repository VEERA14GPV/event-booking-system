package com.booking.config;

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

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        /*
         * WebSocket endpoint for frontend clients.
         *
         * Frontend connection:
         *
         * ws://localhost:8082/ws
         *
         * SockJS provides fallback support
         * when WebSocket is unavailable.
         */
        registry.addEndpoint("/ws")

                .setAllowedOriginPatterns("*")

                .withSockJS();
    }

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

        /*
         * Enable in-memory message broker.
         *
         * Messages published to:
         *
         * /topic/**
         *
         * are broadcast to subscribed clients.
         */
        registry.enableSimpleBroker("/topic");

        /*
         * Prefix for client-to-server messages.
         *
         * Example:
         *
         * /app/seat.lock
         */
        registry.setApplicationDestinationPrefixes("/app");
    }
}
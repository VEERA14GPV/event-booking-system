package com.booking.controller;

import com.booking.dto.websocket.SeatUpdateMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketSeatController {

    @MessageMapping("/seat.update")
    @SendTo("/topic/seats")
    public SeatUpdateMessage updateSeat(SeatUpdateMessage message) {

        return message;
        
    }
}
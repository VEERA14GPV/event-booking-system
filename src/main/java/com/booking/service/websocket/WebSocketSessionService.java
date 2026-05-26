package com.booking.service.websocket;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionService {

    private final Map<String, String>
            sessions = new ConcurrentHashMap<>();

    public void addSession(
            String sessionId,
            String username) {

        sessions.put(sessionId, username);
    }

    public void removeSession(
            String sessionId) {

        sessions.remove(sessionId);
    }

    public String getUsername(
            String sessionId) {

        return sessions.get(sessionId);
    }
}
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { WS_URL } from '../utils/constants';
import tokenService from '../auth/tokenService';

// Thin wrapper around @stomp/stompjs + sockjs-client matching
// WebSocketConfig.java:
//   - SockJS endpoint:        spring.websocket.path (default "/socket")
//   - Broker destination:     /topic/**   (registry.enableSimpleBroker)
//   - App prefix (unused by this backend today): /app
//
// KNOWN BACKEND ISSUE: SecurityConfig.java only permitAll()'s "/ws/**",
// but the SockJS endpoint is actually registered at "/socket" (per
// application.properties). Since they don't match, the SockJS HTTP
// handshake (/socket/info, /socket/.../xhr, etc.) falls through to
// ".anyRequest().authenticated()" and gets rejected before the STOMP
// CONNECT frame (which carries the Authorization header below) is ever
// reached. Until the backend aligns the matcher with the configured path
// (or vice versa), expect the handshake to fail with 401/403. This client
// is written for the intended behavior; see the final report for the fix.
//
// We still pass the JWT as a STOMP connectHeader (the standard, correct
// place for it) so it works as soon as that backend mismatch is fixed.
let client = null;

function buildClient() {
  return new Client({
    webSocketFactory: () => new SockJS(WS_URL),
    // beforeConnect runs on every (re)connect attempt, so a refreshed/changed
    // token is always picked up rather than captured once at client creation.
    beforeConnect: (c) => {
      const token = tokenService.getToken();
      c.connectHeaders = token ? { Authorization: `Bearer ${token}` } : {};
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    debug: () => {}, // silence verbose STOMP frame logging
  });
}

export function getWebSocketClient() {
  if (!client) {
    client = buildClient();
  }
  return client;
}

export function connectWebSocket({ onConnect, onDisconnect, onError } = {}) {
  const c = getWebSocketClient();

  c.onConnect = (frame) => {
    onConnect?.(frame);
  };
  c.onWebSocketClose = () => {
    onDisconnect?.();
  };
  c.onStompError = (frame) => {
    onError?.(frame);
  };

  if (!c.active) {
    c.activate();
  }
  return c;
}

export function disconnectWebSocket() {
  if (client && client.active) {
    client.deactivate();
  }
}

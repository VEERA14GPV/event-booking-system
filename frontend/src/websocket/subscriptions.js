import { getWebSocketClient } from './websocketClient';

// The backend (SeatBroadcastService.java) publishes to exactly ONE topic
// pattern: /topic/seats/{showId}, carrying a SeatUpdateMessage
// { showId, seatId, status } where status is one of
// AVAILABLE | LOCKED | BOOKED (see SeatStatus.java).
//
// It is triggered from:
//   - BookingService.createBooking  -> status "LOCKED" per seat
//   - BookingService.cancelBooking  -> status "AVAILABLE" per seat
//   - PaymentService.processPayment -> status "BOOKED" (success) or
//                                       "AVAILABLE" (failure) per seat
//
// There is NO booking-status topic, payment-status topic, or event-update
// topic anywhere in the backend (no SimpMessagingTemplate.convertAndSend
// call outside SeatBroadcastService). Booking/payment/event "real-time"
// updates requested in the spec are therefore implemented as plain REST
// responses to the action that triggered them, not as a WebSocket push —
// inventing extra topics here would silently break against the real server.
export function seatTopic(showId) {
  return `/topic/seats/${showId}`;
}

export function showTopic(showId) {
  return `/topic/show/${showId}`;
}

// Subscribes to live seat updates for a single show. Returns an
// unsubscribe function. Caller is responsible for ensuring the STOMP
// client is already connected (see useWebSocket.js).
export function subscribeToSeatUpdates(showId, onMessage) {
  const client = getWebSocketClient();

  if (!client.connected) {
    return () => {};
  }

  const subscription = client.subscribe(seatTopic(showId), (message) => {
    try {
      const payload = JSON.parse(message.body);
      onMessage(payload);
    } catch {
      // Ignore malformed frames rather than crashing the subscriber.
    }
  });

  return () => subscription.unsubscribe();
}

// Subscribes to show-level layout updates (e.g. LAYOUT_UPDATED when an
// organizer configures seats after clients are already on the booking page).
export function subscribeToShowUpdates(showId, onMessage) {
  const client = getWebSocketClient();

  if (!client.connected) {
    return () => {};
  }

  const subscription = client.subscribe(showTopic(showId), (message) => {
    try {
      onMessage(JSON.parse(message.body));
    } catch {
      // Ignore malformed frames.
    }
  });

  return () => subscription.unsubscribe();
}

import { useEffect, useRef, useState } from 'react';
import { connectWebSocket, disconnectWebSocket } from './websocketClient';
import { subscribeToSeatUpdates, subscribeToShowUpdates } from './subscriptions';
import useAuth from '../hooks/useAuth';

// Manages the STOMP/SockJS connection lifecycle and subscriptions for one show.
// Subscribes to:
//   /topic/seats/{showId}  — individual seat status changes (AVAILABLE/LOCKED/BOOKED)
//   /topic/show/{showId}   — show-level layout events (LAYOUT_UPDATED)
//
// Returns:
//   { connected, seatUpdates, layoutUpdated }
//   seatUpdates:  map of seatId -> status, merged as messages arrive
//   layoutUpdated: counter incremented each time LAYOUT_UPDATED is received;
//                  consumers can watch it with useEffect to re-fetch seat lists
export default function useWebSocket(showId) {
  const { isAuthenticated } = useAuth();
  const [connected, setConnected] = useState(false);
  const [seatUpdates, setSeatUpdates] = useState({});
  const [layoutUpdated, setLayoutUpdated] = useState(0);
  const unsubscribeSeatRef = useRef(() => {});
  const unsubscribeLayoutRef = useRef(() => {});

  useEffect(() => {
    if (!isAuthenticated || !showId) {
      return undefined;
    }

    setSeatUpdates({});

    function subscribe() {
      unsubscribeSeatRef.current = subscribeToSeatUpdates(showId, (msg) => {
        setSeatUpdates((prev) => ({ ...prev, [msg.seatId]: msg.status }));
      });
      unsubscribeLayoutRef.current = subscribeToShowUpdates(showId, () => {
        setLayoutUpdated((n) => n + 1);
      });
    }

    const client = connectWebSocket({
      onConnect: () => {
        setConnected(true);
        subscribe();
      },
      onDisconnect: () => setConnected(false),
      onError: () => setConnected(false),
    });

    // Already connected from a previous mount (e.g. fast nav) — subscribe now.
    if (client.connected) {
      setConnected(true);
      subscribe();
    }

    return () => {
      unsubscribeSeatRef.current();
      unsubscribeLayoutRef.current();
      unsubscribeSeatRef.current = () => {};
      unsubscribeLayoutRef.current = () => {};
    };
  }, [isAuthenticated, showId]);

  // Tear down the socket entirely on logout / app unmount.
  useEffect(() => {
    if (!isAuthenticated) {
      disconnectWebSocket();
      setConnected(false);
    }
  }, [isAuthenticated]);

  return { connected, seatUpdates, layoutUpdated };
}

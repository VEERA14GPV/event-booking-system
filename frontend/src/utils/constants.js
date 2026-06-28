// Vite dev server proxies API paths to the backend (see vite.config.js),
// so an empty base URL works for both `npm run dev` and a same-origin
// production deployment behind a reverse proxy. Override via .env for
// cross-origin deployments.
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

// SockJS endpoint. Must match `spring.websocket.path` in
// application.properties (currently "/socket"). See WebSocketConfig.java.
export const WS_URL = import.meta.env.VITE_WS_URL ?? '/socket';

export const TOKEN_KEY = 'eb_token';
export const USER_KEY = 'eb_user';

// Mirrors com.booking.enums.RoleType exactly.
export const ROLES = {
  USER: 'ROLE_USER',
  ORGANIZER: 'ROLE_ORGANIZER',
  ADMIN: 'ROLE_ADMIN',
};

// Mirrors com.booking.enums.EventType.
export const EVENT_TYPES = ['MOVIE', 'CONCERT', 'SPORTS', 'COMEDY'];

// Mirrors com.booking.enums.SeatStatus.
export const SEAT_STATUS = {
  AVAILABLE: 'AVAILABLE',
  LOCKED: 'LOCKED',
  BOOKED: 'BOOKED',
};

// Mirrors com.booking.enums.BookingStatus.
export const BOOKING_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  CANCELLED: 'CANCELLED',
  FAILED: 'FAILED',
};

// Mirrors com.booking.enums.PaymentStatus.
export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  SUCCESS: 'SUCCESS',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED',
};

// EventController.ALLOWED_SORT_FIELDS — sending any other value yields a 500.
export const EVENT_SORT_FIELDS = ['price', 'rating', 'name', 'createdAt', 'city'];

// BookingController.ALLOWED_SORT_FIELDS
export const BOOKING_SORT_FIELDS = ['bookingTime', 'status', 'id'];

// IdempotencyConstants.IDEMPOTENCY_HEADER — read by IdempotencyFilter for
// POST requests; prevents duplicate booking/payment submissions on retry.
export const IDEMPOTENCY_HEADER = 'X-Idempotency-Key';

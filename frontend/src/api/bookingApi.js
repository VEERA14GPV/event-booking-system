import axiosClient from '../utils/axiosClient';
import { generateIdempotencyKey } from '../utils/helpers';
import { IDEMPOTENCY_HEADER } from '../utils/constants';

// Maps 1:1 to BookingController.java (@RequestMapping("/bookings")).

// POST /bookings — requires ROLE_USER. Body: BookingRequest
// { userId, showId, seatIds[] }. userId must be sent explicitly even
// though the caller is already authenticated (backend does not derive it
// from the JWT for this endpoint). Returns 201 + BookingResponse.
export function createBooking({ userId, showId, seatIds }) {
  return axiosClient
    .post(
      '/bookings',
      { userId, showId, seatIds },
      { headers: { [IDEMPOTENCY_HEADER]: generateIdempotencyKey() } }
    )
    .then((res) => res.data);
}

// GET /bookings/{bookingId} — ROLE_USER (own booking only, 403 otherwise)
// or ROLE_ADMIN. Returns BookingResponse.
export function getBookingById(bookingId) {
  return axiosClient.get(`/bookings/${bookingId}`).then((res) => res.data);
}

// GET /bookings/my — ROLE_USER. Returns List<BookingResponse> for the
// authenticated user, derived from the JWT (no userId in the path).
export function getMyBookings() {
  return axiosClient.get('/bookings/my').then((res) => res.data);
}

// GET /bookings?page&size&sortBy&direction — ROLE_ADMIN only.
// sortBy must be one of: bookingTime, status, id (else 500).
// Returns Page<BookingResponse> across ALL users.
export function getAllBookings(params = {}) {
  return axiosClient.get('/bookings', { params }).then((res) => res.data);
}

// DELETE /bookings/{bookingId} — owner (ROLE_USER) or ROLE_ADMIN.
// Returns 200 with a plain-text body.
export function cancelBooking(bookingId) {
  return axiosClient.delete(`/bookings/${bookingId}`).then((res) => res.data);
}

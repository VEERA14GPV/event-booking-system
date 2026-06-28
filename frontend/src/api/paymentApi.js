import axiosClient from '../utils/axiosClient';
import { generateIdempotencyKey } from '../utils/helpers';
import { IDEMPOTENCY_HEADER } from '../utils/constants';

// Maps 1:1 to PaymentController.java (@RequestMapping("/payments")).
//
// Both endpoints use hasRole('USER') / hasAnyRole('USER','ADMIN') which is
// correct — Spring's hasRole() prepends "ROLE_" automatically.

// POST /payments — requires ROLE_USER. Body: PaymentRequest { bookingId, amount }.
// RazorpayService is a stub in this backend (always succeeds for amount > 0;
// no real gateway redirect/webhook is involved). Returns PaymentResponse.
export function processPayment({ bookingId, amount }) {
  return axiosClient
    .post(
      '/payments',
      { bookingId, amount },
      { headers: { [IDEMPOTENCY_HEADER]: generateIdempotencyKey() } }
    )
    .then((res) => res.data);
}

// GET /payments/{paymentId} — ROLE_USER or ROLE_ADMIN. Returns PaymentResponse.
// Note: there is no ownership check on this endpoint in PaymentController,
// unlike GET /bookings/{id} — any authenticated USER/ADMIN can read any
// payment by id if they know/guess the id.
export function getPaymentById(paymentId) {
  return axiosClient.get(`/payments/${paymentId}`).then((res) => res.data);
}

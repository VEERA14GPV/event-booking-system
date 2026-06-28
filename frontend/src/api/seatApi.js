import axiosClient from '../utils/axiosClient';

// Maps 1:1 to SeatController.java (@RequestMapping("/seats")).
//
// lockSeat/unlockSeat require ROLE_USER. SeatController uses hasRole('USER')
// which is correct — Spring's hasRole() prepends "ROLE_" automatically.

// GET /seats/show/{showId} -> List<Seat>
export function getSeatsByShow(showId) {
  return axiosClient.get(`/seats/show/${showId}`).then((res) => res.data);
}

// POST /seats/lock — body: SeatLockRequest { showId, seatId, userId }.
// Returns 200 with a plain-text body on success, or 400 with a plain-text
// error body (SeatLockException is caught inside the controller method).
export function lockSeat({ showId, seatId, userId }) {
  return axiosClient
    .post('/seats/lock', { showId, seatId, userId })
    .then((res) => res.data);
}

// DELETE /seats/unlock?showId=&seatId=
export function unlockSeat(showId, seatId) {
  return axiosClient
    .delete('/seats/unlock', { params: { showId, seatId } })
    .then((res) => res.data);
}

// GET /seats/status?showId=&seatId= -> boolean (true if currently locked in Redis)
export function isSeatLocked(showId, seatId) {
  return axiosClient
    .get('/seats/status', { params: { showId, seatId } })
    .then((res) => res.data);
}

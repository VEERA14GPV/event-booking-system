// Workaround for a real backend gap: there is no "list my bookings"
// endpoint. We remember every booking id this browser has created
// (per user) and resolve each one individually via GET /bookings/{id}.
const STORAGE_PREFIX = 'eb_my_bookings_';

function keyFor(userId) {
  return `${STORAGE_PREFIX}${userId}`;
}

export function rememberBookingId(userId, bookingId) {
  if (!userId || !bookingId) return;
  const ids = getRememberedBookingIds(userId);
  if (!ids.includes(bookingId)) {
    ids.push(bookingId);
    localStorage.setItem(keyFor(userId), JSON.stringify(ids));
  }
}

export function removeBookingId(userId, bookingId) {
  if (!userId || !bookingId) return;
  const ids = getRememberedBookingIds(userId).filter((id) => id !== bookingId);
  localStorage.setItem(keyFor(userId), JSON.stringify(ids));
}

export function getRememberedBookingIds(userId) {
  if (!userId) return [];
  const raw = localStorage.getItem(keyFor(userId));
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

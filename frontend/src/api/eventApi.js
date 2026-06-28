import axiosClient from '../utils/axiosClient';

// Maps 1:1 to EventController.java (@RequestMapping("/events")).
//
// NOTE: EventResponse never includes the organizer's user id, so the
// frontend has no way to filter "my events" client-side. OrganizerDashboard
// lists all events and relies on the backend's 403 (EventOwnershipService)
// to reject edits/deletes of events the organizer does not own.

// GET /events?page&size&sortBy&direction&city&venue&language&type&rating&price
// sortBy must be one of: price, rating, name, createdAt, city (else 500).
// Returns Page<EventResponse>.
export function getAllEvents(params = {}) {
  return axiosClient.get('/events', { params }).then((res) => res.data);
}

// GET /events/search?q&page&size — Elasticsearch full-text search across
// name/description/category/venue (falls back to MySQL LIKE if ES is down).
// No filter or sort params are supported by this endpoint.
export function searchEvents({ q, page = 0, size = 10 }) {
  return axiosClient
    .get('/events/search', { params: { q, page, size } })
    .then((res) => res.data);
}

// GET /events/{eventId} -> EventResponse (Redis-cached on the backend).
export function getEventById(eventId) {
  return axiosClient.get(`/events/${eventId}`).then((res) => res.data);
}

// POST /events — requires ROLE_ORGANIZER. Body: EventCreateRequest.
export function createEvent(payload) {
  return axiosClient.post('/events', payload).then((res) => res.data);
}

// PUT /events/{eventId} — requires ROLE_ORGANIZER + ownership. Body: EventUpdateRequest
// (note: `type` is a free-text string here, validated server-side against EventType).
export function updateEvent(eventId, payload) {
  return axiosClient.put(`/events/${eventId}`, payload).then((res) => res.data);
}

// DELETE /events/{eventId} — ROLE_ORGANIZER (own events only) or ROLE_ADMIN.
export function deleteEvent(eventId) {
  return axiosClient.delete(`/events/${eventId}`).then((res) => res.data);
}

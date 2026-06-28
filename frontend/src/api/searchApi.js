import { searchEvents } from './eventApi';

// Thin wrapper kept separate from eventApi so SearchEvents.jsx / SearchBar.jsx
// have a dedicated, single-purpose import — the backend only exposes one
// search endpoint (GET /events/search), there is no separate suggestions
// or autocomplete API. "Live suggestions" in the UI are implemented by
// debouncing calls to this same endpoint with a small page size.
export function searchEventsApi(q, page = 0, size = 10) {
  return searchEvents({ q, page, size });
}

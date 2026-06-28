# Event Booking System — Frontend

React 19 + Vite SPA built **strictly against the real Spring Boot backend** in this
repository (`src/main/java/com/booking/...`). No endpoint, field, or WebSocket topic
in this app was invented — every API call traces back to a controller method read
during analysis, and every gap/bug found in the backend is called out below and in
code comments rather than papered over.

## 1. Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                              Browser (SPA)                            │
│                                                                        │
│  main.jsx                                                             │
│   └─ BrowserRouter                                                    │
│       └─ ToastProvider           (src/components/Toast.jsx)           │
│           └─ ErrorBoundary       (src/components/ErrorBoundary.jsx)   │
│               └─ App.jsx  ── route table ──┐                          │
│                   └─ AuthProvider          │ (src/auth)               │
│                       ├─ ProtectedRoute    │ (must be authenticated)  │
│                       └─ RoleRoute         │ (must hold a role)       │
│                                             ▼                         │
│   pages/*  ──uses──▶  hooks/*  ──uses──▶  api/*  ──uses──▶ axiosClient│
│                                             │                         │
│                       websocket/* ◀── hooks/useWebSocket ─────────────┤
│                                                                        │
└───────────────────────────┬───────────────────────┬──────────────────┘
                             │ HTTPS (JWT Bearer)     │ SockJS/STOMP
                             ▼                        ▼
                  Spring Boot REST API        /socket (SockJS) → /topic/**
                  (JwtAuthenticationFilter →   (SeatBroadcastService →
                   RateLimitingFilter →         /topic/seats/{showId})
                   IdempotencyFilter)
                             │
              ┌──────────────┼───────────────┐
              ▼              ▼               ▼
            MySQL          Redis        Elasticsearch
        (bookings, etc) (locks/cache) (event search, MySQL
                                        LIKE fallback via
                                        Resilience4j)
```

**Layering inside `src/`:**

- `api/` — one file per backend controller. Pure axios calls, 1:1 with REST endpoints. No business logic.
- `auth/` — JWT session storage (`tokenService`), `AuthContext`/`AuthProvider`, `ProtectedRoute`, `RoleRoute`.
- `websocket/` — STOMP/SockJS client singleton, topic subscriptions, connection-lifecycle hook.
- `hooks/` — thin, reusable hooks (`useAuth`, `useApi`, `useWebSocket`) that pages consume.
- `components/` — presentational/shared UI (Navbar, EventCard, SeatMap, Toast, etc).
- `layouts/` — `MainLayout` (Navbar+Footer shell), `OrganizerLayout`, `AdminLayout` (each with their own nav).
- `pages/` — one component per route, composing hooks/api/components.
- `utils/` — axios instance + interceptors, constants mirrored from backend enums, formatting helpers, the `localBookings` workaround.

## 2. API mapping table

| Frontend function (`src/api/...`) | Backend endpoint | Auth required | Notes |
|---|---|---|---|
| `authApi.registerUser` | `POST /auth/register` | public | Body: `RegisterRequest{username,email,password,role}`. UI restricts `role` to USER/ORGANIZER even though backend accepts ADMIN too. |
| `authApi.loginUser` | `POST /auth/login` | public | Returns `JwtResponse{token,type,userId,username,role}`. |
| `eventApi.getAllEvents` | `GET /events` | **authenticated** | Paginated/filterable/sortable. `sortBy` ∈ `price,rating,name,createdAt,city` or 500. |
| `eventApi.searchEvents` / `searchApi.searchEventsApi` | `GET /events/search` | **authenticated** | Elasticsearch `multi_match` (name³, description, category², venue²), MySQL LIKE fallback. Only `q,page,size` — no filters/sort. |
| `eventApi.getEventById` | `GET /events/{id}` | **authenticated** | Redis-cached server-side. |
| `eventApi.createEvent` | `POST /events` | ROLE_ORGANIZER | `EventCreateRequest`. |
| `eventApi.updateEvent` | `PUT /events/{id}` | ROLE_ORGANIZER + ownership | `EventUpdateRequest` (note: `type` is a free string here, not an enum body field like create). |
| `eventApi.deleteEvent` | `DELETE /events/{id}` | ORGANIZER (own only) or ADMIN | Admin bypasses ownership check (`EventController.deleteEvent`). |
| `showApi.getAllShows` | `GET /shows` | **authenticated** | Returns raw `Show` entities (field `id`, nested `event.id` — **not** `eventId`/`showId` like the DTOs use). |
| `showApi.getShowById` | `GET /shows/{id}` | **authenticated** | No create/delete exposed — organizers cannot add showtimes via this API. |
| `seatApi.getSeatsByShow` | `GET /seats/show/{id}` | **authenticated** | Raw `Seat` entities. |
| `seatApi.lockSeat` | `POST /seats/lock` | ROLE_USER (intended) | **Backend bug**: `@PreAuthorize("hasRole('ROLE_USER')")` — Spring prepends `ROLE_`, so this checks for authority `ROLE_ROLE_USER`, which no one has. Every real user gets 403. Fix: change to `hasRole('USER')`. |
| `seatApi.unlockSeat` | `DELETE /seats/unlock` | ROLE_USER (intended) | Same bug as above. |
| `seatApi.isSeatLocked` | `GET /seats/status` | **authenticated** | Unaffected by the role bug. |
| `bookingApi.createBooking` | `POST /bookings` | ROLE_USER | Sends `X-Idempotency-Key`. `userId` must be sent explicitly in the body (not derived from JWT). |
| `bookingApi.getBookingById` | `GET /bookings/{id}` | owner (USER) or ADMIN | Manual ownership check in controller. |
| `bookingApi.getAllBookings` | `GET /bookings` | ADMIN only | All users, paginated/sortable (`bookingTime,status,id`). |
| `bookingApi.cancelBooking` | `DELETE /bookings/{id}` | owner or ADMIN | |
| `paymentApi.processPayment` | `POST /payments` | ROLE_USER (intended) | **Backend bug**, same `ROLE_ROLE_USER` issue as seat lock/unlock. `RazorpayService` is a stub — always succeeds for amount > 0, no real redirect/webhook. |
| `paymentApi.getPaymentById` | `GET /payments/{id}` | USER or ADMIN (intended) | Same role bug. Also: **no ownership check** — any authenticated USER/ADMIN can read any payment by guessing its id. |
| `actuatorApi.getHealth` | `GET /actuator/health` | **authenticated** | Actuator is not in `permitAll()`, so it needs a JWT like everything else. |
| `actuatorApi.getCircuitBreakers` | `GET /actuator/circuitbreakers` | **authenticated** | Resilience4j state for `elasticsearchSearch`/`elasticsearchIndex`. |

No `UserController` exists on the backend, so "manage users" (from the original
requirements) has no real endpoint to call — the Admin dashboard does not fabricate
one.

## 3. Route mapping table

| Route | Page | Layout | Guard |
|---|---|---|---|
| `/login` | `Login` | none | public |
| `/register` | `Register` | none | public |
| `/` | `Home` | `MainLayout` | authenticated |
| `/search` | `SearchEvents` | `MainLayout` | authenticated |
| `/events/:eventId` | `EventDetails` | `MainLayout` | authenticated |
| `/booking/:showId` | `BookingPage` | `MainLayout` | authenticated + ROLE_USER |
| `/my-bookings` | `MyBookings` | `MainLayout` | authenticated + ROLE_USER |
| `/organizer` | `OrganizerDashboard` | `OrganizerLayout` | authenticated + ROLE_ORGANIZER |
| `/organizer/events/new` | `CreateEvent` | `OrganizerLayout` | authenticated + ROLE_ORGANIZER |
| `/organizer/events/:eventId/edit` | `EditEvent` | `OrganizerLayout` | authenticated + ROLE_ORGANIZER |
| `/admin` | `AdminDashboard` | `AdminLayout` | authenticated + ROLE_ADMIN |
| `*` | `NotFound` | none | public |

`SecurityConfig.permitAll()` only covers `/auth/**, /ws/**, swagger-ui/**,
v3/api-docs/**, webjars/**` — everything else is `.anyRequest().authenticated()`.
That's why almost every route above sits behind `ProtectedRoute`, unlike a typical
"browse without an account" storefront.

## 4. WebSocket topic mapping

| Topic | Publisher | Payload | Consumed by |
|---|---|---|---|
| `/topic/seats/{showId}` | `SeatBroadcastService.broadcastSeatUpdate`, called from `BookingService.createBooking/cancelBooking` and `PaymentService.processPayment` | `SeatUpdateMessage{showId,seatId,status}` | `websocket/subscriptions.js` → `hooks/useWebSocket.js` → `BookingPage`'s `SeatMap` |

This is the **only** STOMP destination the backend ever publishes to. The original
requirements asked for live booking/payment/event-update channels too — those don't
exist server-side (no other `SimpMessagingTemplate.convertAndSend` call in the
codebase), so they are intentionally **not** implemented as WebSocket pushes; booking
and payment results are surfaced via the normal REST response instead. Inventing
extra topics here would silently break against the real server.

**Known backend bug affecting this feature:** `WebSocketConfig` registers the SockJS
endpoint at `spring.websocket.path` (`/socket`), but `SecurityConfig.permitAll()`
only whitelists `/ws/**`. Since the paths don't match, the SockJS handshake currently
falls through to `.anyRequest().authenticated()` and gets rejected before STOMP CONNECT
is reached — expect 401/403 on the handshake until the backend aligns the two paths.
This client is written for the intended behavior (`websocket/websocketClient.js` has
the full explanation in comments).

## 5. Known backend issues (found during analysis, not fixed by this frontend)

1. **Seat lock/unlock 403** — `SeatController` uses `hasRole('ROLE_USER')` instead of `hasRole('USER')`.
2. **Payment endpoints 403** — `PaymentController` has the same `hasRole('ROLE_USER')` / `hasAnyRole('ROLE_USER','ROLE_ADMIN')` bug.
3. **WebSocket handshake path mismatch** — `/socket` (configured) vs `/ws/**` (permitted).
4. **No "list my bookings" endpoint** — worked around client-side via `utils/localBookings.js` (remembers booking ids created by this browser, resolves each via the permitted `GET /bookings/{id}`). Bookings from another device won't show up; this is a real gap, not a frontend limitation.
5. **No organizer id on `EventResponse`** — Organizer Dashboard cannot filter to "my events"; it lists all events and relies on the backend's 403 ownership check on edit/delete.
6. **No show/seat-inventory creation endpoints** — `ShowService` has `createShow`/`deleteShow` but `ShowController` never exposes them; organizers cannot add showtimes or seats through this API.
7. **No `UserController`** — "manage users" from the original spec has nothing to call; omitted rather than fabricated.
8. **`PaymentController.getPaymentById` has no ownership check** — any authenticated USER/ADMIN can read any payment by id.

## 6. Installation

```bash
cd frontend
npm install
cp .env.example .env   # adjust VITE_API_BASE_URL / VITE_WS_URL if the backend isn't on localhost:8082
```

## 7. Run (development)

Backend must be running first (`docker compose up -d mysql redis elasticsearch` then
run the Spring Boot app, or `docker compose up app`), listening on port 8082.

```bash
npm run dev
```

Vite serves on `http://localhost:5173` and proxies `/auth,/events,/shows,/seats,
/bookings,/payments,/actuator,/socket` to `http://localhost:8082` (see
`vite.config.js`), so the app works with an empty `VITE_API_BASE_URL`.

## 8. Build (production)

```bash
npm run build      # outputs to frontend/dist
npm run preview    # optional: serve the production build locally
```

## 9. Docker deployment

A `frontend/Dockerfile` (multi-stage: `npm run build` → static files served by
nginx) and `frontend/nginx.conf` (SPA fallback + reverse proxy to the `app`
container for every real backend path, including `/socket` with WebSocket upgrade
headers) have been added, along with a `frontend` service in the root
`docker-compose.yml`.

```bash
# from the repository root
cp .env.example .env   # fill in DB_USER, DB_PASSWORD, DB_ROOT_PASSWORD, JWT_SECRET, RAZORPAY_*
docker compose up -d --build
```

- Backend: `http://localhost:8082`
- Frontend: `http://localhost:5173` (nginx container, port 80 internally)
- Swagger UI: `http://localhost:8082/swagger-ui.html`

The frontend container talks to `app:8082` over the `booking-net` Docker network —
the browser only ever calls the frontend's own origin, which nginx proxies through.

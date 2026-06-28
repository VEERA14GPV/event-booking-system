// Normalizes the several distinct error body shapes the backend can return
// (see GlobalExceptionHandler.java) into a single human-readable string.
//
//   - ResourceNotFoundException / IdempotencyException / IllegalArgumentException
//     / generic Exception -> ErrorResponse { status, error, message, timestamp }
//   - @Valid validation failures (MethodArgumentNotValidException) -> a raw
//     Map<fieldName, message>, e.g. { "name": "must not be blank" }
//   - RateLimitingFilter (429) -> plain text body "Too many requests"
//   - SeatController.lockSeat catches SeatLockException itself and returns
//     a plain string body via ResponseEntity.badRequest().body(message)
export function extractErrorMessage(error) {
  if (error.isRateLimited) {
    return 'Too many requests — please slow down and try again shortly.';
  }

  if (!error.response) {
    return 'Network error — unable to reach the server.';
  }

  const data = error.response.data;

  if (typeof data === 'string' && data.trim().length > 0) {
    return data;
  }

  if (data && typeof data === 'object') {
    if (typeof data.message === 'string') {
      return data.message;
    }
    // Validation error map: { field: message, ... }
    const fieldMessages = Object.values(data).filter((v) => typeof v === 'string');
    if (fieldMessages.length > 0) {
      return fieldMessages.join(', ');
    }
  }

  return `Request failed with status ${error.response.status}`;
}

export function formatCurrency(amount) {
  if (amount === null || amount === undefined) return '-';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
  }).format(amount);
}

export function formatDateTime(value) {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  });
}

export function formatDate(value) {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString('en-IN', { dateStyle: 'medium' });
}

// Generates a per-action idempotency key (see IDEMPOTENCY_HEADER) so a
// double-click or network retry on booking/payment submission cannot create
// duplicate side effects.
export function generateIdempotencyKey() {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID();
  }
  return `idem-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

export function classNames(...values) {
  return values.filter(Boolean).join(' ');
}

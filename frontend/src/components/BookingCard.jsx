import { Link } from 'react-router-dom';
import { formatCurrency, formatDateTime, classNames } from '../utils/helpers';
import { BOOKING_STATUS } from '../utils/constants';

const STATUS_CONFIG = {
  [BOOKING_STATUS.PENDING]:   { cls: 'bg-amber-100 text-amber-700 border border-amber-200',   icon: '⏳', label: 'Pending' },
  [BOOKING_STATUS.CONFIRMED]: { cls: 'bg-emerald-100 text-emerald-700 border border-emerald-200', icon: '✅', label: 'Confirmed' },
  [BOOKING_STATUS.CANCELLED]: { cls: 'bg-gray-100 text-gray-600 border border-gray-200',       icon: '✖', label: 'Cancelled' },
  [BOOKING_STATUS.FAILED]:    { cls: 'bg-red-100 text-red-700 border border-red-200',          icon: '❌', label: 'Failed' },
};

export default function BookingCard({ booking, onCancel, cancelling }) {
  const status = STATUS_CONFIG[booking.bookingStatus] ?? STATUS_CONFIG[BOOKING_STATUS.CANCELLED];
  const canCancel =
    booking.bookingStatus === BOOKING_STATUS.PENDING ||
    booking.bookingStatus === BOOKING_STATUS.CONFIRMED;

  return (
    <div className="flex flex-col rounded-2xl border border-gray-100 bg-white shadow-sm transition-shadow hover:shadow-md overflow-hidden">
      {/* Header stripe */}
      <div className="flex items-center justify-between bg-gray-50 px-4 py-3 border-b border-gray-100">
        <div>
          <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">Booking</p>
          <p className="font-bold text-gray-900">#{booking.bookingId}</p>
        </div>
        <span className={classNames('badge text-xs', status.cls)}>
          {status.icon} {status.label}
        </span>
      </div>

      {/* Body */}
      <div className="p-4 flex-1 space-y-3">
        <div className="grid grid-cols-2 gap-3 text-sm">
          <div className="rounded-xl bg-gray-50 p-3">
            <p className="text-[11px] font-semibold uppercase tracking-wide text-gray-400 mb-1">Show</p>
            <p className="font-semibold text-gray-900">#{booking.showId}</p>
          </div>
          <div className="rounded-xl bg-gray-50 p-3">
            <p className="text-[11px] font-semibold uppercase tracking-wide text-gray-400 mb-1">Total</p>
            <p className="font-bold text-brand-700">{formatCurrency(booking.totalAmount)}</p>
          </div>
        </div>

        <div className="rounded-xl bg-gray-50 p-3 text-sm">
          <p className="text-[11px] font-semibold uppercase tracking-wide text-gray-400 mb-1">Seats</p>
          <p className="font-medium text-gray-900">{booking.seatIds?.join(', ') || '—'}</p>
        </div>

        <div className="text-xs text-gray-400">
          Booked {formatDateTime(booking.bookedAt)}
        </div>
      </div>

      {/* Footer */}
      <div className="flex items-center justify-between border-t border-gray-100 px-4 py-3">
        <Link to="/" className="text-sm font-medium text-brand-600 hover:text-brand-700 hover:underline transition-colors">
          Browse more
        </Link>
        {canCancel && (
          <button
            onClick={() => onCancel(booking.bookingId)}
            disabled={cancelling}
            className="rounded-lg border border-red-200 px-3 py-1.5 text-xs font-semibold text-red-600 hover:bg-red-50 disabled:opacity-50 transition-colors"
          >
            {cancelling ? 'Cancelling…' : 'Cancel'}
          </button>
        )}
      </div>
    </div>
  );
}

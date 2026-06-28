import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useToast } from '../components/Toast';
import Spinner from '../components/Spinner';
import Pagination from '../components/Pagination';
import ConfirmModal from '../components/ConfirmModal';
import { classNames, extractErrorMessage, formatCurrency, formatDate, formatDateTime } from '../utils/helpers';
import { getAllEvents, deleteEvent } from '../api/eventApi';
import { getAllBookings, cancelBooking } from '../api/bookingApi';
import { getHealth, getCircuitBreakers } from '../api/actuatorApi';
import { BOOKING_STATUS } from '../utils/constants';

const PAGE_SIZE = 10;
const TABS = ['Events', 'Bookings', 'System Health'];

const STATUS_CONFIG = {
  [BOOKING_STATUS.PENDING]:   'bg-amber-100 text-amber-700',
  [BOOKING_STATUS.CONFIRMED]: 'bg-emerald-100 text-emerald-700',
  [BOOKING_STATUS.CANCELLED]: 'bg-gray-100 text-gray-600',
  [BOOKING_STATUS.FAILED]:    'bg-red-100 text-red-700',
};

export default function AdminDashboard() {
  const [tab, setTab] = useState(TABS[0]);

  return (
    <div className="space-y-6">
      <div className="flex gap-1 rounded-xl bg-gray-100 p-1">
        {TABS.map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={classNames(
              'flex-1 rounded-lg px-4 py-2 text-sm font-semibold transition-all',
              tab === t ? 'bg-white text-brand-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'
            )}
          >
            {t}
          </button>
        ))}
      </div>

      {tab === 'Events'        && <EventsTab />}
      {tab === 'Bookings'      && <BookingsTab />}
      {tab === 'System Health' && <HealthTab />}
    </div>
  );
}

function EventsTab() {
  const toast = useToast();
  const [page, setPage] = useState(0);
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  const load = () => {
    setLoading(true);
    getAllEvents({ page, size: PAGE_SIZE, sortBy: 'createdAt', direction: 'desc' })
      .then(setPageData)
      .catch((err) => toast.error(extractErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(load, [page]);

  const handleDelete = async () => {
    const eventId = confirmDelete;
    setConfirmDelete(null);
    setDeletingId(eventId);
    try {
      await deleteEvent(eventId);
      toast.success('Event deleted.');
      load();
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setDeletingId(null);
    }
  };

  if (loading) return <div className="flex justify-center py-16"><Spinner size="lg" /></div>;

  return (
    <>
      <ConfirmModal open={!!confirmDelete} title="Delete event"
        message="Permanently delete this event and all its showtimes?" confirmLabel="Delete" danger
        onConfirm={handleDelete} onCancel={() => setConfirmDelete(null)} />

      <div className="space-y-4">
        <div className="overflow-x-auto rounded-2xl border border-gray-100 bg-white shadow-sm">
          <table className="min-w-full divide-y divide-gray-100 text-sm">
            <thead className="bg-gray-50">
              <tr>
                {['Event', 'Category', 'Price', 'Listed', 'Actions'].map((h) => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-500">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {pageData?.content?.map((event) => (
                <tr key={event.eventId} className="hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3.5 font-semibold text-gray-900">{event.name}</td>
                  <td className="px-4 py-3.5"><span className="badge bg-brand-100 text-brand-700">{event.type}</span></td>
                  <td className="px-4 py-3.5 font-semibold text-gray-700">{formatCurrency(event.price)}</td>
                  <td className="px-4 py-3.5 text-xs text-gray-400">{formatDate(event.createdAt)}</td>
                  <td className="px-4 py-3.5">
                    <button
                      onClick={() => setConfirmDelete(event.eventId)}
                      disabled={deletingId === event.eventId}
                      className="rounded-lg border border-red-200 bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600 hover:bg-red-100 disabled:opacity-50 transition-colors"
                    >
                      {deletingId === event.eventId ? '…' : 'Delete'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {pageData?.content?.length === 0 && (
            <p className="px-4 py-12 text-center text-sm text-gray-400">No events found.</p>
          )}
        </div>
        {pageData && <Pagination page={page} totalPages={pageData.totalPages} onChange={setPage} />}
      </div>
    </>
  );
}

function BookingsTab() {
  const toast = useToast();
  const [page, setPage] = useState(0);
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [confirmCancel, setConfirmCancel] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);

  const load = () => {
    setLoading(true);
    getAllBookings({ page, size: PAGE_SIZE, sortBy: 'bookingTime', direction: 'desc' })
      .then(setPageData)
      .catch((err) => toast.error(extractErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(load, [page]);

  const handleCancel = async () => {
    const bookingId = confirmCancel;
    setConfirmCancel(null);
    setCancellingId(bookingId);
    try {
      await cancelBooking(bookingId);
      toast.success('Booking cancelled.');
      load();
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setCancellingId(null);
    }
  };

  if (loading) return <div className="flex justify-center py-16"><Spinner size="lg" /></div>;

  return (
    <>
      <ConfirmModal open={!!confirmCancel} title="Cancel booking"
        message={`Cancel booking #${confirmCancel}? The user's seats will be released.`} confirmLabel="Cancel booking" danger
        onConfirm={handleCancel} onCancel={() => setConfirmCancel(null)} />

      <div className="space-y-4">
        <div className="overflow-x-auto rounded-2xl border border-gray-100 bg-white shadow-sm">
          <table className="min-w-full divide-y divide-gray-100 text-sm">
            <thead className="bg-gray-50">
              <tr>
                {['Booking', 'User ID', 'Show ID', 'Seats', 'Total', 'Status', 'Booked at', 'Actions'].map((h) => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-500">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {pageData?.content?.map((b) => (
                <tr key={b.bookingId} className="hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3.5 font-bold text-gray-900">#{b.bookingId}</td>
                  <td className="px-4 py-3.5 text-gray-500">{b.userId}</td>
                  <td className="px-4 py-3.5 text-gray-500">{b.showId}</td>
                  <td className="px-4 py-3.5 text-gray-500 text-xs">{b.seatIds?.join(', ') || '—'}</td>
                  <td className="px-4 py-3.5 font-semibold text-gray-700">{formatCurrency(b.totalAmount)}</td>
                  <td className="px-4 py-3.5">
                    <span className={classNames('badge', STATUS_CONFIG[b.bookingStatus] ?? 'bg-gray-100 text-gray-600')}>
                      {b.bookingStatus}
                    </span>
                  </td>
                  <td className="px-4 py-3.5 text-xs text-gray-400">{formatDateTime(b.bookedAt)}</td>
                  <td className="px-4 py-3.5">
                    {(b.bookingStatus === BOOKING_STATUS.PENDING || b.bookingStatus === BOOKING_STATUS.CONFIRMED) && (
                      <button
                        onClick={() => setConfirmCancel(b.bookingId)}
                        disabled={cancellingId === b.bookingId}
                        className="rounded-lg border border-red-200 bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600 hover:bg-red-100 disabled:opacity-50 transition-colors"
                      >
                        {cancellingId === b.bookingId ? '…' : 'Cancel'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {pageData?.content?.length === 0 && (
            <p className="px-4 py-12 text-center text-sm text-gray-400">No bookings yet.</p>
          )}
        </div>
        {pageData && <Pagination page={page} totalPages={pageData.totalPages} onChange={setPage} />}
      </div>
    </>
  );
}

function HealthTab() {
  const toast = useToast();
  const [health, setHealth] = useState(null);
  const [breakers, setBreakers] = useState(null);
  const [breakerError, setBreakerError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    Promise.allSettled([getHealth(), getCircuitBreakers()]).then(([h, c]) => {
      if (h.status === 'fulfilled') setHealth(h.value);
      else toast.error(extractErrorMessage(h.reason));
      if (c.status === 'fulfilled') setBreakers(c.value);
      else setBreakerError(extractErrorMessage(c.reason));
      setLoading(false);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) return <div className="flex justify-center py-16"><Spinner size="lg" /></div>;

  const healthUp = health?.status === 'UP';

  return (
    <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
      <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
        <h2 className="mb-4 font-bold text-gray-900">Application health</h2>
        {health ? (
          <>
            <span className={classNames(
              'inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-sm font-bold',
              healthUp ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'
            )}>
              <span className={classNames('h-2 w-2 rounded-full', healthUp ? 'bg-emerald-500' : 'bg-red-500')} />
              {health.status}
            </span>
            <pre className="mt-4 max-h-72 overflow-auto rounded-xl bg-gray-50 p-4 text-xs text-gray-600 leading-relaxed">
              {JSON.stringify(health.components ?? health, null, 2)}
            </pre>
          </>
        ) : (
          <p className="text-sm text-gray-400">Unable to load health status.</p>
        )}
      </div>

      <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
        <h2 className="mb-1 font-bold text-gray-900">Circuit breakers</h2>
        <p className="mb-4 text-xs text-gray-400">Elasticsearch search / index — falls back to MySQL when OPEN.</p>
        {breakerError ? (
          <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-700">
            ⚠️ {breakerError}
          </div>
        ) : breakers ? (
          <pre className="max-h-72 overflow-auto rounded-xl bg-gray-50 p-4 text-xs text-gray-600 leading-relaxed">
            {JSON.stringify(breakers, null, 2)}
          </pre>
        ) : (
          <p className="text-sm text-gray-400">Unable to load circuit breaker state.</p>
        )}
      </div>
    </div>
  );
}

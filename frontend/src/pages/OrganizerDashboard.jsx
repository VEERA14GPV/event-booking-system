import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useToast } from '../components/Toast';
import Spinner from '../components/Spinner';
import Pagination from '../components/Pagination';
import ConfirmModal from '../components/ConfirmModal';
import { getAllEvents, deleteEvent } from '../api/eventApi';
import { extractErrorMessage, formatCurrency, formatDate } from '../utils/helpers';

const PAGE_SIZE = 10;

export default function OrganizerDashboard() {
  const toast = useToast();
  const [page, setPage] = useState(0);
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    getAllEvents({ page, size: PAGE_SIZE, sortBy: 'createdAt', direction: 'desc' })
      .then(setPageData)
      .catch((err) => setError(extractErrorMessage(err)))
      .finally(() => setLoading(false));
  }, [page]);

  useEffect(() => { load(); }, [load]);

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

  return (
    <>
      <ConfirmModal
        open={!!confirmDelete}
        title="Delete event"
        message="This will permanently delete the event and all its showtimes. This cannot be undone."
        confirmLabel="Delete"
        danger
        onConfirm={handleDelete}
        onCancel={() => setConfirmDelete(null)}
      />

      <div className="space-y-4">
        {error && (
          <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">⚠️ {error}</div>
        )}

        {loading ? (
          <div className="flex justify-center py-20"><Spinner size="lg" /></div>
        ) : (
          <div className="overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
            <table className="min-w-full divide-y divide-gray-100 text-sm">
              <thead className="bg-gray-50">
                <tr>
                  {['Event', 'Category', 'City / Venue', 'Price', 'Listed', 'Actions'].map((h) => (
                    <th key={h} className="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-500">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {pageData?.content?.map((event) => (
                  <tr key={event.eventId} className="hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3.5 font-semibold text-gray-900">{event.name}</td>
                    <td className="px-4 py-3.5">
                      <span className="badge bg-brand-100 text-brand-700">{event.type}</span>
                    </td>
                    <td className="px-4 py-3.5 text-gray-500">{event.venue}, {event.city}</td>
                    <td className="px-4 py-3.5 font-semibold text-gray-700">{formatCurrency(event.price)}</td>
                    <td className="px-4 py-3.5 text-gray-400 text-xs">{formatDate(event.createdAt)}</td>
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2">
                        <Link
                          to={`/organizer/events/${event.eventId}/edit`}
                          className="rounded-lg border border-brand-200 bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700 hover:bg-brand-100 transition-colors"
                        >
                          Edit
                        </Link>
                        <Link
                          to={`/organizer/events/${event.eventId}/shows/new`}
                          className="rounded-lg border border-emerald-200 bg-emerald-50 px-2.5 py-1 text-xs font-semibold text-emerald-700 hover:bg-emerald-100 transition-colors"
                        >
                          + Shows
                        </Link>
                        <button
                          onClick={() => setConfirmDelete(event.eventId)}
                          disabled={deletingId === event.eventId}
                          className="rounded-lg border border-red-200 bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600 hover:bg-red-100 disabled:opacity-50 transition-colors"
                        >
                          {deletingId === event.eventId ? '…' : 'Delete'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {pageData?.content?.length === 0 && (
              <div className="flex flex-col items-center justify-center py-16 text-center">
                <span className="mb-3 text-5xl">🎪</span>
                <p className="font-semibold text-gray-700">No events yet</p>
                <p className="mt-1 text-sm text-gray-400">Create your first event to get started.</p>
                <Link to="/organizer/events/new" className="btn-primary mt-4">Create event</Link>
              </div>
            )}
          </div>
        )}

        {pageData && <Pagination page={page} totalPages={pageData.totalPages} onChange={setPage} />}
      </div>
    </>
  );
}

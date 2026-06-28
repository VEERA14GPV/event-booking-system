import { useCallback, useEffect, useState } from 'react';
import { useToast } from '../components/Toast';
import Spinner from '../components/Spinner';
import BookingCard from '../components/BookingCard';
import { getMyBookings, cancelBooking } from '../api/bookingApi';
import { extractErrorMessage } from '../utils/helpers';
import { Link } from 'react-router-dom';

export default function MyBookings() {
  const toast = useToast();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    getMyBookings()
      .then((data) => {
        setBookings(data.sort((a, b) => b.bookingId - a.bookingId));
        setLoading(false);
      })
      .catch((err) => {
        setError(extractErrorMessage(err));
        setLoading(false);
      });
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleCancel = async (bookingId) => {
    setCancellingId(bookingId);
    try {
      await cancelBooking(bookingId);
      toast.success('Booking cancelled successfully.');
      load();
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setCancellingId(null);
    }
  };

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Bookings</h1>
          <p className="mt-1 text-sm text-gray-500">Your booking history.</p>
        </div>
        <Link to="/" className="btn-secondary text-sm">Browse events</Link>
      </div>

      {error && (
        <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-700">
          ⚠️ {error}
        </div>
      )}

      {bookings.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-gray-200 py-20 text-center">
          <span className="mb-4 text-6xl">🎫</span>
          <p className="text-lg font-semibold text-gray-700">No bookings yet</p>
          <p className="mt-2 text-sm text-gray-400 max-w-xs">
            Browse events and book your first show.
          </p>
          <Link to="/" className="btn-primary mt-6">Browse events</Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {bookings.map((booking) => (
            <BookingCard
              key={booking.bookingId}
              booking={booking}
              onCancel={handleCancel}
              cancelling={cancellingId === booking.bookingId}
            />
          ))}
        </div>
      )}
    </div>
  );
}

import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import useWebSocket from '../hooks/useWebSocket';
import { useToast } from '../components/Toast';
import ConfirmModal from '../components/ConfirmModal';
import Spinner from '../components/Spinner';
import SeatMap from '../components/SeatMap';
import { getShowById } from '../api/showApi';
import { getSeatsByShow, lockSeat, unlockSeat } from '../api/seatApi';
import { createBooking } from '../api/bookingApi';
import { processPayment } from '../api/paymentApi';
import { extractErrorMessage, formatCurrency, formatDateTime } from '../utils/helpers';

export default function BookingPage() {
  const { showId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const { seatUpdates, layoutUpdated } = useWebSocket(showId);

  const [show, setShow] = useState(null);
  const [seats, setSeats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedSeatIds, setSelectedSeatIds] = useState([]);
  const [lockingSeatId, setLockingSeatId] = useState(null);
  const [step, setStep] = useState('select');
  const [booking, setBooking] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [payConfirm, setPayConfirm] = useState(false);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    Promise.all([getShowById(showId), getSeatsByShow(showId)])
      .then(([showData, seatData]) => {
        if (cancelled) return;
        setShow(showData);
        setSeats(seatData);
      })
      .catch((err) => { if (!cancelled) setError(extractErrorMessage(err)); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [showId]);

  // Re-fetch seat list when the organizer broadcasts LAYOUT_UPDATED.
  useEffect(() => {
    if (layoutUpdated === 0) return;
    getSeatsByShow(showId).then(setSeats).catch(() => {});
  }, [layoutUpdated, showId]);

  const toggleSeat = async (seat) => {
    if (lockingSeatId) return;
    const isSelected = selectedSeatIds.includes(seat.id);
    setLockingSeatId(seat.id);
    try {
      if (isSelected) {
        await unlockSeat(showId, seat.id);
        setSelectedSeatIds((prev) => prev.filter((id) => id !== seat.id));
      } else {
        await lockSeat({ showId: Number(showId), seatId: seat.id, userId: user.userId });
        setSelectedSeatIds((prev) => [...prev, seat.id]);
      }
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setLockingSeatId(null);
    }
  };

  const handleConfirmBooking = async () => {
    setSubmitting(true);
    try {
      const result = await createBooking({ userId: user.userId, showId: Number(showId), seatIds: selectedSeatIds });
      setBooking(result);
      setStep('review');
      toast.success('Seats reserved — complete payment to confirm.');
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handlePay = async () => {
    setPayConfirm(false);
    setSubmitting(true);
    try {
      await processPayment({ bookingId: booking.bookingId, amount: booking.totalAmount });
      toast.success('Payment successful! Booking confirmed.');
      navigate('/my-bookings', { replace: true });
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;
  if (error) return <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>;
  if (!show) return null;

  const selectedSeats = selectedSeatIds.map((id) => seats.find((s) => s.id === id)).filter(Boolean);
  const totalPrice = (show.price ?? 0) * selectedSeatIds.length;

  return (
    <>
      <ConfirmModal
        open={payConfirm}
        title="Confirm payment"
        message={`You are about to pay ${formatCurrency(booking?.totalAmount)} for ${booking?.seatIds?.length} seat(s). This action cannot be undone.`}
        confirmLabel="Pay now"
        onConfirm={handlePay}
        onCancel={() => setPayConfirm(false)}
      />

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* Main — seat map */}
        <div className="lg:col-span-2">
          <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
            {/* Show header */}
            <div className="mb-6 flex items-start justify-between gap-3">
              <div>
                <h1 className="text-xl font-bold text-gray-900">{show.event?.name}</h1>
                <p className="mt-1 flex flex-wrap gap-3 text-sm text-gray-500">
                  <span>📅 {formatDateTime(show.startTime)}</span>
                  <span>📍 {show.event?.venue}, {show.event?.city}</span>
                </p>
              </div>
              <span className="rounded-xl bg-brand-50 px-3 py-1.5 text-sm font-bold text-brand-700 whitespace-nowrap">
                {formatCurrency(show.price)} / seat
              </span>
            </div>

            {step === 'select' ? (
              <>
                {lockingSeatId && (
                  <div className="mb-4 flex items-center gap-2 rounded-xl bg-amber-50 px-3 py-2 text-sm text-amber-700">
                    <Spinner size="sm" /> Locking seat…
                  </div>
                )}
                <SeatMap
                  seats={seats}
                  selectedSeatIds={selectedSeatIds}
                  onToggle={toggleSeat}
                  liveStatus={seatUpdates}
                />
              </>
            ) : (
              <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-5">
                <div className="flex items-center gap-2 mb-3">
                  <span className="text-xl">✅</span>
                  <h3 className="font-bold text-emerald-800">Seats reserved</h3>
                </div>
                <p className="text-sm text-emerald-700">
                  Seats: <span className="font-semibold">{selectedSeats.map((s) => s.seatNumber).join(', ')}</span>
                </p>
                <p className="mt-1 text-xs text-emerald-600">Complete payment within 5 minutes to confirm your booking.</p>
              </div>
            )}
          </div>
        </div>

        {/* Sidebar — summary */}
        <aside className="lg:col-span-1">
          <div className="sticky top-20 rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
            <h2 className="mb-4 font-bold text-gray-900">Order summary</h2>

            {step === 'select' && seats.length === 0 && (
              <div className="rounded-xl border border-amber-200 bg-amber-50 p-4 text-center">
                <span className="text-2xl">⚠️</span>
                <p className="mt-2 text-sm font-medium text-amber-700">No seats configured</p>
                <p className="mt-1 text-xs text-amber-600">The organizer has not set up seating for this show yet.</p>
              </div>
            )}

            {step === 'select' && seats.length > 0 && selectedSeatIds.length === 0 && (
              <div className="rounded-xl border border-dashed border-gray-200 py-8 text-center">
                <span className="text-3xl">🪑</span>
                <p className="mt-2 text-sm text-gray-500">Select seats to continue</p>
              </div>
            )}

            {step === 'select' && selectedSeatIds.length > 0 && (
              <>
                <div className="space-y-2 mb-4">
                  {selectedSeats.map((seat) => (
                    <div key={seat.id} className="flex items-center justify-between rounded-lg bg-gray-50 px-3 py-2 text-sm">
                      <span className="font-medium text-gray-700">Seat {seat.seatNumber}</span>
                      <span className="font-semibold text-gray-900">{formatCurrency(show.price)}</span>
                    </div>
                  ))}
                </div>
                <div className="border-t border-gray-100 pt-3 mb-4">
                  <div className="flex items-center justify-between">
                    <span className="font-semibold text-gray-700">Total</span>
                    <span className="text-lg font-bold text-brand-700">{formatCurrency(totalPrice)}</span>
                  </div>
                  <p className="mt-1 text-xs text-gray-400">{selectedSeatIds.length} seat(s) × {formatCurrency(show.price)}</p>
                </div>
                <button
                  onClick={handleConfirmBooking}
                  disabled={submitting}
                  className="btn-primary w-full justify-center"
                >
                  {submitting ? 'Reserving…' : '🎫 Reserve seats'}
                </button>
              </>
            )}

            {step === 'review' && booking && (
              <div className="space-y-4">
                <div className="rounded-xl border border-gray-100 bg-gray-50 p-4 text-sm space-y-2">
                  <div className="flex justify-between">
                    <span className="text-gray-500">Booking ID</span>
                    <span className="font-bold text-gray-900">#{booking.bookingId}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-500">Seats</span>
                    <span className="font-semibold text-gray-900">
                      {selectedSeats.map((s) => s.seatNumber).join(', ')}
                    </span>
                  </div>
                  <div className="flex justify-between border-t border-gray-200 pt-2">
                    <span className="font-bold text-gray-700">Total due</span>
                    <span className="text-lg font-extrabold text-brand-700">{formatCurrency(booking.totalAmount)}</span>
                  </div>
                </div>
                <button
                  onClick={() => setPayConfirm(true)}
                  disabled={submitting}
                  className="btn-primary w-full justify-center bg-emerald-600 hover:bg-emerald-700 shadow-emerald-200"
                >
                  {submitting ? 'Processing…' : '💳 Pay now'}
                </button>
                <p className="text-center text-xs text-gray-400">Secured by Razorpay</p>
              </div>
            )}
          </div>
        </aside>
      </div>
    </>
  );
}

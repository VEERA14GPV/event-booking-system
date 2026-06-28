import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Spinner from '../components/Spinner';
import useAuth from '../hooks/useAuth';
import { getEventById } from '../api/eventApi';
import { getShowsByEvent } from '../api/showApi';
import { extractErrorMessage, formatCurrency, formatDate, formatDateTime } from '../utils/helpers';

const CATEGORY = {
  MOVIE:   { icon: '🎬', from: 'from-violet-500', to: 'to-indigo-600' },
  CONCERT: { icon: '🎵', from: 'from-pink-500',   to: 'to-rose-600' },
  SPORTS:  { icon: '🏆', from: 'from-emerald-500',to: 'to-teal-600' },
  COMEDY:  { icon: '😄', from: 'from-amber-500',  to: 'to-orange-600' },
};

export default function EventDetails() {
  const { eventId } = useParams();
  const { isAuthenticated } = useAuth();
  const [event, setEvent] = useState(null);
  const [shows, setShows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    Promise.all([getEventById(eventId), getShowsByEvent(eventId)])
      .then(([eventData, eventShows]) => {
        if (cancelled) return;
        setEvent(eventData);
        const now = new Date();
        const upcoming = eventShows.filter((show) => new Date(show.startTime) > now);
        setShows(upcoming.sort((a, b) => new Date(a.startTime) - new Date(b.startTime)));
      })
      .catch((err) => { if (!cancelled) setError(extractErrorMessage(err)); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [eventId]);

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;
  if (error) return <ErrorBanner message={error} />;
  if (!event) return null;

  const cat = CATEGORY[event.type] ?? { icon: '🎪', from: 'from-brand-500', to: 'to-brand-700' };

  return (
    <div className="space-y-8">
      {/* Hero */}
      <div className={`relative overflow-hidden rounded-3xl bg-gradient-to-br ${cat.from} ${cat.to} p-8 text-white shadow-xl`}>
        <div className="pointer-events-none absolute -right-6 -top-6 text-[120px] opacity-10 select-none">
          {cat.icon}
        </div>
        <div className="relative">
          <span className="mb-3 inline-flex items-center gap-1.5 rounded-full bg-white/20 px-3 py-1 text-xs font-semibold uppercase tracking-wide">
            {cat.icon} {event.type}
          </span>
          <h1 className="text-3xl font-extrabold leading-tight sm:text-4xl">{event.name}</h1>
          <p className="mt-2 text-white/80">
            📍 {event.venue}, {event.city} &nbsp;·&nbsp; 🗣 {event.language}
          </p>
          <div className="mt-4 flex flex-wrap items-center gap-4">
            <span className="flex items-center gap-1 rounded-full bg-white/20 px-3 py-1 text-sm font-semibold">
              ★ {event.rating ?? '—'}
            </span>
            <span className="rounded-full bg-white/20 px-3 py-1 text-sm font-bold">
              {formatCurrency(event.price)} / seat
            </span>
            <span className="text-xs text-white/60">Listed {formatDate(event.createdAt)}</span>
          </div>
        </div>
      </div>

      {/* Description */}
      <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
        <h2 className="mb-3 text-lg font-bold text-gray-900">About this event</h2>
        <p className="text-sm leading-relaxed text-gray-600">{event.description}</p>
      </div>

      {/* Showtimes */}
      <div>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-bold text-gray-900">Upcoming showtimes</h2>
          <span className="rounded-full bg-brand-50 px-3 py-1 text-sm font-semibold text-brand-700">
            {shows.length} available
          </span>
        </div>

        {shows.length === 0 ? (
          <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-gray-200 py-16 text-center">
            <span className="mb-3 text-5xl">📅</span>
            <p className="text-base font-semibold text-gray-700">No upcoming showtimes</p>
            <p className="mt-1 text-sm text-gray-400">Check back soon for scheduled dates.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {shows.map((show) => (
              <div key={show.id} className="group flex flex-col rounded-2xl border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="font-bold text-gray-900">{formatDateTime(show.startTime)}</p>
                    <p className="mt-1 text-sm font-semibold text-brand-700">{formatCurrency(show.price)}</p>
                  </div>
                  <span className="rounded-lg bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-700">
                    Upcoming
                  </span>
                </div>
                {isAuthenticated ? (
                  <Link
                    to={`/booking/${show.id}`}
                    className="btn-primary mt-5 w-full justify-center"
                  >
                    🎫 Select seats
                  </Link>
                ) : (
                  <Link
                    to="/login"
                    state={{ from: { pathname: `/events/${eventId}` } }}
                    className="btn-secondary mt-5 w-full justify-center"
                  >
                    Login to book
                  </Link>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function ErrorBanner({ message }) {
  return (
    <div className="flex items-start gap-3 rounded-xl border border-red-200 bg-red-50 px-4 py-4 text-sm text-red-700">
      <span className="text-base">⚠️</span> {message}
    </div>
  );
}

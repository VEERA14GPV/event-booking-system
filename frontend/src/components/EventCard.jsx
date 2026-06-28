import { Link } from 'react-router-dom';
import { formatCurrency, formatDate } from '../utils/helpers';

const CATEGORY = {
  MOVIE:   { icon: '🎬', from: 'from-violet-500', to: 'to-indigo-600', badge: 'bg-violet-100 text-violet-700' },
  CONCERT: { icon: '🎵', from: 'from-pink-500',   to: 'to-rose-600',   badge: 'bg-pink-100 text-pink-700' },
  SPORTS:  { icon: '🏆', from: 'from-emerald-500',to: 'to-teal-600',   badge: 'bg-emerald-100 text-emerald-700' },
  COMEDY:  { icon: '😄', from: 'from-amber-500',  to: 'to-orange-600', badge: 'bg-amber-100 text-amber-700' },
};

const DEFAULT_CAT = { icon: '🎪', from: 'from-brand-500', to: 'to-brand-700', badge: 'bg-brand-100 text-brand-700' };

export default function EventCard({ event }) {
  const cat = CATEGORY[event.type] ?? DEFAULT_CAT;

  return (
    <Link
      to={`/events/${event.eventId}`}
      className="group flex flex-col overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm transition-all hover:-translate-y-1 hover:shadow-xl"
    >
      {/* Banner */}
      <div className={`relative flex h-36 items-center justify-center bg-gradient-to-br ${cat.from} ${cat.to}`}>
        <span className="text-5xl transition-transform group-hover:scale-110">{cat.icon}</span>
        <span className={`absolute right-3 top-3 rounded-full px-2.5 py-0.5 text-[11px] font-semibold ${cat.badge}`}>
          {event.type}
        </span>
      </div>

      {/* Content */}
      <div className="flex flex-1 flex-col gap-1 p-4">
        <h3 className="line-clamp-1 font-bold text-gray-900 group-hover:text-brand-700 transition-colors">
          {event.name}
        </h3>
        <p className="line-clamp-2 text-xs text-gray-500 leading-relaxed">{event.description}</p>

        <div className="mt-auto pt-3">
          <div className="flex items-center justify-between text-xs text-gray-500">
            <span className="flex items-center gap-1 truncate">📍 {event.venue}, {event.city}</span>
            <span className="ml-2 flex items-center gap-0.5 font-semibold text-amber-500 shrink-0">
              ★ {event.rating ?? '-'}
            </span>
          </div>
          <div className="mt-2 flex items-center justify-between">
            <span className="text-[11px] text-gray-400">{formatDate(event.createdAt)}</span>
            <span className="rounded-lg bg-brand-50 px-2.5 py-1 text-sm font-bold text-brand-700">
              {formatCurrency(event.price)}
            </span>
          </div>
        </div>
      </div>
    </Link>
  );
}

import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import SearchBar from '../components/SearchBar';
import EventCard from '../components/EventCard';
import { EventCardSkeleton } from '../components/Spinner';
import Pagination from '../components/Pagination';
import { getAllEvents } from '../api/eventApi';
import { extractErrorMessage } from '../utils/helpers';
import { EVENT_TYPES } from '../utils/constants';

const PAGE_SIZE = 8;
const TYPE_ICONS = { MOVIE: '🎬', CONCERT: '🎵', SPORTS: '🏆', COMEDY: '😄' };

export default function Home() {
  const [searchParams, setSearchParams] = useSearchParams();
  const page = Number(searchParams.get('page') ?? 0);
  const type = searchParams.get('type') ?? '';

  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    const params = { page, size: PAGE_SIZE, sortBy: 'createdAt', direction: 'desc' };
    if (type) params.type = type;

    getAllEvents(params)
      .then((data) => { if (!cancelled) setPageData(data); })
      .catch((err) => { if (!cancelled) setError(extractErrorMessage(err)); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [page, type]);

  const setFilter = (newType) => setSearchParams(newType ? { type: newType, page: '0' } : { page: '0' });
  const setPage = (p) => setSearchParams(type ? { type, page: String(p) } : { page: String(p) });

  return (
    <div className="space-y-10">
      {/* Hero */}
      <section className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-brand-700 via-brand-800 to-brand-900 px-6 py-16 text-center text-white sm:px-10">
        <div className="pointer-events-none absolute inset-0 opacity-10">
          {['🎬','🎵','🏆','😄','🎪','🎭','🎸','🏟'].map((e, i) => (
            <span key={i} className="absolute text-4xl select-none"
              style={{ left: `${10 + i * 12}%`, top: `${20 + (i % 3) * 25}%`, transform: `rotate(${i * 15}deg)` }}>
              {e}
            </span>
          ))}
        </div>
        <div className="relative">
          <span className="mb-4 inline-block rounded-full bg-white/15 px-4 py-1 text-xs font-semibold uppercase tracking-widest">
            Live seat booking
          </span>
          <h1 className="text-4xl font-extrabold leading-tight sm:text-5xl">
            Find your next<br className="hidden sm:block" /> great event
          </h1>
          <p className="mx-auto mt-4 max-w-lg text-brand-200">
            Movies, concerts, sports and comedy — search, lock your seats live, and book in seconds.
          </p>
          <div className="mx-auto mt-8 max-w-xl">
            <SearchBar placeholder="Search events, cities, venues…" />
          </div>
          <div className="mt-6 flex flex-wrap justify-center gap-2">
            {EVENT_TYPES.map((t) => (
              <button
                key={t}
                onClick={() => setFilter(t)}
                className="flex items-center gap-1.5 rounded-full bg-white/15 px-4 py-1.5 text-sm font-medium text-white hover:bg-white/25 transition-colors"
              >
                {TYPE_ICONS[t]} {t}
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* Event listing */}
      <section>
        <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">
              {type ? `${TYPE_ICONS[type] ?? ''} ${type}` : 'Latest events'}
            </h2>
            {pageData && !loading && (
              <p className="mt-0.5 text-sm text-gray-500">{pageData.totalElements ?? ''} events available</p>
            )}
          </div>

          {/* Filter chips */}
          <div className="flex flex-wrap gap-2">
            <FilterChip label="All" active={!type} onClick={() => setFilter('')} />
            {EVENT_TYPES.map((t) => (
              <FilterChip key={t} label={`${TYPE_ICONS[t]} ${t}`} active={type === t} onClick={() => setFilter(t)} />
            ))}
          </div>
        </div>

        {error && (
          <div className="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
        )}

        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {loading
            ? Array.from({ length: PAGE_SIZE }).map((_, i) => <EventCardSkeleton key={i} />)
            : pageData?.content?.map((event) => <EventCard key={event.eventId} event={event} />)}
        </div>

        {!loading && pageData?.content?.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 text-center">
            <span className="text-5xl mb-4">🔍</span>
            <p className="text-lg font-semibold text-gray-700">No events found</p>
            <p className="mt-1 text-sm text-gray-400">Try a different category or check back later.</p>
            {type && (
              <button onClick={() => setFilter('')} className="mt-4 btn-secondary">
                Clear filter
              </button>
            )}
          </div>
        )}

        {pageData && (
          <div className="mt-8">
            <Pagination page={page} totalPages={pageData.totalPages} onChange={setPage} />
          </div>
        )}
      </section>
    </div>
  );
}

function FilterChip({ label, active, onClick }) {
  return (
    <button
      onClick={onClick}
      className={`rounded-full px-4 py-1.5 text-sm font-medium transition-all ${
        active
          ? 'bg-brand-600 text-white shadow-sm shadow-brand-200'
          : 'border border-gray-200 bg-white text-gray-600 hover:border-brand-300 hover:text-brand-700'
      }`}
    >
      {label}
    </button>
  );
}

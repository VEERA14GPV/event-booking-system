import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import SearchBar from '../components/SearchBar';
import EventCard from '../components/EventCard';
import { EventCardSkeleton } from '../components/Spinner';
import Pagination from '../components/Pagination';
import { searchEventsApi } from '../api/searchApi';
import { extractErrorMessage } from '../utils/helpers';

const PAGE_SIZE = 8;

export default function SearchEvents() {
  const [searchParams, setSearchParams] = useSearchParams();
  const q = searchParams.get('q') ?? '';
  const page = Number(searchParams.get('page') ?? 0);

  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!q.trim()) { setPageData(null); return; }
    let cancelled = false;
    setLoading(true);
    setError(null);

    searchEventsApi(q, page, PAGE_SIZE)
      .then((data) => { if (!cancelled) setPageData(data); })
      .catch((err) => { if (!cancelled) setError(extractErrorMessage(err)); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [q, page]);

  const goToPage = (p) => setSearchParams({ q, page: String(p) });

  return (
    <div className="space-y-6">
      <div className="mx-auto max-w-2xl">
        <SearchBar
          initialValue={q}
          autoNavigate={false}
          onSearch={(value) => { if (value.trim()) setSearchParams({ q: value.trim(), page: '0' }); }}
          placeholder="Search events, cities, venues…"
        />
      </div>

      {!q.trim() && (
        <div className="flex flex-col items-center justify-center py-16 text-center">
          <span className="mb-4 text-6xl">🔍</span>
          <p className="text-lg font-semibold text-gray-700">Search for events</p>
          <p className="mt-2 text-sm text-gray-400">
            Search by name, city, venue, category or description.
          </p>
        </div>
      )}

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
      )}

      {q.trim() && (
        <>
          <div className="flex items-center justify-between">
            <p className="text-sm text-gray-600">
              {loading ? (
                <span className="animate-pulse">Searching…</span>
              ) : (
                <>
                  <span className="font-semibold text-gray-900">{pageData?.totalElements ?? 0}</span>
                  {' '}result{pageData?.totalElements !== 1 ? 's' : ''} for{' '}
                  <span className="font-semibold text-brand-700">"{q}"</span>
                </>
              )}
            </p>
          </div>

          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {loading
              ? Array.from({ length: PAGE_SIZE }).map((_, i) => <EventCardSkeleton key={i} />)
              : pageData?.content?.map((event) => <EventCard key={event.eventId} event={event} />)}
          </div>

          {!loading && pageData?.content?.length === 0 && (
            <div className="flex flex-col items-center justify-center py-16 text-center">
              <span className="mb-4 text-5xl">😕</span>
              <p className="text-lg font-semibold text-gray-700">No events matched</p>
              <p className="mt-1 text-sm text-gray-400">Try a different search term.</p>
            </div>
          )}

          {pageData && <Pagination page={page} totalPages={pageData.totalPages} onChange={goToPage} />}
        </>
      )}
    </div>
  );
}

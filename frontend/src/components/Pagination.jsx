import { classNames } from '../utils/helpers';

export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i).filter(
    (p) => p === 0 || p === totalPages - 1 || Math.abs(p - page) <= 1
  );

  let lastRendered = -1;

  return (
    <div className="flex items-center justify-center gap-1.5">
      <button
        onClick={() => onChange(Math.max(0, page - 1))}
        disabled={page === 0}
        className="flex h-9 items-center gap-1 rounded-lg border border-gray-200 px-3 text-sm font-medium text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40 transition-colors"
      >
        ← Prev
      </button>

      {pages.map((p) => {
        const showEllipsis = p - lastRendered > 1;
        lastRendered = p;
        return (
          <span key={p} className="flex items-center gap-1.5">
            {showEllipsis && <span className="px-1 text-sm text-gray-300">…</span>}
            <button
              onClick={() => onChange(p)}
              className={classNames(
                'h-9 min-w-[36px] rounded-lg px-2 text-sm font-medium transition-all',
                p === page
                  ? 'bg-brand-600 text-white shadow-sm shadow-brand-200'
                  : 'border border-gray-200 text-gray-600 hover:bg-gray-50'
              )}
            >
              {p + 1}
            </button>
          </span>
        );
      })}

      <button
        onClick={() => onChange(Math.min(totalPages - 1, page + 1))}
        disabled={page >= totalPages - 1}
        className="flex h-9 items-center gap-1 rounded-lg border border-gray-200 px-3 text-sm font-medium text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40 transition-colors"
      >
        Next →
      </button>
    </div>
  );
}

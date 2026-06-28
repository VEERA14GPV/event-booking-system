import { classNames } from '../utils/helpers';

const SIZES = {
  sm: 'h-4 w-4 border-2',
  md: 'h-8 w-8 border-[3px]',
  lg: 'h-12 w-12 border-[3px]',
};

export default function Spinner({ size = 'md', className = '' }) {
  return (
    <div
      role="status"
      aria-label="Loading"
      className={classNames(
        'animate-spin rounded-full border-brand-200 border-t-brand-600',
        SIZES[size] ?? SIZES.md,
        className
      )}
    />
  );
}

export function SkeletonBlock({ className = '' }) {
  return <div className={classNames('animate-pulse rounded-lg bg-gray-100', className)} />;
}

export function EventCardSkeleton() {
  return (
    <div className="overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
      <SkeletonBlock className="h-36 w-full rounded-none" />
      <div className="space-y-2.5 p-4">
        <SkeletonBlock className="h-4 w-3/4" />
        <SkeletonBlock className="h-3 w-full" />
        <SkeletonBlock className="h-3 w-2/3" />
        <div className="flex justify-between pt-1">
          <SkeletonBlock className="h-3 w-1/4" />
          <SkeletonBlock className="h-6 w-16 rounded-lg" />
        </div>
      </div>
    </div>
  );
}

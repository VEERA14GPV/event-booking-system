import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="flex min-h-[70vh] flex-col items-center justify-center text-center px-4">
      <span className="text-8xl mb-6 select-none">🎭</span>
      <h1 className="text-6xl font-extrabold text-brand-700">404</h1>
      <p className="mt-3 text-xl font-semibold text-gray-800">Page not found</p>
      <p className="mt-2 text-sm text-gray-500 max-w-sm">
        The page you&apos;re looking for doesn&apos;t exist or has been moved.
      </p>
      <div className="mt-8 flex flex-wrap justify-center gap-3">
        <Link to="/" className="btn-primary">Browse events</Link>
        <Link to="/search" className="btn-secondary">Search</Link>
      </div>
    </div>
  );
}

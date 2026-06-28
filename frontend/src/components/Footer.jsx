import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="mt-16 border-t border-gray-200 bg-white">
      <div className="mx-auto max-w-7xl px-4 py-8">
        <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
          <Link to="/" className="flex items-center gap-2 text-gray-700 hover:text-brand-700 transition-colors">
            <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-brand-600 text-sm text-white">🎟</span>
            <span className="font-bold">EventBook</span>
          </Link>
          <p className="text-sm text-gray-400">
            &copy; {new Date().getFullYear()} EventBook — Find, lock & book your seats in seconds.
          </p>
          <div className="flex gap-4 text-sm text-gray-400">
            <Link to="/search" className="hover:text-brand-600 transition-colors">Browse</Link>
            <Link to="/login" className="hover:text-brand-600 transition-colors">Login</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}

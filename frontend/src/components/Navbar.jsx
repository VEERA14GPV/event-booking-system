import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import { ROLES } from '../utils/constants';
import { classNames } from '../utils/helpers';

export default function Navbar() {
  const { isAuthenticated, user, logout, hasRole } = useAuth();
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const close = () => setOpen(false);

  const navLinkClass = ({ isActive }) =>
    classNames(
      'rounded-lg px-3 py-2 text-sm font-medium transition-all',
      isActive
        ? 'bg-white/15 text-white'
        : 'text-brand-100 hover:bg-white/10 hover:text-white'
    );

  return (
    <header className="sticky top-0 z-40 border-b border-white/10 bg-brand-800 shadow-lg backdrop-blur-sm">
      <nav className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 text-white" onClick={close}>
          <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-white/20 text-base">🎟</span>
          <span className="text-lg font-bold tracking-tight">EventBook</span>
        </Link>

        {/* Mobile hamburger */}
        <button
          className="relative flex h-9 w-9 flex-col items-center justify-center gap-1.5 rounded-lg text-white hover:bg-white/10 md:hidden"
          onClick={() => setOpen((o) => !o)}
          aria-label="Toggle menu"
        >
          <span className={classNames('block h-0.5 w-5 bg-white transition-all', open && 'translate-y-2 rotate-45')} />
          <span className={classNames('block h-0.5 w-5 bg-white transition-all', open && 'opacity-0')} />
          <span className={classNames('block h-0.5 w-5 bg-white transition-all', open && '-translate-y-2 -rotate-45')} />
        </button>

        {/* Nav links */}
        <div
          className={classNames(
            'flex-col gap-1 md:flex md:flex-row md:items-center md:gap-1',
            open
              ? 'absolute left-0 right-0 top-[57px] flex bg-brand-800 border-t border-white/10 p-4 shadow-xl md:static md:border-0 md:bg-transparent md:p-0 md:shadow-none'
              : 'hidden'
          )}
        >
          <NavLink to="/" className={navLinkClass} onClick={close} end>Home</NavLink>
          <NavLink to="/search" className={navLinkClass} onClick={close}>Search</NavLink>

          {isAuthenticated && hasRole(ROLES.USER) && (
            <NavLink to="/my-bookings" className={navLinkClass} onClick={close}>My Bookings</NavLink>
          )}
          {isAuthenticated && hasRole(ROLES.ORGANIZER) && (
            <NavLink to="/organizer" className={navLinkClass} onClick={close}>Organizer</NavLink>
          )}
          {isAuthenticated && hasRole(ROLES.ADMIN) && (
            <NavLink to="/admin" className={navLinkClass} onClick={close}>Admin</NavLink>
          )}

          {/* Auth section */}
          <div className="mt-3 flex items-center gap-2 border-t border-white/10 pt-3 md:ml-3 md:mt-0 md:border-l md:border-t-0 md:pl-3 md:pt-0">
            {isAuthenticated ? (
              <>
                <div className="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-1.5">
                  <span className="h-6 w-6 rounded-full bg-brand-400 flex items-center justify-center text-xs font-bold text-white">
                    {user?.username?.[0]?.toUpperCase()}
                  </span>
                  <span className="text-sm text-white font-medium hidden sm:block">{user?.username}</span>
                  <span className="rounded bg-brand-600 px-1.5 py-0.5 text-[10px] font-semibold uppercase text-brand-100">
                    {user?.role?.replace('ROLE_', '')}
                  </span>
                </div>
                <button
                  onClick={() => { close(); logout(); }}
                  className="rounded-lg border border-white/20 px-3 py-1.5 text-sm font-medium text-white hover:bg-white/10 transition-colors"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => { close(); navigate('/login'); }}
                  className="rounded-lg px-3 py-1.5 text-sm font-medium text-brand-100 hover:bg-white/10 hover:text-white transition-colors"
                >
                  Login
                </button>
                <button
                  onClick={() => { close(); navigate('/register'); }}
                  className="rounded-lg bg-white px-3 py-1.5 text-sm font-semibold text-brand-800 hover:bg-brand-50 transition-colors shadow-sm"
                >
                  Get started
                </button>
              </>
            )}
          </div>
        </div>
      </nav>
    </header>
  );
}

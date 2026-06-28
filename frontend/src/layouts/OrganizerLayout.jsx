import { NavLink, Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { classNames } from '../utils/helpers';

const tabClass = ({ isActive }) =>
  classNames(
    'flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold transition-all',
    isActive ? 'bg-brand-600 text-white shadow-sm' : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
  );

export default function OrganizerLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <Navbar />
      <div className="mx-auto w-full max-w-7xl flex-1 px-4 py-6">
        <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900">Organizer Console</h1>
            <p className="mt-0.5 text-sm text-gray-500">Manage your events and showtimes.</p>
          </div>
          <nav className="flex gap-2 rounded-xl border border-gray-200 bg-white p-1 shadow-sm">
            <NavLink to="/organizer" end className={tabClass}>🎪 My events</NavLink>
            <NavLink to="/organizer/events/new" className={tabClass}>+ Create event</NavLink>
          </nav>
        </div>
        <Outlet />
      </div>
      <Footer />
    </div>
  );
}

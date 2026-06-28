import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

export default function AdminLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <Navbar />
      <div className="mx-auto w-full max-w-7xl flex-1 px-4 py-6">
        <div className="mb-6">
          <h1 className="text-2xl font-extrabold text-gray-900">Admin Dashboard</h1>
          <p className="mt-0.5 text-sm text-gray-500">Manage events, bookings and monitor system health.</p>
        </div>
        <Outlet />
      </div>
      <Footer />
    </div>
  );
}

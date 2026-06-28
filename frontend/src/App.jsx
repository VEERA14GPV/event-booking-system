import { Route, Routes } from 'react-router-dom';
import AuthProvider from './auth/AuthProvider';
import ProtectedRoute from './auth/ProtectedRoute';
import RoleRoute from './auth/RoleRoute';
import MainLayout from './layouts/MainLayout';
import OrganizerLayout from './layouts/OrganizerLayout';
import AdminLayout from './layouts/AdminLayout';
import { ROLES } from './utils/constants';

import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import SearchEvents from './pages/SearchEvents';
import EventDetails from './pages/EventDetails';
import BookingPage from './pages/BookingPage';
import MyBookings from './pages/MyBookings';
import OrganizerDashboard from './pages/OrganizerDashboard';
import CreateEvent from './pages/CreateEvent';
import EditEvent from './pages/EditEvent';
import AdminDashboard from './pages/AdminDashboard';
import NotFound from './pages/NotFound';
import CreateShows from './pages/CreateShows';

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Public browsing routes — no auth required */}
        <Route element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<SearchEvents />} />
          <Route path="/events/:eventId" element={<EventDetails />} />
        </Route>

        {/* Protected user routes */}
        <Route element={<ProtectedRoute />}>
          <Route element={<MainLayout />}>
            <Route element={<RoleRoute allow={[ROLES.USER, ROLES.ORGANIZER]} />}>
              <Route path="/booking/:showId" element={<BookingPage />} />
              <Route path="/my-bookings" element={<MyBookings />} />
            </Route>
          </Route>

          <Route element={<RoleRoute allow={[ROLES.ORGANIZER]} />}>
            <Route element={<OrganizerLayout />}>
              <Route path="/organizer" element={<OrganizerDashboard />} />
              <Route path="/organizer/events/new" element={<CreateEvent />} />
              <Route path="/organizer/events/:eventId/shows/new" element={<CreateShows />} />
              <Route path="/organizer/events/:eventId/edit" element={<EditEvent />} />
            </Route>
          </Route>

          <Route element={<RoleRoute allow={[ROLES.ADMIN]} />}>
            <Route element={<AdminLayout />}>
              <Route path="/admin" element={<AdminDashboard />} />
            </Route>
          </Route>
        </Route>

        <Route path="*" element={<NotFound />} />
      </Routes>
    </AuthProvider>
  );
}

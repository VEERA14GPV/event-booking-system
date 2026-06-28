import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import Spinner from '../components/Spinner';

// Guards a route tree behind specific roles, e.g.
// <Route element={<RoleRoute allow={[ROLES.ORGANIZER]} />}>...</Route>
// Assumes it is nested under <ProtectedRoute /> so isAuthenticated is
// already guaranteed, but checks again defensively.
export default function RoleRoute({ allow = [] }) {
  const { isAuthenticated, isLoading, hasRole } = useAuth();

  if (isLoading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!hasRole(...allow)) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}

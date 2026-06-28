import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthContext from './AuthContext';
import tokenService from './tokenService';
import { loginUser, registerUser } from '../api/authApi';

export default function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const storedToken = tokenService.getToken();
    const storedUser = tokenService.getUser();
    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(storedUser);
    }
    setIsLoading(false);
  }, []);

  const logout = useCallback(
    (redirect = true) => {
      tokenService.clear();
      setToken(null);
      setUser(null);
      if (redirect) navigate('/login', { replace: true });
    },
    [navigate]
  );

  useEffect(() => {
    const handleUnauthorized = () => logout(true);
    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, [logout]);

  const login = useCallback(async (username, password) => {
    const data = await loginUser({ username, password });
    const sessionUser = { userId: data.userId, username: data.username, role: data.role };
    tokenService.setToken(data.token);
    tokenService.setUser(sessionUser);
    setToken(data.token);
    setUser(sessionUser);
    return sessionUser;
  }, []);

  const register = useCallback(async (payload) => registerUser(payload), []);

  const hasRole = useCallback((...roles) => !!user && roles.includes(user.role), [user]);

  const value = useMemo(
    () => ({ user, token, isAuthenticated: !!token && !!user, isLoading, login, register, logout, hasRole }),
    [user, token, isLoading, login, register, logout, hasRole]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

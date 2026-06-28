import axios from 'axios';
import { API_BASE_URL } from './constants';
import tokenService from '../auth/tokenService';

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach `Authorization: Bearer <token>` to every outgoing request.
// JwtAuthenticationFilter on the backend reads this exact header.
axiosClient.interceptors.request.use((config) => {
  const token = tokenService.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Global response handling:
// - 401 (invalid/expired/missing JWT) -> clear session and force re-login.
//   We dispatch a DOM event instead of importing AuthContext directly to
//   avoid a circular dependency (AuthContext also depends on this client).
// - 429 (UserRateLimiterService) -> surface as a typed error so callers/UI
//   can show "Too many requests" instead of a generic failure.
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      tokenService.clear();
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    }

    if (status === 429) {
      error.isRateLimited = true;
    }

    return Promise.reject(error);
  }
);

export default axiosClient;

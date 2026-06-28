import { createContext } from 'react';

// Shape: {
//   user: { userId, username, role } | null,
//   token: string | null,
//   isAuthenticated: boolean,
//   isLoading: boolean,            // true while restoring session on first mount
//   login: (username, password) => Promise<void>,
//   register: (payload) => Promise<void>,
//   logout: () => void,
//   hasRole: (...roles) => boolean,
// }
const AuthContext = createContext(undefined);

export default AuthContext;

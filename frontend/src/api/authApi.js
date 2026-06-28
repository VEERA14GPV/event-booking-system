import axiosClient from '../utils/axiosClient';

// Maps 1:1 to AuthController.java (@RequestMapping("/auth")).

// POST /auth/register -> 201 Created, body: plain string "User registered successfully"
// Body must match RegisterRequest: { username, email, password, role }
// `role` must be one of RoleType: ROLE_USER | ROLE_ORGANIZER | ROLE_ADMIN.
export function registerUser(payload) {
  return axiosClient.post('/auth/register', payload).then((res) => res.data);
}

// POST /auth/login -> 200 OK, body: JwtResponse { token, type, userId, username, role }
// Body must match LoginRequest: { username, password }
export function loginUser(payload) {
  return axiosClient.post('/auth/login', payload).then((res) => res.data);
}

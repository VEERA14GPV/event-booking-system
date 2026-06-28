import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import { useToast } from '../components/Toast';
import { extractErrorMessage } from '../utils/helpers';
import { ROLES } from '../utils/constants';
import { registerUser } from '../api/authApi';

export default function Register() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const [submitError, setSubmitError] = useState(null);

  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm({
    defaultValues: { role: ROLES.USER },
  });

  if (isAuthenticated) return <Navigate to="/" replace />;

  const password = watch('password');

  const onSubmit = async (values) => {
    setSubmitError(null);
    try {
      await registerUser({ username: values.username, email: values.email, password: values.password, role: values.role });
      toast.success('Account created — please sign in.');
      navigate('/login', { replace: true });
    } catch (err) {
      setSubmitError(extractErrorMessage(err));
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-brand-50 via-white to-indigo-50 p-4">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="mb-8 text-center">
          <Link to="/" className="inline-flex items-center gap-2 text-brand-700">
            <span className="flex h-10 w-10 items-center justify-center rounded-2xl bg-brand-600 text-xl text-white shadow-lg">🎟</span>
            <span className="text-2xl font-extrabold">EventBook</span>
          </Link>
          <p className="mt-2 text-sm text-gray-500">Create your free account</p>
        </div>

        <div className="rounded-2xl border border-gray-100 bg-white p-8 shadow-xl shadow-gray-100">
          <h1 className="text-xl font-bold text-gray-900">Join EventBook</h1>
          <p className="mt-1 text-sm text-gray-500">Book events or start organizing your own.</p>

          <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
            <div>
              <label className="mb-1.5 block text-sm font-semibold text-gray-700">Username</label>
              <input
                {...register('username', { required: 'Username is required' })}
                className="input"
                placeholder="your_username"
                autoComplete="username"
              />
              {errors.username && <p className="mt-1 text-xs text-red-600">{errors.username.message}</p>}
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-semibold text-gray-700">Email</label>
              <input
                type="email"
                {...register('email', {
                  required: 'Email is required',
                  pattern: { value: /^\S+@\S+\.\S+$/, message: 'Invalid email format' },
                })}
                className="input"
                placeholder="you@example.com"
                autoComplete="email"
              />
              {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>}
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-semibold text-gray-700">Password</label>
              <input
                type="password"
                {...register('password', {
                  required: 'Password is required',
                  minLength: { value: 6, message: 'Minimum 6 characters' },
                })}
                className="input"
                placeholder="Min 6 characters"
                autoComplete="new-password"
              />
              {errors.password && <p className="mt-1 text-xs text-red-600">{errors.password.message}</p>}
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-semibold text-gray-700">Confirm password</label>
              <input
                type="password"
                {...register('confirmPassword', {
                  required: 'Please confirm your password',
                  validate: (v) => v === password || 'Passwords do not match',
                })}
                className="input"
                placeholder="Repeat your password"
                autoComplete="new-password"
              />
              {errors.confirmPassword && <p className="mt-1 text-xs text-red-600">{errors.confirmPassword.message}</p>}
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-semibold text-gray-700">I am a…</label>
              <div className="grid grid-cols-2 gap-3">
                <RoleOption value={ROLES.USER} label="Attendee" icon="🎫" desc="Book events" register={register} watch={watch} />
                <RoleOption value={ROLES.ORGANIZER} label="Organizer" icon="🎪" desc="Create events" register={register} watch={watch} />
              </div>
            </div>

            {submitError && (
              <div className="flex items-start gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                <span>⚠️</span> {submitError}
              </div>
            )}

            <button type="submit" disabled={isSubmitting} className="btn-primary w-full py-3 text-base">
              {isSubmitting ? 'Creating account…' : 'Create account'}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-gray-500">
            Already have an account?{' '}
            <Link to="/login" className="font-semibold text-brand-700 hover:underline">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

function RoleOption({ value, label, icon, desc, register, watch }) {
  const selected = watch('role') === value;
  return (
    <label className={`flex cursor-pointer flex-col rounded-xl border-2 p-3 transition-all ${
      selected ? 'border-brand-500 bg-brand-50' : 'border-gray-200 hover:border-gray-300'
    }`}>
      <input type="radio" value={value} {...register('role')} className="sr-only" />
      <span className="text-xl mb-1">{icon}</span>
      <span className={`text-sm font-bold ${selected ? 'text-brand-700' : 'text-gray-800'}`}>{label}</span>
      <span className="text-xs text-gray-500">{desc}</span>
    </label>
  );
}

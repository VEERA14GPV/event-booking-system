import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useToast } from '../components/Toast';
import Spinner from '../components/Spinner';
import { getEventById, updateEvent } from '../api/eventApi';
import { extractErrorMessage } from '../utils/helpers';
import { EVENT_TYPES } from '../utils/constants';

export default function EditEvent() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [eventName, setEventName] = useState('');

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm();

  useEffect(() => {
    let cancelled = false;
    getEventById(eventId)
      .then((event) => {
        if (cancelled) return;
        setEventName(event.name);
        reset({ name: event.name, description: event.description, city: event.city, venue: event.venue,
          language: event.language, type: event.type, rating: event.rating, price: event.price });
      })
      .catch((err) => { if (!cancelled) setError(extractErrorMessage(err)); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [eventId, reset]);

  const onSubmit = async (values) => {
    try {
      await updateEvent(eventId, { ...values, rating: Number(values.rating), price: Number(values.price) });
      toast.success('Event updated successfully.');
      navigate('/organizer', { replace: true });
    } catch (err) {
      toast.error(extractErrorMessage(err));
    }
  };

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>;
  if (error) return (
    <div className="mx-auto max-w-2xl">
      <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-4 text-sm text-red-700">⚠️ {error}</div>
      <Link to="/organizer" className="btn-secondary mt-4 inline-flex">← Back to events</Link>
    </div>
  );

  return (
    <div className="mx-auto max-w-2xl">
      <div className="mb-4">
        <Link to="/organizer" className="text-sm text-brand-600 hover:underline">← My events</Link>
      </div>
      <div className="rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
        <div className="mb-6 flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-amber-100 text-xl">✏️</div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">{eventName || `Event #${eventId}`}</h1>
            <p className="text-sm text-gray-500">Update event details below.</p>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <Field label="Event name" error={errors.name}>
            <input {...register('name', { required: 'Name is required' })} className="input" />
          </Field>

          <Field label="Description" error={errors.description}>
            <textarea rows={4} {...register('description', { required: 'Description is required' })} className="input resize-none" />
          </Field>

          <div className="grid grid-cols-2 gap-4">
            <Field label="City" error={errors.city}>
              <input {...register('city', { required: 'City is required' })} className="input" />
            </Field>
            <Field label="Venue" error={errors.venue}>
              <input {...register('venue', { required: 'Venue is required' })} className="input" />
            </Field>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Field label="Language" error={errors.language}>
              <input {...register('language', { required: 'Language is required' })} className="input" />
            </Field>
            <Field label="Category" error={errors.type}>
              <select {...register('type', { required: true })} className="input">
                {EVENT_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </Field>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Field label="Rating (0–5)" error={errors.rating}>
              <input type="number" step="0.1" min="0" max="5"
                {...register('rating', { required: true, min: 0, max: 5 })} className="input" />
            </Field>
            <Field label="Base price (₹)" error={errors.price}>
              <input type="number" step="0.01" min="0"
                {...register('price', { required: true, min: 0 })} className="input" />
            </Field>
          </div>

          <div className="flex gap-3 pt-2">
            <Link to="/organizer" className="btn-secondary flex-1 justify-center">Cancel</Link>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? 'Saving…' : '💾 Save changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function Field({ label, error, children }) {
  return (
    <div>
      <label className="mb-1.5 block text-sm font-semibold text-gray-700">{label}</label>
      {children}
      {error && <p className="mt-1 text-xs text-red-600">{error.message || 'Required'}</p>}
    </div>
  );
}

import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../components/Toast';
import { createEvent } from '../api/eventApi';
import { extractErrorMessage } from '../utils/helpers';
import { EVENT_TYPES } from '../utils/constants';

export default function CreateEvent() {
  const navigate = useNavigate();
  const toast = useToast();

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    defaultValues: { type: EVENT_TYPES[0], rating: 0, price: 0 },
  });

  const onSubmit = async (values) => {
    try {
      const result = await createEvent({
        name: values.name,
        description: values.description,
        city: values.city,
        venue: values.venue,
        language: values.language,
        type: values.type,
        rating: Number(values.rating),
        price: Number(values.price),
      });
      toast.success('Event created! Now add showtimes.');
      navigate(`/organizer/events/${result.eventId}/shows/new`, { replace: true });
    } catch (err) {
      toast.error(extractErrorMessage(err));
    }
  };

  return (
    <div className="mx-auto max-w-2xl">
      <div className="rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
        <div className="mb-6 flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-brand-100 text-xl">🎪</div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">Create event</h1>
            <p className="text-sm text-gray-500">Fill in details, then add showtimes.</p>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <Field label="Event name" error={errors.name}>
            <input {...register('name', { required: 'Name is required' })} className="input" placeholder="e.g. Coldplay World Tour" />
          </Field>

          <Field label="Description" error={errors.description}>
            <textarea
              rows={4}
              {...register('description', { required: 'Description is required' })}
              className="input resize-none"
              placeholder="Tell attendees what this event is about…"
            />
          </Field>

          <div className="grid grid-cols-2 gap-4">
            <Field label="City" error={errors.city}>
              <input {...register('city', { required: 'City is required' })} className="input" placeholder="Chennai" />
            </Field>
            <Field label="Venue" error={errors.venue}>
              <input {...register('venue', { required: 'Venue is required' })} className="input" placeholder="YMCA Ground" />
            </Field>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Field label="Language" error={errors.language}>
              <input {...register('language', { required: 'Language is required' })} className="input" placeholder="English" />
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
                {...register('price', { required: true, min: 0 })} className="input" placeholder="0.00" />
            </Field>
          </div>

          <button type="submit" disabled={isSubmitting} className="btn-primary w-full justify-center py-3">
            {isSubmitting ? 'Creating…' : '✅ Create event'}
          </button>
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

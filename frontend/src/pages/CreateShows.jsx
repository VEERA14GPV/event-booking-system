import { useFieldArray, useForm } from 'react-hook-form';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useToast } from '../components/Toast';
import { createShows } from '../api/showApi';
import { extractErrorMessage } from '../utils/helpers';

const SEAT_TYPES = ['STANDARD', 'PREMIUM', 'VIP'];
const minDateTime = () => new Date(Date.now() + 60_000).toISOString().slice(0, 16);

// Separate component so useFieldArray can be called per-showtime without
// violating the rules of hooks (can't call hooks inside a .map()).
function SeatLayoutBuilder({ control, register, errors, showIndex }) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: `shows.${showIndex}.rows`,
  });

  return (
    <div className="mt-4 border-t border-gray-200 pt-4">
      <div className="mb-3 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-xs font-bold uppercase tracking-wider text-gray-500">Seat Layout</span>
          {fields.length > 0 && (
            <span className="rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-semibold text-emerald-700">
              {fields.length} row{fields.length !== 1 ? 's' : ''}
            </span>
          )}
        </div>
        <button
          type="button"
          onClick={() => append({ letter: '', count: 10, type: 'STANDARD' })}
          className="rounded-lg border border-dashed border-emerald-300 px-2.5 py-1 text-xs font-semibold text-emerald-600 hover:bg-emerald-50 transition-colors"
        >
          + Add row
        </button>
      </div>

      {fields.length === 0 ? (
        <p className="rounded-lg bg-gray-100 py-3 text-center text-xs text-gray-400 italic">
          No seating configured. Click "Add row" to set up seats (optional).
        </p>
      ) : (
        <>
          <div className="mb-1 grid grid-cols-[3rem_1fr_1fr_1.5rem] gap-2 px-1">
            <span className="text-xs font-semibold text-gray-400">Row</span>
            <span className="text-xs font-semibold text-gray-400">Seats</span>
            <span className="text-xs font-semibold text-gray-400">Type</span>
            <span />
          </div>
          <div className="space-y-2">
            {fields.map((rowField, rowIndex) => (
              <div key={rowField.id} className="grid grid-cols-[3rem_1fr_1fr_1.5rem] items-center gap-2">
                <input
                  type="text"
                  maxLength={2}
                  placeholder="A"
                  className="input text-center text-sm font-bold uppercase"
                  {...register(`shows.${showIndex}.rows.${rowIndex}.letter`, {
                    required: true,
                    pattern: /^[A-Za-z]{1,2}$/,
                  })}
                />
                <input
                  type="number"
                  min="1"
                  max="50"
                  placeholder="10"
                  className="input text-sm"
                  {...register(`shows.${showIndex}.rows.${rowIndex}.count`, {
                    required: true,
                    min: 1,
                    max: 50,
                    valueAsNumber: true,
                  })}
                />
                <select
                  className="input text-sm"
                  {...register(`shows.${showIndex}.rows.${rowIndex}.type`)}
                >
                  {SEAT_TYPES.map((t) => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={() => remove(rowIndex)}
                  className="flex h-7 w-6 items-center justify-center rounded text-gray-400 hover:bg-red-50 hover:text-red-500 transition-colors"
                  title="Remove row"
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
          <p className="mt-2 text-xs text-gray-400">
            Seats are auto-named: row A with 5 seats → A1, A2, A3, A4, A5
          </p>
        </>
      )}
    </div>
  );
}

export default function CreateShows() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const toast = useToast();

  const { register, control, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    defaultValues: { shows: [{ startTime: '', price: 0, rows: [] }] },
  });

  const { fields, append, remove } = useFieldArray({ control, name: 'shows' });

  const onSubmit = async (values) => {
    try {
      const payload = values.shows.map((show) => {
        const seats = (show.rows || []).flatMap((row) => {
          const letter = (row.letter || '').trim().toUpperCase();
          const count = Number(row.count) || 0;
          if (!letter || count < 1) return [];
          return Array.from({ length: count }, (_, i) => ({
            seatNumber: `${letter}${i + 1}`,
            seatType: row.type || 'STANDARD',
          }));
        });
        return {
          eventId: Number(eventId),
          startTime: show.startTime,
          price: Number(show.price),
          seats,
        };
      });

      await createShows(payload);
      const totalSeats = payload.reduce((sum, s) => sum + s.seats.length, 0);
      toast.success(
        `${values.shows.length} showtime(s) created` +
        (totalSeats > 0 ? ` with ${totalSeats} seats.` : '.')
      );
      navigate('/organizer', { replace: true });
    } catch (err) {
      toast.error(extractErrorMessage(err));
    }
  };

  return (
    <div className="mx-auto max-w-2xl">
      <div className="mb-4">
        <Link to="/organizer" className="text-sm text-brand-600 hover:underline">← My events</Link>
      </div>

      <div className="rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
        <div className="mb-6 flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-100 text-xl">📅</div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">Add showtimes</h1>
            <p className="text-sm text-gray-500">Schedule show dates and optionally configure seat rows.</p>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
          {fields.map((field, index) => (
            <div key={field.id} className="rounded-xl border border-gray-200 bg-gray-50 p-4">
              <div className="mb-3 flex items-center justify-between">
                <span className="text-sm font-semibold text-gray-700">Showtime {index + 1}</span>
                <button
                  type="button"
                  onClick={() => remove(index)}
                  disabled={fields.length === 1}
                  className="rounded-lg border border-gray-200 bg-white px-2.5 py-1 text-xs font-medium text-gray-500 hover:bg-red-50 hover:text-red-600 hover:border-red-200 disabled:cursor-not-allowed disabled:opacity-40 transition-colors"
                >
                  Remove
                </button>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="mb-1.5 block text-sm font-semibold text-gray-700">Start time</label>
                  <input
                    type="datetime-local"
                    min={minDateTime()}
                    className="input"
                    {...register(`shows.${index}.startTime`, {
                      required: 'Start time is required',
                      validate: (v) => new Date(v) > new Date() || 'Must be a future date and time',
                    })}
                  />
                  {errors.shows?.[index]?.startTime && (
                    <p className="mt-1 text-xs text-red-600">{errors.shows[index].startTime.message}</p>
                  )}
                </div>

                <div>
                  <label className="mb-1.5 block text-sm font-semibold text-gray-700">Price (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    className="input"
                    placeholder="0.00"
                    {...register(`shows.${index}.price`, {
                      required: 'Price is required',
                      min: { value: 0, message: 'Price cannot be negative' },
                    })}
                  />
                  {errors.shows?.[index]?.price && (
                    <p className="mt-1 text-xs text-red-600">{errors.shows[index].price.message}</p>
                  )}
                </div>
              </div>

              <SeatLayoutBuilder
                control={control}
                register={register}
                errors={errors}
                showIndex={index}
              />
            </div>
          ))}

          <button
            type="button"
            onClick={() => append({ startTime: '', price: 0, rows: [] })}
            className="flex w-full items-center justify-center gap-2 rounded-xl border border-dashed border-brand-300 py-3 text-sm font-semibold text-brand-600 hover:bg-brand-50 transition-colors"
          >
            + Add another showtime
          </button>

          <div className="flex gap-3 pt-2">
            <Link to="/organizer" className="btn-secondary flex-1 justify-center">Cancel</Link>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? 'Creating…' : `✅ Create ${fields.length} show${fields.length > 1 ? 's' : ''}`}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

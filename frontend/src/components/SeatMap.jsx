import { classNames } from '../utils/helpers';
import { SEAT_STATUS } from '../utils/constants';

const STATUS_CLASSES = {
  [SEAT_STATUS.AVAILABLE]: 'border-gray-300 bg-white text-gray-600 hover:border-brand-500 hover:bg-brand-50 hover:text-brand-700 cursor-pointer shadow-sm',
  [SEAT_STATUS.LOCKED]:    'border-amber-300 bg-amber-50 text-amber-600 cursor-not-allowed',
  [SEAT_STATUS.BOOKED]:    'border-red-200 bg-red-50 text-red-400 cursor-not-allowed',
  SELECTED:                'border-brand-500 bg-brand-600 text-white shadow-md shadow-brand-200',
};

export default function SeatMap({ seats, selectedSeatIds, onToggle, liveStatus = {} }) {
  if (!seats || seats.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-12 text-center">
        <span className="text-4xl mb-3">🪑</span>
        <p className="text-sm font-medium text-gray-600">No seats configured for this show.</p>
      </div>
    );
  }

  const grouped = seats.reduce((acc, seat) => {
    const row = (seat.seatNumber?.charAt(0) || '?').toUpperCase();
    (acc[row] ??= []).push(seat);
    return acc;
  }, {});

  return (
    <div className="space-y-6">
      {/* Screen indicator */}
      <div className="flex flex-col items-center gap-1">
        <div className="h-1.5 w-2/3 rounded-full bg-gradient-to-r from-transparent via-brand-400 to-transparent opacity-60" />
        <p className="text-[11px] font-semibold uppercase tracking-widest text-gray-400">Screen / Stage</p>
      </div>

      {Object.entries(grouped).sort(([a], [b]) => a.localeCompare(b)).map(([row, rowSeats]) => (
        <div key={row}>
          <div className="mb-3 flex items-center gap-2">
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500">Row {row}</span>
            <span className="h-px flex-1 bg-gray-100" />
            <span className="text-xs text-gray-400">
              {rowSeats.filter(s => (liveStatus[s.id] ?? s.status) === SEAT_STATUS.AVAILABLE).length} available
            </span>
          </div>
          <div className="flex flex-wrap gap-2">
            {rowSeats.map((seat) => {
              const effectiveStatus = liveStatus[seat.id] ?? seat.status;
              const isSelected = selectedSeatIds.includes(seat.id);
              const isDisabled = !isSelected && effectiveStatus !== SEAT_STATUS.AVAILABLE;

              return (
                <button
                  key={seat.id}
                  type="button"
                  disabled={isDisabled}
                  onClick={() => onToggle(seat)}
                  title={`${seat.seatNumber} — ${effectiveStatus}`}
                  className={classNames(
                    'flex h-10 w-12 items-center justify-center rounded-lg border text-xs font-semibold transition-all',
                    isSelected ? STATUS_CLASSES.SELECTED : STATUS_CLASSES[effectiveStatus]
                  )}
                >
                  {seat.seatNumber}
                </button>
              );
            })}
          </div>
        </div>
      ))}

      {/* Legend */}
      <div className="flex flex-wrap gap-4 border-t border-gray-100 pt-4">
        <LegendItem swatch="border-gray-300 bg-white" label="Available" />
        <LegendItem swatch="border-brand-500 bg-brand-600" label="Selected" />
        <LegendItem swatch="border-amber-300 bg-amber-50" label="Locked by others" />
        <LegendItem swatch="border-red-200 bg-red-50" label="Booked" />
      </div>
    </div>
  );
}

function LegendItem({ swatch, label }) {
  return (
    <span className="flex items-center gap-1.5 text-xs text-gray-500">
      <span className={classNames('h-3.5 w-3.5 rounded border', swatch)} />
      {label}
    </span>
  );
}

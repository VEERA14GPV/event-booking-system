import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { classNames } from '../utils/helpers';

const ToastContext = createContext(undefined);

const VARIANTS = {
  success: { cls: 'bg-emerald-600 shadow-emerald-200', icon: '✅' },
  error:   { cls: 'bg-red-600 shadow-red-200',         icon: '❌' },
  info:    { cls: 'bg-brand-600 shadow-brand-200',     icon: 'ℹ️' },
  warning: { cls: 'bg-amber-500 shadow-amber-200',     icon: '⚠️' },
};

let idCounter = 0;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const remove = useCallback((id) => setToasts((prev) => prev.filter((t) => t.id !== id)), []);

  const push = useCallback(
    (message, variant = 'info', duration = 4000) => {
      const id = ++idCounter;
      setToasts((prev) => [...prev, { id, message, variant }]);
      if (duration > 0) setTimeout(() => remove(id), duration);
      return id;
    },
    [remove]
  );

  const api = useMemo(
    () => ({
      push,
      success: (msg, dur) => push(msg, 'success', dur),
      error:   (msg, dur) => push(msg, 'error', dur),
      info:    (msg, dur) => push(msg, 'info', dur),
      warning: (msg, dur) => push(msg, 'warning', dur),
      remove,
    }),
    [push, remove]
  );

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="fixed bottom-5 right-5 z-50 flex w-80 max-w-[90vw] flex-col gap-2.5">
        {toasts.map((t) => {
          const v = VARIANTS[t.variant] ?? VARIANTS.info;
          return (
            <div
              key={t.id}
              role="alert"
              className={classNames(
                'animate-[slideUp_0.2s_ease-out] flex items-start gap-3 rounded-2xl px-4 py-3.5 text-sm font-medium text-white shadow-lg',
                v.cls
              )}
            >
              <span className="text-base shrink-0">{v.icon}</span>
              <span className="flex-1 leading-snug">{t.message}</span>
              <button
                onClick={() => remove(t.id)}
                className="shrink-0 rounded-full p-0.5 text-white/70 hover:text-white transition-colors"
                aria-label="Dismiss"
              >
                ✕
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (ctx === undefined) throw new Error('useToast must be used within a ToastProvider');
  return ctx;
}

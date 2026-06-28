import { useCallback, useState } from 'react';
import { extractErrorMessage } from '../utils/helpers';

// Generic async-call wrapper: tracks loading/error state and returns the
// resolved data, so pages don't all hand-roll the same try/catch/finally.
//
//   const { run, loading, error, data } = useApi(getEventById);
//   useEffect(() => { run(eventId); }, [eventId]);
export default function useApi(apiFn) {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const run = useCallback(
    async (...args) => {
      setLoading(true);
      setError(null);
      try {
        const result = await apiFn(...args);
        setData(result);
        return result;
      } catch (err) {
        const message = extractErrorMessage(err);
        setError(message);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [apiFn]
  );

  return { run, data, error, loading, setData };
}

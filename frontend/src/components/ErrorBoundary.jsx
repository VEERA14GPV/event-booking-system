import { Component } from 'react';

// Class component is required here — React error boundaries (componentDidCatch
// / getDerivedStateFromError) have no hooks equivalent.
export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error('Unhandled UI error:', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-[60vh] flex-col items-center justify-center gap-4 p-6 text-center">
          <h2 className="text-xl font-semibold text-gray-900">Something went wrong</h2>
          <p className="max-w-md text-sm text-gray-500">
            An unexpected error occurred while rendering this page. Try reloading, and if the
            problem persists, contact support.
          </p>
          <button
            onClick={() => window.location.reload()}
            className="rounded-md bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700"
          >
            Reload page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

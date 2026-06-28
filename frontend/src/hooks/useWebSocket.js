// Re-export so components can `import useWebSocket from '../hooks/useWebSocket'`
// per the project's folder layout. The actual implementation lives next to
// the rest of the websocket module since it is tightly coupled to it.
export { default } from '../websocket/useWebSocket';

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': 'http://localhost:8082',
      '/events': 'http://localhost:8082',
      '/shows': 'http://localhost:8082',
      '/seats': 'http://localhost:8082',
      '/bookings': 'http://localhost:8082',
      '/payments': 'http://localhost:8082',
      '/actuator': 'http://localhost:8082',
      '/socket': {
        target: 'http://localhost:8082',
        ws: true,
      },
    },
  },
})

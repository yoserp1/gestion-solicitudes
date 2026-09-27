import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api/v1/indicadores': 'http://localhost:8082',
      '/api': 'http://localhost:8081',
    },
  },
})

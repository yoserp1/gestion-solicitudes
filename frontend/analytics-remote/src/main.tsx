import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { StandaloneApp } from './StandaloneApp'

const theme = createTheme({
  palette: { primary: { main: '#175c4c' }, secondary: { main: '#d76b39' }, background: { default: '#f4f6f3' } },
  shape: { borderRadius: 8 },
  typography: { fontFamily: '"IBM Plex Sans", "Segoe UI", sans-serif' },
})

createRoot(document.getElementById('root')!).render(
  <StrictMode><ThemeProvider theme={theme}><CssBaseline /><StandaloneApp /></ThemeProvider></StrictMode>,
)

import { createTheme } from '@mui/material/styles'

export const theme = createTheme({
  palette: {
    primary: { main: '#175c4c', dark: '#0f4539', contrastText: '#ffffff' },
    secondary: { main: '#d76b39', dark: '#a94720' },
    background: { default: '#f4f6f3', paper: '#ffffff' },
    text: { primary: '#18211e', secondary: '#52605b' },
  },
  shape: { borderRadius: 8 },
  typography: {
    fontFamily: '"IBM Plex Sans", "Segoe UI", sans-serif',
    h1: { fontWeight: 750, fontSize: 'clamp(1.75rem, 4vw, 2.5rem)', letterSpacing: 0 },
    h2: { fontWeight: 700, letterSpacing: 0 },
    button: { textTransform: 'none', fontWeight: 700, letterSpacing: 0 },
  },
  components: {
    MuiButtonBase: { defaultProps: { disableRipple: false } },
    MuiButton: { styleOverrides: { root: { minHeight: 42 } } },
    MuiOutlinedInput: { styleOverrides: { root: { backgroundColor: '#ffffff' } } },
  },
})

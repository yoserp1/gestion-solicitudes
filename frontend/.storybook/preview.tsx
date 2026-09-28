import { CssBaseline, ThemeProvider } from '@mui/material'
import type { Preview } from '@storybook/react-webpack5'
import { theme } from '../src/theme'

const preview: Preview = {
  decorators: [
    (Story) => <ThemeProvider theme={theme}><CssBaseline /><Story /></ThemeProvider>,
  ],
  parameters: {
    layout: 'padded',
    a11y: { test: 'error' },
  },
}

export default preview

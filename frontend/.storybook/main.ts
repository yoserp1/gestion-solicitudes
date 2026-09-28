import type { StorybookConfig } from '@storybook/react-webpack5'

const config: StorybookConfig = {
  stories: ['../src/**/*.stories.@(ts|tsx)'],
  addons: ['@storybook/addon-docs', '@storybook/addon-a11y'],
  framework: { name: '@storybook/react-webpack5', options: {} },
  docs: { autodocs: 'tag' },
  webpackFinal: async (config) => {
    config.module?.rules?.push({
      test: /\.tsx?$/,
      exclude: /node_modules/,
      use: [{
        loader: 'ts-loader',
        options: { transpileOnly: true, compilerOptions: { jsx: 'react-jsx', module: 'esnext' } },
      }],
    })
    config.resolve?.extensions?.push('.ts', '.tsx')
    return config
  },
}

export default config

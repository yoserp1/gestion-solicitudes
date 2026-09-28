import { ModuleFederationPlugin } from '@module-federation/enhanced/rspack'
import { rspack } from '@rspack/core'
import path from 'node:path'

const isProduction = process.env.NODE_ENV === 'production'

export default {
  mode: isProduction ? 'production' : 'development',
  entry: './src/bootstrap.ts',
  output: {
    path: path.resolve(import.meta.dirname, 'dist'),
    publicPath: 'auto',
    clean: true,
  },
  resolve: { extensions: ['.tsx', '.ts', '.jsx', '.js'] },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: [{ loader: 'builtin:swc-loader', options: { jsc: { parser: { syntax: 'typescript', tsx: true }, transform: { react: { runtime: 'automatic' } } } } }],
      },
    ],
  },
  plugins: [
    new rspack.HtmlRspackPlugin({ template: './index.html' }),
    new ModuleFederationPlugin({
      name: 'analytics',
      dts: false,
      filename: 'remoteEntry.js',
      exposes: { './IndicatorsApp': './src/IndicatorsApp.tsx' },
      shared: {
        react: { singleton: true, requiredVersion: '^19.2.0' },
        'react-dom': { singleton: true, requiredVersion: '^19.2.0' },
        '@emotion/react': { singleton: true },
        '@emotion/styled': { singleton: true },
        '@mui/material': { singleton: true, requiredVersion: '^7.3.7' },
      },
    }),
  ],
  devServer: {
    port: 3001,
    headers: { 'Access-Control-Allow-Origin': '*' },
    proxy: [
      { context: ['/api/v1/indicadores'], target: 'http://localhost:8082' },
      { context: ['/api'], target: 'http://localhost:8081' },
    ],
  },
}

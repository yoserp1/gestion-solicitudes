import type { Meta, StoryObj } from '@storybook/react-webpack5'
import { RequestState } from './RequestState'

const meta = {
  title: 'Common/RequestState',
  component: RequestState,
  args: { loading: false, error: null, notice: null, onRetry: () => undefined },
  tags: ['autodocs'],
} satisfies Meta<typeof RequestState>

export default meta
type Story = StoryObj<typeof meta>

export const Loading: Story = { args: { loading: true } }
export const Success: Story = { args: { notice: 'Solicitud registrada correctamente.' } }
export const Error: Story = { args: { error: 'No fue posible conectar con el servicio.' } }
export const Forbidden: Story = { args: { error: 'Autorización insuficiente para esta operación.' } }

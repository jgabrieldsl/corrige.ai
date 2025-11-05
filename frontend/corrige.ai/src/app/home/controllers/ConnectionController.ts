import { create } from 'zustand'
import type { ConnectionRequest, ConnectionResponse } from '../models'
import { ConnectionService } from '../services'
import { ApiService } from '@/shared/api'

interface IConnection {
  isConnecting: boolean
  connection: ConnectionResponse
  setConnection: (params: ConnectionRequest) => Promise<void>
  disconnect: () => Promise<void>
}

export const useConnectionController = create<IConnection>()((set, get) => {
  const api = new ApiService()
  const connectionService = new ConnectionService(api);
  return {
    isConnecting: false,
    connection: {} as ConnectionResponse,
    setConnection: async (params: ConnectionRequest) => {
      try {
        set({ isConnecting: true })

        const data = await connectionService.createConnection(params)

        set({ connection: data, isConnecting: false })

      } catch (error) {
        console.error('Um erro ocorreu')
        set({ isConnecting: false })
      }
    },
    disconnect: async () => {
      try {
        const { connection } = get()
        if (connection?.dados?.socketId) {
          await connectionService.disconnect(connection.dados.socketId)
          set({ connection: {} as ConnectionResponse })
        }
      } catch (error) {
        console.error('Erro ao desconectar', error)
      }
    }
  }
})

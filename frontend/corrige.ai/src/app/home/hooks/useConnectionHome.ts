import { useConnectionController } from '../controllers'
import type { ConnectionRequest } from '../models'

export const useConnectionHome = () => {
  const { connection, isConnecting, currentUserId, setConnection, disconnect } = useConnectionController()

  const connectionPayload: ConnectionRequest = {
    tipo: 'CONNECT',
    dados: {
      userId: Math.random().toString(36).substring(2, 15),
      userType: 'STUDENT',
      authToken: crypto.randomUUID()
    }
  }

  const handleConnect = async () => await setConnection(connectionPayload)
  const handleDisconnect = async () => await disconnect()

  return { connection, isConnecting, currentUserId, handleConnect, handleDisconnect }
}
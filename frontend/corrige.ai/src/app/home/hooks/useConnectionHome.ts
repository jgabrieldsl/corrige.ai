import { useConnectionController } from '../controllers'
import type { ConnectionRequest } from '../models'

export const useConnectionHome = () => {
  const { connection, isConnecting, setConnection, disconnect } = useConnectionController()

  const connectionPayload: ConnectionRequest = {
    tipo: 'CONNECT',
    dados: {
      userId: 'abc123',
      userType: 'STUDENT',
      authToken: 'token-xyz'
    }
  }

  const handleConnect = async () => await setConnection(connectionPayload)
  const handleDisconnect = async () => await disconnect()

  return { connection, isConnecting, handleConnect, handleDisconnect }
}
import { useConnectionController } from '../controllers'
import type { ConnectionRequest } from '../models'

export const useConnectionHome = () => {
  const { connection, isConnecting, setConnection } = useConnectionController()

  const connectionPayload: ConnectionRequest = {
    tipo: 'CONNECT',
    dados: {
      userId: 'abc123',
      userType: 'STUDENT',
      authToken: 'token-xyz'
    }
  }

  const handleConnect = async () => await setConnection(connectionPayload)

  return { connection, isConnecting, handleConnect }
}
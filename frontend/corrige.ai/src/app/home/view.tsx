import { useConnectionHome } from './hooks'
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/components/ui/card"
import { Button } from "@/shared/components/ui/button"
import { Alert, AlertDescription } from "@/shared/components/ui/alert"

export const Home = () => {
  const { connection, isConnecting, handleConnect, handleDisconnect } = useConnectionHome()

  const isConnected = connection?.dados?.socketId

  return (
    <main className="h-screen w-screen flex items-center justify-center">
      <Card className="w-full max-w-2xs mx-8">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">
            Teste inicial da API
          </CardTitle>
        </CardHeader>
        
        <CardContent className="space-y-4">
          {isConnecting && (
            <Alert>
              <AlertDescription className="text-black text-center">
                Estabelecendo conexão...
              </AlertDescription>
            </Alert>
          )}
          
          {isConnected && (
            <Card className="bg-green-50 border-green-200">
              <CardHeader>
                <CardTitle className="text-green-700 text-lg text-center">
                  Conexão estabelecida!
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                <p className="text-sm text-green-600 text-center">
                  Socket ID: {connection.dados.socketId}
                </p>
                <p className="text-sm text-green-600 text-center">
                  Timestamp: {new Date(connection.dados.timestamp).toLocaleString()}
                </p>
                <p className="text-sm text-green-600 text-center font-semibold">
                  Usuários conectados: {connection.dados.totalUsuarios}
                </p>
              </CardContent>
            </Card>
          )}

          <Button 
            onClick={handleConnect}
            disabled={isConnecting || !!isConnected}
            variant={"default"}
            className="w-full"
          >
            {isConnecting ? 'Conectando...' : isConnected ? 'Conectado' : 'Conectar'}
          </Button>

          {isConnected && (
            <Button 
              onClick={handleDisconnect}
              variant={"destructive"}
              className="w-full"
            >
              Desconectar
            </Button>
          )}
        </CardContent>
      </Card>
    </main>
  )
}

export default Home

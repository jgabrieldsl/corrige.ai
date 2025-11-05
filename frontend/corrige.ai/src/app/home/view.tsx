import { useState } from 'react'
import { useConnectionHome, useChatHome } from './hooks'
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/components/ui/card"
import { Button } from "@/shared/components/ui/button"
import { Alert, AlertDescription } from "@/shared/components/ui/alert"
import { ExpandableChat, ExpandableChatBody, ExpandableChatFooter, ExpandableChatHeader } from '@/components/ui/expandable-chat'
import { Input } from '@/shared/components/ui/input'
import { Paperclip, Send } from 'lucide-react'

export const Home = () => {
  const { connection, isConnecting, currentUserId, handleConnect, handleDisconnect } = useConnectionHome()
  const [messageInput, setMessageInput] = useState('')

  const isConnected = connection?.dados?.socketId
  const { messages, sendMessage } = useChatHome(isConnected || null, currentUserId)

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault()
    if (messageInput.trim()) {
      await sendMessage(messageInput)
      setMessageInput('')
    }
  }

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
      <ExpandableChat>
        <ExpandableChatHeader>
          <div className="flex items-center gap-4">
            <div className="flex flex-col">
              <h4 className="text-lg font-medium">Corrige AI Chat</h4>
              <p className="text-sm text-muted-foreground">
                {isConnected ? `${messages.length} mensagens` : 'Conecte-se para usar o chat'}
              </p>
            </div>
          </div>
        </ExpandableChatHeader>
        <ExpandableChatBody>
          <div className="p-4 space-y-4">
            {!isConnected && (
              <div className="text-center text-muted-foreground py-8">
                <p>Conecte-se para começar a usar o chat</p>
              </div>
            )}
            {messages.map((msg, index) => (
              <div 
                key={index} 
                className={`flex items-end gap-2 ${
                  msg.userId === currentUserId ? 'justify-end' : ''
                }`}
              >
                <div className={`${
                  msg.userId === currentUserId 
                    ? 'bg-primary text-primary-foreground' 
                    : 'bg-muted'
                  } p-3 rounded-lg max-w-[80%]`}
                >
                  <p className="text-xs font-semibold mb-1">
                    {msg.userId === currentUserId ? 'Você' : msg.userId}
                  </p>
                  <p>{msg.mensagem}</p>
                  <p className="text-xs opacity-70 mt-1">
                    {new Date(msg.timestamp).toLocaleTimeString()}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </ExpandableChatBody>
        <ExpandableChatFooter>
          <form onSubmit={handleSendMessage} className="flex items-center gap-2">
            <Button size="icon" variant="ghost" type="button">
              <Paperclip className="h-5 w-5" />
            </Button>
            <Input
              autoComplete="off"
              name="message"
              placeholder="Digite sua mensagem..."
              className="flex-1"
              value={messageInput}
              onChange={(e) => setMessageInput(e.target.value)}
              disabled={!isConnected}
            />
            <Button type="submit" disabled={!isConnected || !messageInput.trim()}>
              <Send className="h-5 w-5" />
            </Button>
          </form>
        </ExpandableChatFooter>
      </ExpandableChat>
    </main>
  )
}

export default Home

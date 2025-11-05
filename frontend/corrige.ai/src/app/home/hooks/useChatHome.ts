import { useEffect, useRef } from 'react'
import { useChatController } from '../controllers'

export const useChatHome = (socketId: string | null, currentUserId: string) => {
  const { messages, isConnected, setCurrentUserId, connectToChat, sendMessage, disconnectFromChat } = useChatController()
  const hasConnected = useRef(false)

  useEffect(() => {
    if (currentUserId) {
      setCurrentUserId(currentUserId)
    }
  }, [currentUserId])

  useEffect(() => {
    if (socketId && !hasConnected.current) {
      connectToChat(socketId)
      hasConnected.current = true
    }
    
    return () => {
      if (hasConnected.current) {
        disconnectFromChat()
        hasConnected.current = false
      }
    }
  }, [socketId])

  const handleSendMessage = async (mensagem: string) => {
    if (socketId && mensagem.trim()) {
      await sendMessage(socketId, mensagem)
    }
  }

  return {
    messages,
    isConnected,
    sendMessage: handleSendMessage
  }
}

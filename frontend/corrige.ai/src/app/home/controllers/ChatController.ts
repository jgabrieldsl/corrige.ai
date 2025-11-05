import { create } from 'zustand'
import type { ChatMessage } from '../models'
import { ChatService } from '../services'
import { ApiService } from '@/shared/api'

interface IChatController {
  messages: ChatMessage[]
  eventSource: EventSource | null
  isConnected: boolean
  currentUserId: string
  setCurrentUserId: (userId: string) => void
  connectToChat: (socketId: string) => void
  sendMessage: (socketId: string, mensagem: string) => Promise<void>
  disconnectFromChat: () => void
}

export const useChatController = create<IChatController>()((set, get) => {
  const api = new ApiService()
  const chatService = new ChatService(api)
  
  return {
    messages: [],
    eventSource: null,
    isConnected: false,
    currentUserId: '',
    
    setCurrentUserId: (userId: string) => {
      set({ currentUserId: userId })
    },
    
    connectToChat: (socketId: string) => {
      const { eventSource } = get()
      
      // Fecha conexão anterior se existir
      if (eventSource) {
        eventSource.close()
      }
      
      // Conecta ao stream SSE
      const newEventSource = chatService.connectToMessageStream(socketId, (message) => {
        const { currentUserId } = get()
        
        // Só adiciona se não for uma mensagem do próprio usuário
        // (evita duplicação já que mensagens próprias são adicionadas localmente)
        if (message.userId !== currentUserId) {
          set((state) => ({
            messages: [...state.messages, message]
          }))
        }
      })
      
      set({
        eventSource: newEventSource,
        isConnected: true
      })
    },
    
    sendMessage: async (socketId: string, mensagem: string) => {
      try {
        const { currentUserId } = get()
        
        // Adiciona a mensagem localmente primeiro (otimistic update)
        const localMessage: ChatMessage = {
          userId: currentUserId,
          userType: 'STUDENT',
          mensagem: mensagem,
          timestamp: Date.now()
        }
        
        set((state) => ({
          messages: [...state.messages, localMessage]
        }))
        
        // Envia para o servidor
        await chatService.sendMessage({ socketId, mensagem })
      } catch (error) {
        console.error('Erro ao enviar mensagem:', error)
        throw error
      }
    },
    
    disconnectFromChat: () => {
      const { eventSource } = get()
      
      if (eventSource) {
        eventSource.close()
      }
      
      set({
        eventSource: null,
        isConnected: false,
        messages: [],
        currentUserId: ''
      })
    }
  }
})

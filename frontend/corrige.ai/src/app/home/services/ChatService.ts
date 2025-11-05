import type { SendMessageRequest, ChatMessage } from '../models'
import type { ApiService } from '@/shared/api'

export class ChatService {
  private apiService: ApiService

  constructor(apiService: ApiService) {
    this.apiService = apiService
  }

  async sendMessage(request: SendMessageRequest): Promise<void> {
    try {
      await this.apiService.post('/api/chat/send', request)
    } catch (error) {
      throw error
    }
  }

  // Conecta ao stream SSE de mensagens
  connectToMessageStream(socketId: string, onMessage: (message: ChatMessage) => void): EventSource {
    const eventSource = new EventSource(`http://localhost:8080/api/chat/stream/${socketId}`)
    
    eventSource.addEventListener('chat-message', (event) => {
      const message: ChatMessage = JSON.parse(event.data)
      onMessage(message)
    })
    
    eventSource.onerror = (error) => {
      console.error('Erro no stream SSE:', error)
    }
    
    return eventSource
  }
}

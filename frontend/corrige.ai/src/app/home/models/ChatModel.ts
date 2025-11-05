export interface ChatMessage {
  userId: string
  userType: string
  mensagem: string
  timestamp: number
}

export interface SendMessageRequest {
  socketId: string
  mensagem: string
}

export interface ConnectionData {
  socketId: string
  timestamp: number
  totalUsuarios: number
}

export interface ConnectionResponse {
  tipo: string
  dados: ConnectionData
}

export interface ConnectionRequest {
  tipo: 'CONNECT'
  dados: {
    userId: string
    userType: 'STUDENT' | 'TEACHER'
    authToken: string
  }
}
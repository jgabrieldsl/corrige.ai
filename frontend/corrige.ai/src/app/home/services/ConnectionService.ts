import type { ConnectionResponse, ConnectionRequest } from '../models'
import type { ApiService } from '@/shared/api'


export class ConnectionService {
  private apiService: ApiService

  constructor(apiService: ApiService) {
    this.apiService = apiService
  }

  async createConnection(connectionData: ConnectionRequest): Promise<ConnectionResponse> {
    try {
      const data = await this.apiService.post<ConnectionResponse>(`/api/test`, connectionData)
      
      return data as ConnectionResponse
    } catch (error) {
      throw error
    }
  }
}

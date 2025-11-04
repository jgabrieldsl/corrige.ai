import axios from 'axios'
import type { AxiosResponse } from 'axios'

const headers = {
  'Content-Type': 'application/json',
}

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers,
})

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    return error
  }
)

export class ApiService {
  async get<T> (url: string, params?: any): Promise<T> {
    const response = await api.get<T>(url, { params })
    return response.data
  }

  async post<T> (url: string, data: any): Promise<T> {
    const response = await api.post<T>(url, data)
    return response.data
  }

  async put<T> (url: string, data: any): Promise<T> {
    const response = await api.put<T>(url, data)
    return response.data
  }

  async delete<T> (url: string, params?: any): Promise<T> {
    const response = await api.delete<T>(url, { params })
    return response.data
  }
}
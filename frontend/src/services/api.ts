import axios from 'axios'
import { useAuthStore } from '../stores/auth'

export const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.user?.token) {
    config.headers.Authorization = `Basic ${auth.user.token}`
  }
  return config
})
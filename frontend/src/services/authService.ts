import { api } from './api'
import type { SessionUser } from '../types'

export async function login(username: string, password: string) {
  const { data } = await api.post<SessionUser>('/auth/login', { username, password })
  return data
}

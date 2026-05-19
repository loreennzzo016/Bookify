import { api } from './api'
import type { User, UserPayload } from '../types'

export async function getUsers() {
  const { data } = await api.get<User[]>('/users')
  return data
}

export async function createUser(payload: UserPayload) {
  const { data } = await api.post<User>('/users', payload)
  return data
}

export async function updateUser(id: number, payload: UserPayload) {
  const { data } = await api.put<User>(`/users/${id}`, payload)
  return data
}

export async function deleteUser(id: number) {
  await api.delete(`/users/${id}`)
}

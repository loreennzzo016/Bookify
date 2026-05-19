import { api } from './api'
import type { Book, BookPayload } from '../types'

export async function getBooks() {
  const { data } = await api.get<Book[]>('/books')
  return data
}

export async function getBooksByUser(userId: number) {
  const { data } = await api.get<Book[]>(`/books/user/${userId}`)
  return data
}

export async function createBook(payload: BookPayload) {
  const { data } = await api.post<Book>('/books', toBookFormData(payload), {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return data
}

export async function updateBook(id: number, payload: BookPayload) {
  const { data } = await api.put<Book>(`/books/${id}`, toBookFormData(payload), {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return data
}

export async function deleteBook(id: number) {
  await api.delete(`/books/${id}`)
}

export async function assignBook(id: number, userId: number | null) {
  const { data } = await api.post<Book>(`/books/${id}/assign`, { userId })
  return data
}

export async function exportBooksCsv() {
  const { data } = await api.get<Blob>('/books/export/csv', { responseType: 'blob' })
  const url = URL.createObjectURL(data)
  const link = document.createElement('a')
  link.href = url
  link.download = 'books.csv'
  link.click()
  URL.revokeObjectURL(url)
}

function toBookFormData(payload: BookPayload) {
  const formData = new FormData()
  formData.append('title', payload.title)
  formData.append('author', payload.author)
  formData.append('year', String(payload.year))
  if (payload.thumbnail) {
    formData.append('thumbnail', payload.thumbnail)
  }
  return formData
}

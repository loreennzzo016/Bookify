export type Role = 'ADMIN' | 'USER'

export interface User {
  id: number
  username: string
  role: Role
}

export interface UserPayload {
  username: string
  password?: string
  role: Role
}

export interface Book {
  id: number
  title: string
  author: string
  year: number
  thumbnail: string | null
  assignedTo: User | null
}

export interface BookPayload {
  title: string
  author: string
  year: number
  thumbnail?: File | null
}

export interface SessionUser extends User {
  token: string
}

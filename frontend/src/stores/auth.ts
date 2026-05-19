import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { SessionUser } from '../types'
import { login as requestLogin } from '../services/authService'

const STORAGE_KEY = 'online-library-session'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<SessionUser | null>(loadSession())
  const isAuthenticated = computed(() => Boolean(user.value))
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function login(username: string, password: string) {
    user.value = await requestLogin(username, password)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user.value))
  }

  function logout() {
    user.value = null
    localStorage.removeItem(STORAGE_KEY)
  }

  return { user, isAuthenticated, isAdmin, login, logout }
})

function loadSession(): SessionUser | null {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as SessionUser
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return null
  }
}
